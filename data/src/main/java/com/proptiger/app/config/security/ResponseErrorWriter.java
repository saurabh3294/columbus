package com.proptiger.app.config.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.pojo.response.APIResponse;

/**
 * Utility class to write error message on response object using PrintWriter
 * object
 * 
 * @author Rajeev Pandey
 *
 */
public class ResponseErrorWriter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseErrorWriter.class);

    public static void writeErrorToResponse(HttpServletResponse response, String code, String msg, String userIpAddress)
            throws IOException, JsonProcessingException {
        logger.warn("Unauthenticated call from host {}", userIpAddress);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        APIResponse res = new APIResponse(code, msg);
        ObjectMapper mapper = new ObjectMapper();
        out.println(mapper.writeValueAsString(res));
        return;
    }
    public static void logAPIUrlInLogFile(HttpServletRequest httpRequest, Exception ex) {
        if (httpRequest != null) {
            logger.error(
                    "Exception occured while accessing url {} {} {} {} {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpRequest.getQueryString(),
                    httpRequest.getHeader("user-agent"),
                    httpRequest.getRemoteAddr(),
                    ex);
        }
    }
}
