package com.proptiger.data.internal.dto;

import com.proptiger.data.model.user.User;

/**
 * Registration DTO
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public class RegisterUser extends User {
    private static final long serialVersionUID = 5973266034024200507L;

    private String            confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public User createUserObj(){
        User user = new User();
        user.setAttributes(this.getAttributes());
        user.setCountryId(this.getCountryId());
        user.setEmail(this.getEmail());
        user.setEmails(this.getEmails());
        user.setFullName(this.getFullName());
        user.setPassword(this.getPassword());
        user.setRegistered(this.isRegistered());
        user.setContactNumbers(this.getContactNumbers());
        return user;
    }
}
