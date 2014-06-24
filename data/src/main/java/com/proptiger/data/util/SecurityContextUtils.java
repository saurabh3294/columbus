package com.proptiger.data.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.proptiger.data.internal.dto.ActiveUser;

/**
 * Security utils to get current logged in user
 * @author Rajeev Pandey
 *
 */
public class SecurityContextUtils {
    
    /**
     * @return ActiveUser object or null if user is not logged in
     */
    public static ActiveUser getLoggedInUser(){
        ActiveUser activeUser = null;
        Object activeUserObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(activeUserObj instanceof ActiveUser){
            activeUser = (ActiveUser)activeUserObj;
        }
        return activeUser;
    }
}
