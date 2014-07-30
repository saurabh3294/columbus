package com.proptiger.data.internal.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    public boolean            admin            = false;

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

}
