package com.proptiger.data.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.security.UserRole;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.service.ApplicationNameService;

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
            /*
             * session will be valid for SESSION_MAX_INTERACTIVE_INTERVAL value,
             * this should be same as of cookie life time, so both should be
             * synched.
             */
            request.getSession().setMaxInactiveInterval(
                    PropertyReader.getRequiredPropertyAsType(
                            PropertyKeys.SESSION_MAX_INTERACTIVE_INTERVAL,
                            Integer.class));
        }
        return activeUser;
    }

    private static Authentication createNewAuthentication(User user) {
        Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
        UserDetails userDetails = new ActiveUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                getDefaultAuthority(),
                applicationType);

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
     * This method will put active user in request session too, to enable
     * controllers to get active user object
     * 
     * @param user
     * @return
     */
    public static Authentication autoLogin(User user) {
        Authentication auth = createNewAuthentication(user);
        putAuthInContext(auth);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        putActiveUserInSession(request, auth);
        return auth;
    }

    /**
     * Util metod to create and set Authentication object in security context,
     * this method is not responsible to put Authentication object in session,
     * caller must explicitly put the same in request session
     * 
     * @param activeUser
     * @return
     */
    public static Authentication autoLogin(ActiveUser activeUser) {
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                activeUser,
                null,
                activeUser.getAuthorities());
        putAuthInContext(newAuthentication);
        return newAuthentication;
    }

    private static void putAuthInContext(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static int getLoggedInUserId() {
        return Integer.parseInt(getLoggedInUser().getUserId());
    }

    public static void setAuthentication(Authentication newAuth) {
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public static List<GrantedAuthority> getDefaultAuthority() {
        List<GrantedAuthority> authority = new ArrayList<>();
        authority.add(new SimpleGrantedAuthority(UserRole.USER.name()));
        return authority;
    }

    /**
     * This method grants USER role to the currently logged in user after otp
     * validation
     */
    public static Authentication grantUserAuthorityToActiveUser() {
        Authentication auth = SecurityContextUtils.getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                authorities);
        SecurityContextUtils.setAuthentication(newAuth);
        return newAuth;
    }
    
    /**
     * This method grants PRE_AUTH_USER role to the currently logged in user after otp
     * validation
     */
    public static Authentication grantPreAuthAuthority(Authentication auth) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(UserRole.PRE_AUTH_USER.name()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                authorities);
        SecurityContextUtils.setAuthentication(newAuth);
        return newAuth;
    }
}
