package com.proptiger.data.service.security;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.util.PropertyReader;

/**
 * Captcha service that uses google recapcha to generate and validate captcha
 * image
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class CaptchaService {
    @Autowired
    private PropertyReader propertyReader;

    private Logger         logger = LoggerFactory.getLogger(CaptchaService.class);

    /**
     * Validates captcha
     * 
     * @param request
     * @return
     */
    public boolean isValidCaptcha(HttpServletRequest request) {
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(propertyReader.getRequiredProperty("recaptcha.private.key"));
        String remoteAddr = request.getRemoteAddr();
        String challengeField = request.getParameter("recaptcha_challenge_field");
        String responseField = request.getParameter("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challengeField, responseField);
        if (reCaptchaResponse.isValid()) {
            return true;
        }
        logger.error(
                "Invalid captcha, error-{}, challenge: {}, response: {}, remoteip {}",
                reCaptchaResponse.getErrorMessage(),
                challengeField,
                responseField,
                remoteAddr);
        return false;
    }

    /**
     * Find if user called API with capctcha response
     * 
     * @param request
     * @return
     */
    public boolean isCaptchaRequest(HttpServletRequest request) {
        String challengeField = request.getParameter("recaptcha_challenge_field");
        String responseField = request.getParameter("recaptcha_response_field");
        if (challengeField != null && responseField != null && !challengeField.isEmpty() && !responseField.isEmpty()) {
            return true;
        }
        return false;
    }

    public void writeCaptchaInResponse(HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            ObjectMapper mapper = new ObjectMapper();
            out.println(mapper.writeValueAsString(new APIResponse(
                    ResponseCodes.CAPTCHA_REQUIRED,
                    ResponseErrorMessages.CAPTCHA_REQUIRED)));
        }
        catch (Exception e) {
            logger.error("Error generating captcha {}", e);
        }
    }
}
