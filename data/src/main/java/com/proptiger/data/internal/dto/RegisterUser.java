package com.proptiger.data.internal.dto;

import com.proptiger.core.model.user.User;


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

    //TODO Just to make sure register api works for ios app, as they are using userName instead of fullName. Should be removed 
    @Deprecated
    private String userName;

    //overriding this field as it's marked as JsonIgnore in User class
    private String password;
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getUserName() {
        return super.getFullName();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        super.setFullName(userName);
    }

    public User createUserObj(){
        User user = new User();
        user.setAttributes(this.getAttributes());
        user.setCountryId(this.getCountryId());
        user.setEmail(this.getEmail());
        user.setEmails(this.getEmails());
        user.setFullName(this.getFullName());
        user.setPassword(this.getPassword());
        user.setRegistered(true);
        user.setContactNumbers(this.getContactNumbers());
        return user;
    }
}
