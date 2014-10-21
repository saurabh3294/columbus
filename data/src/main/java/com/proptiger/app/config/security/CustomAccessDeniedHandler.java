package com.proptiger.app.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.proptiger.core.constants.ResponseCodes;

/**
 * @author Rajeev Pandey
 *
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (!response.isCommitted()) {
            String userIpAddress = request.getRemoteAddr();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ResponseErrorWriter.writeErrorToResponse(
                    response,
                    ResponseCodes.FORBIDDEN,
                    accessDeniedException.getMessage(),
                    userIpAddress);
        }

    }

}
