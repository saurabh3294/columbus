package com.proptiger.data.model.user;

import com.proptiger.data.internal.dto.RegisterUser;

public class UserDetails extends RegisterUser {

    private static final long serialVersionUID = 255485230112121756L;
    
    private String            oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}
