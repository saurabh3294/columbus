package com.proptiger.app.config.security.social;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationRedirectException;

import com.proptiger.app.config.security.ResponseErrorWriter;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;

/**
 * Failure handler for spring social flow, if auth requested with access token
 * and that failed then show error mesage otherwise redirect to service provider
 * url
 * 
 * @author Rajeev Pandey
 *
 */
public class SocialAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public SocialAuthFailureHandler(String defaultFalureUrl) {
        super(defaultFalureUrl);
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String accessToken = request.getParameter("access_token");
        if (accessToken != null && !accessToken.isEmpty()) {
            /*
             * client requested with access token and that access token might be
             * expired or invalid.
             */
            String userIpAddress = request.getRemoteAddr();
            ResponseErrorWriter.writeErrorToResponse(
                    response,
                    ResponseCodes.BAD_CREDENTIAL,
                    exception.getMessage() != null ? exception.getMessage() : ResponseErrorMessages.BAD_CREDENTIAL,
                    userIpAddress);
            return;
        }
        /*
         * for normal flow of app/v1/login/{provider}
         */
        if (exception instanceof SocialAuthenticationRedirectException) {
            response.sendRedirect(((SocialAuthenticationRedirectException) exception).getRedirectUrl());
            return;
        }
        super.onAuthenticationFailure(request, response, exception);

    }
}
