package com.proptiger.app.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;

/**
 * Handle authenication failure case.
 * @author Rajeev Pandey
 *
 */
public class AuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String userIpAddress = request.getRemoteAddr();
        ResponseErrorWriter.writeErrorToResponse(
                response,
                ResponseCodes.BAD_CREDENTIAL,
                exception.getMessage() != null ? exception.getMessage() : ResponseErrorMessages.BAD_CREDENTIAL,
                userIpAddress);
    }
}
