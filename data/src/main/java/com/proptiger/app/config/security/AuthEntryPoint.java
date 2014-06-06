package com.proptiger.app.config.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.pojo.response.APIResponse;

/**
 * The Entry Point will not redirect to any sort of Login - it will return the
 * 401, in case of protected url called without login.
 */
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {
        String userIpAddress = request.getRemoteAddr();
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        // "Unauthorized");
        writeErrorToResponse(
                response,
                ResponseCodes.UNAUTHORIZED,
                ResponseErrorMessages.AUTHENTICATION_ERROR,
                userIpAddress);
    }

    private void writeErrorToResponse(HttpServletResponse response, String code, String msg, String userIpAddress)
            throws IOException, JsonProcessingException {
        logger.warn("Unauthenticated call from host {}", userIpAddress);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        APIResponse res = new APIResponse(code, msg);
        ObjectMapper mapper = new ObjectMapper();
        out.println(mapper.writeValueAsString(res));
        return;
    }
}