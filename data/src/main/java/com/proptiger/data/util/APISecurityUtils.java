package com.proptiger.data.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility methods related to api access security.
 * 
 * @author Rajeev Pandey
 *
 */
public class APISecurityUtils {

    /**
     * Add a wrning header about illegal api access, so that no client will be
     * forced to provide HMAC key in header.
     * 
     * @param response
     */
    public static void addWarningHeader(HttpServletResponse response, boolean warningEnabled) {
        if(warningEnabled){
            response.addHeader("Warning", Constants.Security.WARN_ILLEGAL_API_ACCESS_MSG);
            response.addHeader("Warning", "Send MD5 of (ip+sep+user-agent+sep+server-time+sep+api-secretword)");
        }
    }

    /**
     * Set server current time and server api secret key in response
     * 
     * @param response
     */
    public static void setTimeAndKeywordInHeader(HttpServletResponse response) {
        response.setHeader(Constants.Security.SERVER_CURR_TIME, String.valueOf(new Date().getTime()));
        response.setHeader(Constants.Security.API_SECRET_KEYWORD, Constants.Security.API_SECRET_WORD_VALUE);
    }

    public static String createCrawlCacheKey(String requestIP, Integer timeFrame) {
        Long timeBucket = new Date().getTime() / (timeFrame * 1000);
        String key = timeBucket + requestIP;
        return key;
    }

    /**
     * Create client redis key
     * 
     * @param request
     * @return
     */
    public static String createKeyForSecretHash(HttpServletRequest request) {
        return request.getRemoteAddr() + ":" + PasswordUtils.encode(request.getHeader(Constants.USER_AGENT));
    }
}
