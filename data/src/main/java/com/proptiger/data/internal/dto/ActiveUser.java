package com.proptiger.data.internal.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.enums.Application;
import com.proptiger.data.service.ApplicationNameService;

/**
 * Active user details to hold principle and other authorities of user after
 * successful login. This is a wrapper over User class.
 * 
 * @author Rajeev Pandey
 * 
 */
public class ActiveUser extends SocialUser {
    private static final long serialVersionUID = -3022788419586557079L;
    private Integer           userIdentifier;
    //TODO should be removed once we define role for a user
    @JsonIgnore
    private Application applicationType = Application.DEFAULT;

    public ActiveUser(
            Integer id,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userIdentifier = id;
        this.applicationType = ApplicationNameService.getApplicationTypeOfRequest();
    }

    public Integer getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(Integer userId) {
        this.userIdentifier = userId;
    }
    
    @Override
    public String getUserId() {
        return userIdentifier.toString();
    }

    public Application getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(Application applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ActiveUser){
            return getUsername().equals(((ActiveUser)obj).getUsername());
        }
        return false;
    }

    
}
