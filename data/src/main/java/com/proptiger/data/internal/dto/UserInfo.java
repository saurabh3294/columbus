package com.proptiger.data.internal.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Rajeev Pandey
 * 
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -3022788419586557079L;
    private String            name;
    private Integer           userIdentifier;
    private String            sessionId;
    @JsonIgnore
    public boolean            admin            = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(Integer userId) {
        this.userIdentifier = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }

}
