package com.proptiger.data.service.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.CustomRedisCacheManager;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.security.MaxAllowedRequestCount;
import com.proptiger.data.util.Constants;

/**
 * Identifies crawling and return captcha in case of POST request and writes in
 * log for all other request methods
 * 
 * @author Rajeev Pandey
 * @author Azitabh Ajit
 *
 */
@Service
public class CrawlPreventionService {
    private Logger                  logger = LoggerFactory.getLogger(CrawlPreventionService.class);
    @Autowired
    private CustomRedisCacheManager redisCacheManager;

    @Autowired
    private CaptchaService          captchaService;

    @Autowired
    private SecurityUtilService     securityUtilService;

    /**
     * 1. First check if request validation and captcha is disable property file
     * or through IP whitelisting 2. Then it will identify if request contains
     * answer of previous captcha. 3. Validates a request by identifying secret
     * hash and server time sent by client. 4. If valid then identify crawling
     * based on access count bucket and respond with captcha error if invalid
     * 
     * @param request
     * @param response
     * @return boolean
     */
    public boolean isValidRequest(HttpServletRequest request, HttpServletResponse response) {
        if (securityUtilService.isReqValAndCaptchaDisabled(request)) {
            return true;
        }
        if (captchaService.isCaptchaRequest(request)) {
            if (captchaService.isValidCaptcha(request)) {
                resetAccessCountInCache(request);
                return true;
            }
            else {
                // if invalid answer in captcha then respond again with captcha
                captchaService.writeCaptchaInResponse(response);
                return false;
            }
        }
        boolean isValid = isValidRequestWithSecretHash(request, response);
        if (isValid) {
            if (isCrawlIdentified(request, response)) {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * reset access count
     * 
     * @param request
     */
    private void resetAccessCountInCache(HttpServletRequest request) {
        for (MaxAllowedRequestCount maxAllowedRequestCount : MaxAllowedRequestCount.values()) {
            Integer timeFrame = maxAllowedRequestCount.getTimeFrame();
            Cache cache = redisCacheManager.getCache(Constants.CacheName.API_ACCESS, timeFrame);
            String key = securityUtilService.createCrawlCacheKey(request.getRemoteAddr(), timeFrame);
            ValueWrapper cacheValWrapper = cache.get(key);
            APIAccessCount apiAccessCount = (APIAccessCount) (cacheValWrapper != null
                    ? cacheValWrapper.get()
                    : new APIAccessCount().setPostCount(0));
            if (apiAccessCount.getPostCount() > maxAllowedRequestCount.getAllowedPostRequestCount()) {
                apiAccessCount.setPostCount(0);
            }
            cache.put(key, apiAccessCount);
        }
    }

    /**
     * This method identify any bot access or access from a script and validate
     * that count in a specified time bucket, if access count surpass the limit,
     * respond with captcha message. For internal IPs/server should be disabled
     * 
     * @param request
     * @param response
     * @return
     */
    private boolean isCrawlIdentified(HttpServletRequest request, HttpServletResponse response) {
        boolean crawlIdentified = false;
        for (MaxAllowedRequestCount maxAllowedRequestCount : MaxAllowedRequestCount.values()) {
            if (isSpecificCrawlingIdentified(maxAllowedRequestCount, request)) {
                captchaService.writeCaptchaInResponse(response);
                crawlIdentified = true;
                break;
            }
        }
        return crawlIdentified;
    }

    private boolean isSpecificCrawlingIdentified(
            MaxAllowedRequestCount maxAllowedRequestCount,
            HttpServletRequest request) {
        boolean isCrawling = false;
        String requestMethod = request.getMethod();
        String requestIP = request.getRemoteAddr();
        Integer timeFrame = maxAllowedRequestCount.getTimeFrame();
        Integer maxRequestCount = maxAllowedRequestCount.getAllowedAllRequestCount();
        Cache cache = redisCacheManager.getCache(Constants.CacheName.API_ACCESS, timeFrame);
        String key = securityUtilService.createCrawlCacheKey(requestIP, timeFrame);

        APIAccessCount apiAccessCount = (APIAccessCount) (cache.get(key) != null
                ? cache.get(key).get()
                : new APIAccessCount());
        Integer accessCount = apiAccessCount.getTotalAccessCount();
        apiAccessCount.setTotalAccessCount(++accessCount);
        switch (requestMethod) {
            case "POST":
                int postCount = apiAccessCount.getPostCount();
                apiAccessCount.setPostCount(postCount + 1);
                if (postCount > maxAllowedRequestCount.getAllowedPostRequestCount()) {
                    isCrawling = true;
                    logCrawlError(maxAllowedRequestCount, request, requestIP, postCount);
                }
                break;
            default:
                /*
                 * For other request method just log the crawl message rather
                 * than blocking the request.
                 */
                if (accessCount % maxRequestCount == 0) {
                    logCrawlError(maxAllowedRequestCount, request, requestIP, accessCount);
                }
                break;
        }
        cache.put(key, apiAccessCount);
        return isCrawling;
    }

    private void logCrawlError(
            MaxAllowedRequestCount maxAllowedRequestCount,
            HttpServletRequest request,
            String requestIP,
            Integer count) {
        logger.error("Crawing Identified!! Method:" + request.getMethod()
                + "  Type: "
                + maxAllowedRequestCount.name()
                + " IP: "
                + requestIP
                + "  Request Count in Time Slot: "
                + count
                + " URL: "
                + request.getRequestURI()
                + " AGENT "
                + request.getHeader("user-agent")
                + " PROTOCOL: "
                + request.getProtocol()
                + " USER HOST: "
                + request.getRemoteHost());
    }

    /**
     * This method validates request and return true for valid request.
     * 
     * @param request
     * @param response
     * @param handler
     * @return
     */
    private boolean isValidRequestWithSecretHash(HttpServletRequest request, HttpServletResponse response) {
        securityUtilService.setTimeAndKeywordInHeader(response);
        boolean isValid = true;
        if (securityUtilService.isReqValivationEnabled(request)) {
            Cache illegalAccessCache = redisCacheManager.getCache(Constants.CacheName.ILLEGAL_API_ACCESS);
            String redisKey = securityUtilService.createKeyForSecretHash(request);
            ValueWrapper redisValWrapper = illegalAccessCache.get(redisKey);
            Integer illegalAccessCount = (Integer) (redisValWrapper != null
                    ? illegalAccessCache.get(redisKey).get()
                    : null);
            /*
             * probably first hit from the client, set access count to 1.
             */
            if (illegalAccessCount == null) {
                illegalAccessCache.put(redisKey, 1);
            }
            else {
                // not first request from user
                isValid = isValidSubsequentRequest(request, response, illegalAccessCache, redisKey, illegalAccessCount);
            }
            return isValid;
        }
        else {
            /*
             * even if disabled, we are validating the request to show warning
             * message if needed and let request complete normally.
             * 
             * TODO should be removed once integrated by all clients.
             */
            if (!securityUtilService.isHashAndTimeExistInHeader(request) || !isValidServerTimeAndSecretHashHeader(request)) {
                securityUtilService.addWarningHeader(response);
            }
            // should always return true, as bot prevention is disabled
            return true;
        }

    }

    /**
     * This methods validates the request on the basis of server time and secret
     * hash header
     * 
     * @param request
     * @param response
     * @param jedis
     * @param redisKey
     * @param illegalAccessCount
     * @return
     */
    private boolean isValidSubsequentRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            Cache cache,
            String redisKey,
            Integer illegalAccessCount) {
        boolean valid = true;
        /*
         * now match the secret header and timestamp present in request header.
         * If client did not follow the contract then just pass the request and
         * update the count in redis that will act as threshold access count
         * without being follow the contract, and so after a threshold just
         * discard the request being processed
         */

        if (!securityUtilService.isHashAndTimeExistInHeader(request)) {
            valid = handleIllegalAPIAccess(response, cache, redisKey, illegalAccessCount);

        }
        else {
            valid = isValidServerTimeAndSecretHashHeader(request);
        }
        return valid;
    }

    /**
     * Handles illegal api access and depending on threshold either let request
     * process normally or will respond with captcha
     * 
     * @param response
     * @param cache
     * @param redisKey
     * @param illegalAccessCount
     * @return
     */
    private boolean handleIllegalAPIAccess(
            HttpServletResponse response,
            Cache cache,
            String redisKey,
            Integer illegalAccessCount) {
        boolean valid = true;
        securityUtilService.addWarningHeader(response);
        int illegalAPIAccessThresholdCount = securityUtilService.getIllegalAPIAccessCountThreshold();
        if (illegalAccessCount >= illegalAPIAccessThresholdCount) {
            // illegal access count surpassed the threshold, block request
            logger.error(
                    "Illegal api access count surpasswed the threshold {} for {}",
                    illegalAPIAccessThresholdCount,
                    redisKey);
            captchaService.writeCaptchaInResponse(response);
            valid = false;
        }
        else {
            illegalAccessCount = illegalAccessCount + 1;
            cache.put(redisKey, illegalAccessCount);
        }
        return valid;
    }

    /**
     * Validate request by checking server time header and secret hash header.
     * 
     * @param request
     * @param serverTimeHeader
     * @param secretHashValHeader
     * @return
     */
    private boolean isValidServerTimeAndSecretHashHeader(HttpServletRequest request) {
        String serverTimeHeader = request.getHeader(Constants.Security.SERVER_CURR_TIME);
        String secretHashValHeader = request.getHeader(Constants.Security.SECRET_HASH_HEADER_KEY);
        // check the validity of server time and secret key header
        Date currTimeSentByClient = new Date(Long.valueOf(serverTimeHeader.trim()));
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();
        cal.add(Calendar.MINUTE, -Constants.Security.CLIENT_TIME_VALIDITY_MINUTES);
        /*
         * if current time sent by client is not in 15 minute window then
         * discard the request. current time sent by client should not be
         * greater than current server time, as this seems invalid for a valid
         * request
         */
        if (currTimeSentByClient.before(cal.getTime()) || currTimeSentByClient.after(currDate)) {
            logger.debug(
                    "Server time contract mismatch, user@{} sent {}",
                    request.getRemoteAddr(),
                    currTimeSentByClient);
            return false;
        }

        if (isSecretHashMatchedWithClientHash(request, serverTimeHeader, secretHashValHeader)) {
            return true;
        }
        else {
            logger.error("secret hash did not matched, user@{} sent {}", request.getRemoteAddr(), secretHashValHeader);
            return false;
        }
    }

    /**
     * Compare hash sent by client and generated hash
     * 
     * @param request
     * @param serverTimeHeader
     * @param secretHashValHeader
     * @return
     */
    private boolean isSecretHashMatchedWithClientHash(
            HttpServletRequest request,
            String serverTimeHeader,
            String secretHashValHeader) {
        String generatedHash = securityUtilService.generateSecretHash(request, serverTimeHeader);
        return generatedHash.equals(secretHashValHeader);
    }

    private static class APIAccessCount implements Serializable {
        private static final long serialVersionUID = 6672362687125668786L;
        private int               totalAccessCount;
        private int               postCount;

        public APIAccessCount() {
        }

        public int getTotalAccessCount() {
            return totalAccessCount;
        }

        public APIAccessCount setTotalAccessCount(int totalAccessCount) {
            this.totalAccessCount = totalAccessCount;
            return this;
        }

        public int getPostCount() {
            return postCount;
        }

        public APIAccessCount setPostCount(int postCount) {
            this.postCount = postCount;
            return this;
        }

    }
}
