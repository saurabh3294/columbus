package com.proptiger.data.internal.dto;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.user.User;

/**
 * Registration DTO
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public class Register extends BaseModel {
    private static final long serialVersionUID = 5973266034024200507L;

    private String            userName;
    private String            email;
    private Long              contact;
    private String            password;
    private String            confirmPassword;
    private Integer           countryId;
    private boolean           registerMe       = true;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getContact() {
        return contact;
    }

    public void setContact(Long contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean getRegisterMe() {
        return registerMe;
    }

    public void setRegisterMe(boolean registerMe) {
        this.registerMe = registerMe;
    }

    public User createUser() {
        User user = new User();
        user.setFullName(this.getUserName());
        user.setPassword(this.getPassword());
        user.setCountryId(this.getCountryId());
        user.setRegistered(this.getRegisterMe());
        user.setEmail(this.getEmail());
        return user;
    }
}
