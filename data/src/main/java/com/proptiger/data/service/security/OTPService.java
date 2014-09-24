package com.proptiger.data.service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.app.config.security.AuthSuccessHandler;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.security.UserRole;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.user.UserOTP;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.repo.APIAccessLogDao;
import com.proptiger.data.repo.user.UserOTPDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.util.SecurityContextUtils;
import com.proptiger.exception.BadRequestException;

/**
 * Service class to handle generation/validation of one time password.
 * 
 * @author Rajeev Pandey
 *
 */
public class OTPService {

    @Autowired
    private APIAccessLogDao accessLogDao;

    @Autowired
    private MailSender      mailSender;

    @Autowired
    private UserOTPDao      userOTPDao;

    private OTPGenerator    generator = new OTPGenerator();
    
    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    public boolean isOTPRequired(Authentication auth) {
        ActiveUser activeUser = (ActiveUser) auth.getPrincipal();
        if (activeUser.getApplicationType().equals(Application.B2B)) {
            // check if user's IP is not white listed, then return true
            return true;
        }
        return false;
    }

    @Transactional
    public void respondWithOTP(ActiveUser activeUser) {
        int otp = generator.getRandomInt();
        UserOTP userOTP = new UserOTP();
        userOTP.setOtp(otp);
        userOTP.setUserId(activeUser.getUserIdentifier());
        userOTPDao.save(userOTP);
        /*
         * Mail template should be used
         */
        mailSender.sendMailUsingAws(new MailDetails(new MailBody().setBody("OTP-" + otp).setSubject(
                "One time password to login")).setMailTo(activeUser.getUsername()));

    }

    @Transactional
    public void validate(Integer otp, ActiveUser activeUser, HttpServletRequest request, HttpServletResponse response) {
        Pageable pageable = new LimitOffsetPageRequest(0, 1, Direction.DESC, "id");
        List<UserOTP> userOTPs = userOTPDao.findLatestOTPByUserId(activeUser.getUserIdentifier(), pageable);
        if (userOTPs.isEmpty() || otp == null) {
            throw new BadRequestException(ResponseCodes.OTP_REQUIRED, ResponseErrorMessages.WRONG_OTP);
        }
        Calendar cal = Calendar.getInstance();
        if (cal.getTime().after(userOTPs.get(0).getExpiresAt())) {
            userOTPDao.delete(userOTPs);
            /*
             * in case OTP expired re-send otp
             */
            respondWithOTP(activeUser);
            throw new BadRequestException(ResponseCodes.OTP_REQUIRED, ResponseErrorMessages.OTP_EXPIRED);
        }
        if (otp.equals(userOTPs.get(0).getOtp())) {
            grantAuthority();
            clearUserOTP(activeUser);
            try {
                authSuccessHandler.onAuthenticationSuccess(request, response, SecurityContextUtils.getAuthentication());
            }
            catch (ServletException | IOException e) {
                throw new BadRequestException(ResponseCodes.OTP_REQUIRED, "OTP validation failed");
            }
        }
        else {
            throw new BadRequestException(ResponseCodes.OTP_REQUIRED, ResponseErrorMessages.WRONG_OTP);
        }
    }

    private void clearUserOTP(ActiveUser activeUser) {
        userOTPDao.deleteByUserId(activeUser.getUserIdentifier());
    }

    /**
     * This method grants USER role to the currently logged in user after otp
     * validation
     */
    private void grantAuthority() {
        Authentication auth = SecurityContextUtils.getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                authorities);
        SecurityContextUtils.setAuthentication(newAuth);
    }
}
