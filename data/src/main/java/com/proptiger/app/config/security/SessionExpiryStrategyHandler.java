package com.proptiger.app.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;

/**
 * This handler overrides the dfault implementation not to redirect on any url
 * rather display a session expiry message to user
 * 
 * @author Rajeev Pandey
 *
 */
public class SessionExpiryStrategyHandler implements RedirectStrategy {

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String userIpAddress = request.getRemoteAddr();
        ResponseErrorWriter.writeErrorToResponse(
                response,
                ResponseCodes.SESSION_EXPIRED,
                ResponseErrorMessages.SESSION_EXPIRED_DUPLICATE_LOGIN,
                userIpAddress);
    }

}
