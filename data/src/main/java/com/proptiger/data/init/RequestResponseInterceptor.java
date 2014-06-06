package com.proptiger.data.init;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import redis.clients.jedis.Jedis;

import com.proptiger.data.enums.security.MaxAllowedRequestCount;

/**
 * 
 * @author Rajeev Pandey
 * @author Azitabh Ajit
 * 
 */
public class RequestResponseInterceptor extends HandlerInterceptorAdapter {
    private String  redisHost;

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
        Jedis jedis = new Jedis(redisHost, redisPort);
        for (MaxAllowedRequestCount maxAllowedRequestCount : MaxAllowedRequestCount.values()) {
            preventSpecificCrawling(maxAllowedRequestCount, requestIP, jedis);
        }
        jedis.disconnect();
    }

    private void preventSpecificCrawling(MaxAllowedRequestCount maxAllowedRequestCount, String requestIP, Jedis jedis) {
        Integer timeFrame = maxAllowedRequestCount.getTimeFrame();
        Integer maxRequestCount = maxAllowedRequestCount.getAllowedRequestCount();

        Long timeBucket = new Date().getTime() / (timeFrame * 1000);
        String key = timeBucket + requestIP;

        Integer count = 1;

        String cachedValue = jedis.get(key);
        if (cachedValue != null) {
            count = Integer.valueOf(cachedValue);
            count++;
            if (count % maxRequestCount == 0) {
                logger.error("Crawing Identified!!  Type: " + maxAllowedRequestCount.getLabel()
                        + " IP: "
                        + requestIP
                        + "  Request Count in Time Slot: "
                        + count);
            }
        }
        jedis.setex(key, timeFrame, count.toString());
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public Integer getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(Integer redisPort) {
        this.redisPort = redisPort;
    }
    
}