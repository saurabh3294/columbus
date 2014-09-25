package com.proptiger.data.service.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.app.config.security.AuthSuccessHandler;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.enums.Application;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.CompanyIP;
import com.proptiger.data.model.CompanySubscription;
import com.proptiger.data.model.UserSubscriptionMapping;
import com.proptiger.data.model.user.UserOTP;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.repo.APIAccessLogDao;
import com.proptiger.data.repo.CompanyIPDao;
import com.proptiger.data.repo.user.UserOTPDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.user.UserSubscriptionService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
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
    private APIAccessLogDao         accessLogDao;

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private UserOTPDao              userOTPDao;

    private OTPGenerator            generator = new OTPGenerator();

    @Autowired
    private AuthSuccessHandler      authSuccessHandler;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private CompanyIPDao            companyIPDao;

    public boolean isOTPRequired(Authentication auth, HttpServletRequest request) {
        boolean required = false;
        if(!PropertyReader.getRequiredPropertyAsType(PropertyKeys.ENABLE_OTP, Boolean.class)){
            return required;
        }
        ActiveUser activeUser = (ActiveUser) auth.getPrincipal();
        if (activeUser.getApplicationType().equals(Application.B2B)) {
            required = true;
            String userIP = request.getRemoteAddr();
            if(isUserCompanyIPWhitelisted(userIP, activeUser)){
                /*
                 * if user company ip is whitelisted then no need of
                 * otp
                 */
                required = false;
            }
        }
        return required;
    }

    private boolean isUserCompanyIPWhitelisted(String userIP, ActiveUser activeUser) {
        boolean whitelisted = false;
        List<UserSubscriptionMapping> userSubscriptions = userSubscriptionService
                .getUserSubscriptionMappingList(activeUser.getUserIdentifier());
        Set<Integer> companyIds = new HashSet<>();
        for (UserSubscriptionMapping userSubscription : userSubscriptions) {
            CompanySubscription subs = userSubscription.getSubscription();
            if (subs != null) {
                companyIds.add(subs.getCompanyId());
            }
        }
        if (!companyIds.isEmpty()) {
            List<CompanyIP> companyIps = companyIPDao.findByCompanyIdIn(companyIds);
            for (CompanyIP companyIP : companyIps) {
                if (companyIP.getIp().equals(userIP)) {
                    whitelisted = true;
                    break;
                }
            }
        }
        return whitelisted;
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
            SecurityContextUtils.grantUserAuthorityToActiveUser();
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

}