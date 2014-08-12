package com.proptiger.data.init;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.proptiger.data.service.APIAccessDetailPersistentService;
import com.proptiger.data.service.security.CrawlPreventionService;

/**
 * 
 * @author Rajeev Pandey
 * @author Azitabh Ajit
 * 
 */
public class RequestResponseInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private APIAccessDetailPersistentService userAccessDetailPersistentService;

    @Autowired
    private CrawlPreventionService           crawlPreventionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (crawlPreventionService.isValidRequest(request, response)) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            userAccessDetailPersistentService.processRequest(request, response);
            return super.preHandle(request, response, handler);
        }
        return false;
    }

}