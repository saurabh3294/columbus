package com.proptiger.app.config.security;

import java.net.URI;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

/**
 * A session repository that fetch session information from user service module
 * 
 * @author Rajeev Pandey
 */
public class UserServiceSessionRepository implements SessionRepository<MapSession> {

    private static Logger                    logger = LoggerFactory.getLogger(UserServiceSessionRepository.class);
    @Autowired
    private HttpRequestUtil     httpRequestUtil;

    private static final String URL_DATA_V1_ENTITY_SESSION = "/userservice/data/v1/entity/session";

    @Override
    public MapSession createSession() {
        // do nothing
        return null;
    }

    @Override
    public void save(MapSession session) {
        // do nothing
    }

    @Override
    public MapSession getSession(String jsessionId) {
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
        String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(URL_DATA_V1_ENTITY_SESSION).toString();
        try {
            ActiveUserCopy activeUserCopy = httpRequestUtil.getInternalApiResultAsType(
                    URI.create(stringUrl),
                    header,
                    ActiveUserCopy.class);
            if (activeUserCopy != null) {
                ActiveUser activeUser = activeUserCopy.toActiveUser();
                return createSessionForActiveUser(activeUser, jsessionId);
            }
        }
        catch (Exception e) {
            logger.error("Error while getting session info from user service for {}",jsessionId);
        }
        return null;
    }

    private MapSession createSessionForActiveUser(ActiveUser activeUser, String sessionId) {
        MapSession loaded = new MapSession();
        loaded.setId(sessionId);
        loaded.setCreationTime(new Date().getTime());
        loaded.setLastAccessedTime(new Date().getTime());
        loaded.setMaxInactiveInterval(PropertyReader.getRequiredPropertyAsType(
                PropertyKeys.SESSION_MAX_INTERACTIVE_INTERVAL,
                Integer.class));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                activeUser,
                null,
                activeUser.getAuthorities());
        loaded.setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, activeUser);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(auth);
        loaded.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        return loaded;
    }

    @Override
    public void delete(String id) {
        //do nothing
    }

}
