package com.proptiger.app.config.security;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.core.util.SecurityContextUtils;

public class CustomHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository{

    @Autowired
    private HttpRequestUtil     httpRequestUtil;

    private static final String URL_DATA_V1_ENTITY_SESSION = "/userservice/data/v1/entity/session";

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext context = super.loadContext(requestResponseHolder);
        if(context != null && context.getAuthentication() == null){
            String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
            if (jsessionId != null && !jsessionId.isEmpty()) {
                HttpServletRequest httpsServletRequest = requestResponseHolder.getRequest();
                HttpHeaders header = new HttpHeaders();
                header.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
                String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                        .append(URL_DATA_V1_ENTITY_SESSION).toString();
                try {
                    MapSession loaded = new MapSession();
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
                        HttpSession httpsSession = httpsServletRequest.getSession(true);
                        if(httpsSession != null){
                            SecurityContext securityContext = new SecurityContextImpl();
                            securityContext.setAuthentication(auth);
                            httpsSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
                            SecurityContextUtils.putActiveUserInSession(httpsServletRequest,auth);
                        }
                       
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception "+e);;
                }
                
            }
        }
        return context;
    }
}
