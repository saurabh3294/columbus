package com.proptiger.app.config.security;

import java.io.IOException;
import java.net.URI;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.core.util.SecurityContextUtils;

/**
 * @author Rajeev Pandey
 * 
 */
public class SessionRepositoryFilter implements Filter {

    @Autowired
    private HttpRequestUtil     httpRequestUtil;

    private static final String URL_DATA_V1_ENTITY_SESSION = "/userservice/data/v1/entity/session";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
        if (jsessionId != null && !jsessionId.isEmpty()) {
            HttpHeaders header = new HttpHeaders();
            header.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
            String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                    .append(URL_DATA_V1_ENTITY_SESSION).toString();
            try {
                ActiveUserCopy activeUserCopy = httpRequestUtil.getInternalApiResultAsType(
                        URI.create(stringUrl),
                        header,
                        ActiveUserCopy.class);
                if(activeUserCopy != null){
                    ActiveUser activeUser = activeUserCopy.toActiveUser();
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            activeUser,
                            null,
                            activeUser.getAuthorities());
                    SecurityContextUtils.putActiveUserInSession((HttpServletRequest)request, auth);
                }
            }
            catch (Exception e) {
                System.out.println("Exception "+e);;
            }
            
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
