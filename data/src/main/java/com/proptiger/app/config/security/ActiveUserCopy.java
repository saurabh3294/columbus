package com.proptiger.app.config.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.Application;
import com.proptiger.core.enums.security.UserRole;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.util.SecurityContextUtils;

/**
 * This class is being used to deserialize ActiveUser instance and create a
 * object of ActiveUser
 * 
 * @author Rajeev Pandey
 *
 */
public class ActiveUserCopy extends BaseModel {
    private static final long           serialVersionUID = -3316374911598633399L;
    private Integer                     userIdentifier;
    private String                      fullName;
    private Application                 applicationType  = Application.DEFAULT;
    private String                      password;
    private String                      username;
    private Set<CustomGrantedAuthority> authorities;
    private boolean                     accountNonExpired;
    private boolean                     accountNonLocked;
    private boolean                     credentialsNonExpired;
    private boolean                     enabled;

    public Integer getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(Integer userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Application getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(Application applicationType) {
        this.applicationType = applicationType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authority = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            for (CustomGrantedAuthority customGrantedAuthority : authorities) {
                authority.add(new SimpleGrantedAuthority(customGrantedAuthority.getAuthority()));
            }
        }
        else {
            authority.add(new SimpleGrantedAuthority(UserRole.USER.name()));
        }
        return authority;
    }

    public void setAuthorities(Set<CustomGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class CustomGrantedAuthority {
        private String authority;

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

    }

    public ActiveUser toActiveUser() {
        return new ActiveUser(
                this.getFullName(),
                this.getUserIdentifier(),
                this.getUsername(),
                this.getPassword(),
                this.isEnabled(),
                this.isAccountNonExpired(),
                this.isCredentialsNonExpired(),
                this.isAccountNonLocked(),
                this.getAuthorities(),
                this.getApplicationType());
    }
}
