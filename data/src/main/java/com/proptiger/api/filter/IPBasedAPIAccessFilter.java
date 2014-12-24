package com.proptiger.api.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.proptiger.app.config.security.CustomAccessDeniedHandler;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.util.IPUtils;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

/**
 * This filter blocks a request coming from non white listed IP for API regex
 * API_REGEX
 * This is applied before spring security login filters
 * @author Rajeev Pandey
 *
 */
public class IPBasedAPIAccessFilter implements Filter {

    //change API regex, for example /data/v1/entity/city.*
    //private final String  API_REGEX = "/data/v1/(coupon/.*|transaction/offline-coupon\\?.*|entity/notification/sender)";
    private List<RequestMatcher>            requestMatchers;
    private Set<String>               whiteListIps;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public IPBasedAPIAccessFilter() {
        List<String> regex = PropertyReader.getRequiredPropertyAsType(PropertyKeys.SECURITY_SECURED_APIS, List.class);
        requestMatchers = new ArrayList<>();
        for(String apiRegex: regex){
            requestMatchers.add(new RegexRequestMatcher(apiRegex, null));
        }
        accessDeniedHandler = new CustomAccessDeniedHandler();
        whiteListIps = PropertyReader.getRequiredPropertyAsType(PropertyKeys.WHITELISTED_IP, Set.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean secureAccess = true;
        for(RequestMatcher requestMatcher: requestMatchers){
            if (requestMatcher.matches(httpRequest)) {
                // secure url, should be accessed from specified IP only
                if (!whiteListIps.contains(IPUtils.getClientIP(request))) {
                    secureAccess = false;
                    break;
                }
            }
        }
        
        if (!secureAccess) {
            accessDeniedHandler.handle(httpRequest, (HttpServletResponse) response, new AccessDeniedException(
                    ResponseErrorMessages.User.ACCESS_DENIED));
            return;
        }
        chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {
    }

}
