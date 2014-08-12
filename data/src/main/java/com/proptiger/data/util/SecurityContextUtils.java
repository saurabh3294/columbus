package com.proptiger.data.util;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.ForumUser;

/**
 * Security utils to get current logged in user and session related work.
 * 
 * @author Rajeev Pandey
 *
 */
public class SecurityContextUtils {

    /**
     * @return ActiveUser object or null if user is not logged in
     */
    public static ActiveUser getLoggedInUser() {
        ActiveUser activeUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object activeUserObj = authentication.getPrincipal();
            if (activeUserObj instanceof ActiveUser) {
                activeUser = (ActiveUser) activeUserObj;
            }
        }

        return activeUser;
    }

    /**
     * This method should be called afer successful login.
     * 
     * @param request
     * @param authentication
     * @return ActiveUser
     */
    public static ActiveUser putActiveUserInSession(
            final HttpServletRequest request,
            final Authentication authentication) {
        Object principal = authentication.getPrincipal();
        ActiveUser activeUser = null;
        if (principal instanceof ActiveUser) {
            activeUser = (ActiveUser) principal;
            /*
             * putting in request session so it would be acessible to
             * controllers
             */
            request.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, activeUser);
        }
        return activeUser;
    }

    private static Authentication createNewAuthentication(ForumUser forumUser) {
        UserDetails userDetails = new ActiveUser(
                forumUser.getUserId(),
                forumUser.getEmail(),
                forumUser.getPassword(),
                true,
                true,
                true,
                true,
                new ArrayList<GrantedAuthority>());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        return newAuthentication;
    }

    /**
     * This method create new Authentication object from ForumUser object and
     * set that in SecurityContextHolder, so it would work like auto login.
     * 
     * @param forumUser
     * @return
     */
    public static Authentication autoLogin(ForumUser forumUser) {
        Authentication auth = createNewAuthentication(forumUser);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }
    
    public static Authentication autoLogin(ActiveUser activeUser) {
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                activeUser,
                null,
                activeUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return newAuthentication;
    }
    
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
