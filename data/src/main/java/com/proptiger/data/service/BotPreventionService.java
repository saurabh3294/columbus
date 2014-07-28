package com.proptiger.data.service;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.proptiger.data.util.Constants;
import com.proptiger.data.util.PasswordUtils;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * Bot prevention service identifies a probable bot request and sent empty
 * response. It relies on redis to identify first request of a client and then
 * it let pass that request plus subsequent request till a threshold, even if
 * user did not match the hash contract.
 * 
 * Once threshold limit exceeds it will direst to send a empty response, so it
 * is tightly bound to redis to identify first or subsequent request from a
 * user. A user is identified as IP+MD5(user-agent)
 * 
 * See method generateSecretHash for hash value generation.
 * 
 * This service expects Constants.Security.SERVER_CURR_TIME and
 * Constants.Security.SERVER_CURR_TIME in request header for a valid request
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class BotPreventionService {

    /*
     * As of now this is static for all users, could be auto generated in future
     * for each user
     */
    private static final String API_SECRET_KEYWORD           = "proptiger-api";
    private static final int    CLIENT_TIME_VALIDITY_MINUTES = 10;
    private static final String HASH_SEPERATOR               = "#$$#";
    private Logger              logger                       = LoggerFactory.getLogger(BotPreventionService.class);
    @Autowired
    private PropertyReader      propertyReader;

    public boolean isValidRequest(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // if bot prevention is not enabled then return true
        if (!propertyReader.getRequiredPropertyAsType(PropertyKeys.ENABLE_BOT_PREVENTAION, Boolean.class)) {
            return true;
        }
        setTimeAndKeywordInHeader(response);
        Jedis jedis = new Jedis(
                propertyReader.getRequiredProperty(PropertyKeys.REDIS_HOST),
                propertyReader.getRequiredPropertyAsType(PropertyKeys.REDIS_PORT, Integer.class));
        String redisKey = createClientRedisKey(request);
        String secretHashKeyValRedis = jedis.get(redisKey);
        if (secretHashKeyValRedis == null) {
            /*
             * probably first hit from the client, set access count as 1.
             */
            jedis.set(redisKey, String.valueOf(1));
        }
        else {
            /*
             * now match the secret header and timestamp present in request
             * header. If client did not follow the contract then just pass the
             * request and update the count in redis that will act as threshold
             * access count without being follow the contract, and so after a
             * threshold just discard the request being processed
             */
            String serverTimeHeader = request.getHeader(Constants.Security.SERVER_CURR_TIME);
            String secretHashValHeader = request.getHeader(Constants.Security.SECRET_HASH_HEADER_KEY);
            isSecretHashMatchedWithClientHash(request, serverTimeHeader, secretHashValHeader);
            if (serverTimeHeader == null || secretHashValHeader == null
                    || serverTimeHeader.trim().isEmpty()
                    || secretHashValHeader.trim().isEmpty()) {
                // if any of them null then user did not follow the contract
                int illegalAPIAccessThresholdCount = getIllegalAPIAccessCountThreshold();
                int accessedCount = Integer.parseInt(secretHashKeyValRedis);
                if (accessedCount >= illegalAPIAccessThresholdCount) {
                    // illegal access count surpassed the threshold
                    logger.error(
                            "Illegal api access count surpasswed the threshold {} for {}",
                            illegalAPIAccessThresholdCount,
                            redisKey);
                    return false;
                }
                else {
                    accessedCount = accessedCount + 1;
                    jedis.set(redisKey, String.valueOf(accessedCount));
                }
            }
            else {
                // check the validity of server time and secret key header
                Date currTimeSentByClient = new Date(Long.valueOf(serverTimeHeader.trim()));
                Calendar cal = Calendar.getInstance();
                Date currDate = cal.getTime();
                cal.add(Calendar.MINUTE, -CLIENT_TIME_VALIDITY_MINUTES);
                /*
                 * if current time sent by client is not in 15 minute window //
                 * then discard the request. // current time sent by client
                 * should not be greater than // current server time, as this
                 * seems invalid for a valid request
                 */
                if (currTimeSentByClient.before(cal.getTime()) || currTimeSentByClient.after(currDate)) {
                    logger.debug("Server time contract mismatch, client sent {}", currTimeSentByClient);
                    return false;
                }

                if (isSecretHashMatchedWithClientHash(request, serverTimeHeader, secretHashValHeader)) {
                    return true;
                }
                else {
                    return false;
                }

            }

        }
        // set current server time in response header. in each request sending
        // these values
        jedis.disconnect();
        return true;
    }

    private void setTimeAndKeywordInHeader(HttpServletResponse response) {
        response.setHeader(Constants.Security.SERVER_CURR_TIME, String.valueOf(new Date().getTime()));
        response.setHeader(Constants.Security.API_SECRET_KEYWORD, API_SECRET_KEYWORD);
    }

    private boolean isSecretHashMatchedWithClientHash(
            HttpServletRequest request,
            String serverTimeHeader,
            String secretHashValHeader) {
        String generatedHash = generateSecretHash(request, serverTimeHeader);
        return generatedHash.equals(secretHashValHeader);
    }

    /**
     * Generating MD5 encoded hash value, same sequence of values should be used
     * by client as well to access apis.
     * 
     * @param request
     * @param serverTimeHeader
     * @return
     */
    private String generateSecretHash(HttpServletRequest request, String serverTimeHeader) {
        StringBuilder hash = new StringBuilder();
        hash.append(request.getRemoteAddr()).append(HASH_SEPERATOR).append(request.getHeader(Constants.USER_AGENT))
                .append(HASH_SEPERATOR).append(serverTimeHeader).append(HASH_SEPERATOR).append(API_SECRET_KEYWORD);
        return PasswordUtils.encode(hash.toString());
    }

    private String createClientRedisKey(HttpServletRequest request) {
        String redisKey = request.getRemoteAddr() + ":" + PasswordUtils.encode(request.getHeader(Constants.USER_AGENT));
        return redisKey;
    }

    public Integer getIllegalAPIAccessCountThreshold() {
        return propertyReader.getRequiredPropertyAsType(PropertyKeys.ILLEGAL_API_ACCESS_THRESHOLD_COUNT, Integer.class);
    }
}
