package com.proptiger.data.init;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import redis.clients.jedis.Jedis;

import com.proptiger.data.enums.security.MaxAllowedRequestCount;
import com.proptiger.data.service.APIAccessDetailPersistentService;
import com.proptiger.data.service.BotPreventionService;

/**
 * 
 * @author Rajeev Pandey
 * @author Azitabh Ajit
 * 
 */
public class RequestResponseInterceptor extends HandlerInterceptorAdapter {
    private String                           redisHost;

    private Integer                          redisPort;

    private Logger                           logger = LoggerFactory.getLogger(RequestResponseInterceptor.class);

    @Autowired
    private APIAccessDetailPersistentService userAccessDetailPersistentService;

    @Autowired
    private BotPreventionService             botPreventionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (botPreventionService.isValidRequest(request, response, handler)) {
            preventCrawling(request);
            response.addHeader("Access-Control-Allow-Origin", "*");
            userAccessDetailPersistentService.processRequest(request, response);
            return super.preHandle(request, response, handler);
        }
        return false;
    }

    private void preventCrawling(HttpServletRequest request) {

        Jedis jedis = new Jedis(redisHost, redisPort);
        for (MaxAllowedRequestCount maxAllowedRequestCount : MaxAllowedRequestCount.values()) {
            preventSpecificCrawling(maxAllowedRequestCount, request, jedis);
        }
        jedis.disconnect();
    }

    private void preventSpecificCrawling(
            MaxAllowedRequestCount maxAllowedRequestCount,
            HttpServletRequest request,
            Jedis jedis) {
        String requestIP = request.getRemoteAddr();
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