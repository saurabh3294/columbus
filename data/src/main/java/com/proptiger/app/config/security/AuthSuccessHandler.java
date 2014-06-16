package com.proptiger.app.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.util.Constants;

/**
 * Auth success handler to manage session and response after authentication. It
 * put the logged in user details to request session so that would be available
 * to controllers
 * 
 * @author Rajeev Pandey
 * 
 */
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public AuthSuccessHandler() {
        super();
    }

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws ServletException, IOException {

        ActiveUser userInfo = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof ActiveUser) {
            userInfo = (ActiveUser) principal;
            /*
             * putting in request session so it would be acessible to
             * controllers
             */
            request.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, userInfo);
        }
        clearAuthenticationAttributes(request);
        redirectStrategy.sendRedirect(request, response, determineTargetUrl(request, response));
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().equals(Constants.Security.LOGIN_URL)) {
            return Constants.Security.DEFAULT_TARGET_URL;
        }
        else {
            return request.getRequestURI();
        }
    }
}
