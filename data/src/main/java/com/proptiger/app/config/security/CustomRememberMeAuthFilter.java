package com.proptiger.app.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.util.CacheClientUtil;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * Custom implementation of remember me filter to set active user data in
 * session in a flow when user gets auto logged in based on remember me cookie,
 * and continue in filter chain so no need of redirection required in
 * authentication success handler.
 * 
 * This class handles the user who logged in using rememberme on website when
 * login was based on memcache and since they should not be logged out becoz we
 * removed dependency over memcache we are making user auto login in api if
 * PHPSESSID is found in memcache else normal flow should work.
 * 
 * @author Rajeev Pandey
 *
 */
public class CustomRememberMeAuthFilter extends RememberMeAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomRememberMeAuthFilter.class);

    public CustomRememberMeAuthFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        if (SecurityContextUtils.getAuthentication() == null) {
            HttpServletRequest request = ((HttpServletRequest) req);
            String sessionId = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals(Constants.PHPSESSID_KEY)) {
                        sessionId = c.getValue();
                        break;
                    }
                }
            }
            if (sessionId != null && !sessionId.isEmpty()) {
                try {
                    ActiveUser activeUser = CacheClientUtil.getUserInfoFromMemcache(sessionId);
                    Authentication auth = SecurityContextUtils.autoLogin(activeUser);
                    onSuccessfulAuthentication(request, (HttpServletResponse) res, auth);
                }
                catch (Exception e) {
                    logger.error("Remembered user could not be found in memcache for PHPSESSID {}", sessionId);
                }
            }
        }

        super.doFilter(req, res, chain);

    }

    /*
     * Putting Active user details in session that would enable controllers to
     * access the same. So no need to redirect to the url asked just let it pass
     * through filter chain.
     */
    @Override
    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult) {
        SecurityContextUtils.putActiveUserInSession(request, authResult);
    }
}
