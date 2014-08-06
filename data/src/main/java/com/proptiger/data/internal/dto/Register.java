package com.proptiger.data.internal.dto;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;

/**
 * Registration DTO
 * 
 * @author Rajeev Pandey
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

    public ForumUser createForumUserObject() {
        ForumUser forumUser = new ForumUser();
        forumUser.setUsername(this.userName);
        forumUser.setEmail(this.email);
        forumUser.setContact(this.contact);
        forumUser.setCountryId(this.countryId);
        forumUser.setPassword(this.password);
        //TODO these empty string should be removed once we alter forum_user table
        forumUser.setCity("");
        forumUser.setImage("");
        forumUser.setFbImageUrl("");
        forumUser.setProvider("");
        forumUser.setProviderid("");
        forumUser.setUniqueUserId("");
        forumUser.setStatus(ForumUser.USER_STATUS_ACTIVE);
        return forumUser;
    }
}
