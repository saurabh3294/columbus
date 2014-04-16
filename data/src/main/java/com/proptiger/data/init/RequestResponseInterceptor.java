package com.proptiger.data.init;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import redis.clients.jedis.Jedis;

import com.proptiger.data.security.enums.MaxAllowedRequestCount;

/**
 * 
 * @author Rajeev Pandey
 * 
 */
public class RequestResponseInterceptor extends HandlerInterceptorAdapter {
    @Value("${redis.hostName}")
    private String  redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    private Logger  logger = LoggerFactory.getLogger(RequestResponseInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        preventCrawling(request);
        response.addHeader("Access-Control-Allow-Origin", "*");
        return super.preHandle(request, response, handler);
    }

    private void preventCrawling(HttpServletRequest request) {
        String requestIP = request.getRemoteAddr();
        for (MaxAllowedRequestCount maxAllowedRequestCount : MaxAllowedRequestCount.values()) {
            preventSpecificCrawling(maxAllowedRequestCount, requestIP);
        }
    }

    private void preventSpecificCrawling(MaxAllowedRequestCount maxAllowedRequestCount, String requestIP) {
        Integer timeFrame = maxAllowedRequestCount.getTimeFrame();
        Integer maxRequestCount = maxAllowedRequestCount.getAllowedRequestCount();

        Long timeBucket = new Date().getTime() / (timeFrame * 1000);
        String key = timeBucket + requestIP;

        Integer count = 1;

        Jedis jedis = new Jedis(redisHost, redisPort);
        String cachedValue = jedis.get(key);
        if (cachedValue != null) {
            count = Integer.valueOf(cachedValue);
            count++;
            if (count % maxRequestCount == 0) {
                logger.error("Crawing Identified!!  Type: " + maxAllowedRequestCount.getLabel()
                        + " IP: "
                        + requestIP
                        + "  Times Allowed Count: "
                        + count
                        / maxRequestCount);
            }
        }
        jedis.setex(key, timeFrame, count.toString());
        jedis.disconnect();
    }
}