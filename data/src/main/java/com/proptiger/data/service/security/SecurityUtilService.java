package com.proptiger.data.service.security;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PasswordUtils;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * Utility methods related to api access security.
 * 
 * @author Rajeev Pandey
 *
 */
@Component
public class SecurityUtilService {

    /**
     * This method decides if crawl prevention check should be applied or not
     * for this request, based on on/off flag, internal IP or any other
     * conditions.
     * 
     * @param request
     * @param response
     * @return
     */
    public boolean isReqValivationEnabled(HttpServletRequest request) {
        return PropertyReader.getRequiredPropertyAsType(PropertyKeys.ENABLE_REQUEST_VALIDATION, Boolean.class);
    }

    /**
     * Add a wrning header about illegal api access, so that no client will be
     * forced to provide HMAC key in header.
     * 
     * @param response
     */
    public void addWarningHeader(HttpServletResponse response) {
        if (PropertyReader.getRequiredPropertyAsType(PropertyKeys.ENABLE_REQ_VALIDATION_WARNING, Boolean.class)) {
            response.addHeader("Warn", Constants.Security.WARN_ILLEGAL_API_ACCESS_MSG);
            response.addHeader("Warn", "Send MD5 of (ip+sep+user-agent+sep+server-time+sep+api-secretword)");
        }
    }

    /**
     * Set server current time and server api secret key in response
     * 
     * @param response
     */
    public void setTimeAndKeywordInHeader(HttpServletResponse response) {
        response.setHeader(Constants.Security.SERVER_CURR_TIME, String.valueOf(new Date().getTime()));
        response.setHeader(Constants.Security.API_SECRET_KEYWORD, Constants.Security.API_SECRET_WORD_VALUE);
    }

    public String createCrawlCacheKey(String requestIP, Integer timeFrame) {
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
    public String createKeyForSecretHash(HttpServletRequest request) {
        return request.getRemoteAddr() + ":" + PasswordUtils.encode(request.getHeader(Constants.USER_AGENT));
    }
    
    public boolean isHashAndTimeExistInHeader(HttpServletRequest request) {
        String serverTimeHeader = request.getHeader(Constants.Security.SERVER_CURR_TIME);
        String secretHashValHeader = request.getHeader(Constants.Security.SECRET_HASH_HEADER_KEY);
        if (serverTimeHeader == null || secretHashValHeader == null
                || serverTimeHeader.trim().isEmpty()
                || secretHashValHeader.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Generating MD5 encoded hash value, same sequence of values should be used
     * by client as well to access apis.
     * 
     * @param request
     * @param serverTimeHeader
     * @return
     */
    public String generateSecretHash(HttpServletRequest request, String serverTimeHeader) {
        StringBuilder hash = new StringBuilder();
        hash.append(request.getRemoteAddr()).append(Constants.Security.HASH_SEPERATOR)
                .append(request.getHeader(Constants.USER_AGENT)).append(Constants.Security.HASH_SEPERATOR)
                .append(serverTimeHeader).append(Constants.Security.HASH_SEPERATOR)
                .append(Constants.Security.API_SECRET_WORD_VALUE);
        return PasswordUtils.encode(hash.toString());
    }

    public Integer getIllegalAPIAccessCountThreshold() {
        return PropertyReader.getRequiredPropertyAsType(PropertyKeys.ILLEGAL_API_ACCESS_THRESHOLD_COUNT, Integer.class);
    }

    /**
     * Check if user ip matches with any of the white listed IPs
     * 
     * @param request
     * @return
     */
    public boolean isCrawlCheckDisabled(HttpServletRequest request) {
        boolean disabled = false;
        String userIp = request.getRemoteAddr();
        Set<String> whiteListIps = PropertyReader.getRequiredPropertyAsType(PropertyKeys.WHITELISTED_IP, Set.class);
        if (whiteListIps != null && !whiteListIps.isEmpty()) {
            disabled = whiteListIps.contains(userIp);
        }
        return disabled;
    }

    /**
     * Check if request validation and captcha is disabled by verifying enable flag in
     * property file and checking if user IP is whitelisted
     * 
     * @param request
     * @return
     */
    public boolean isReqValAndCaptchaDisabled(HttpServletRequest request) {
        if (!PropertyReader.getRequiredPropertyAsType(PropertyKeys.ENABLE_CRAWL_PREVENTION, Boolean.class)) {
            return true;
        }
        return isCrawlCheckDisabled(request);
    }

}
