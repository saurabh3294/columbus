package com.proptiger.data.model.user;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.internal.dto.RegisterUser;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "user.users")
public class User extends BaseModel {
    private static final long            serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                          id;

    @Column(name = "full_name")
    private String                       fullName;

    @JsonIgnore
    private String                       password;

    @Column(name = "country_id")
    private Integer                      countryId;

    @Column(name = "is_registered")
    private boolean                      registered;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserEmail>              emails;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private Set<UserContactNumber>      contactNumbers;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private Set<UserAuthProviderDetail> userAuthProviderDetails;

    @Column(name = "created_at")
    private Date                         createdAt;

    @Column(name = "updated_at")
    private Date                         updatedAt;

    @Column(name = "email")
    private String email;
    
    @Column(name = "verified")
    private boolean verified;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserAttribute>         attributes;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public List<UserEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<UserEmail> emails) {
        this.emails = emails;
    }

    public Set<UserContactNumber> getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(Set<UserContactNumber> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public Set<UserAuthProviderDetail> getUserAuthProviderDetails() {
        return userAuthProviderDetails;
    }

    public void setUserAuthProviderDetails(Set<UserAuthProviderDetail> userAuthProviderDetails) {
        this.userAuthProviderDetails = userAuthProviderDetails;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isContactPresent(String userContactNumber) {
        if (contactNumbers != null) {
            for (UserContactNumber contactNumber : contactNumbers) {
                if (contactNumber.getContactNumber().equals(userContactNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    public UserContactNumber getContactByContactNumber(String contactNumber) {
        UserContactNumber matchedContactNumber = null;
        if (contactNumbers != null) {
            for (UserContactNumber number : this.contactNumbers) {
                if (number.getContactNumber().equals(contactNumber)) {
                    matchedContactNumber = number;
                }
            }
        }
        return matchedContactNumber;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public void copyFieldsFromRegisterToUser(RegisterUser register) {
        this.setFullName(register.getFullName());
        this.setPassword(register.getPassword());
        this.setCountryId(register.getCountryId());
        this.setRegistered(true);
        this.setEmail(register.getEmail());
    }
    
    public static class WhoAmIDetail extends BaseModel{
        private static final long serialVersionUID = 708536340494027592L;
        private String userName;
        private String imageUrl;
        public WhoAmIDetail(String userName, String avatar) {
            super();
            this.userName = userName;
            this.imageUrl = avatar;
        }
        public String getUserName() {
            return userName;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String url) {
            this.imageUrl = url;
        }
    }
	public List<UserAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<UserAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getProfileImageUrl() {
        if (this.userAuthProviderDetails != null && !this.userAuthProviderDetails.isEmpty()) {
            for (UserAuthProviderDetail authProviderDetail : userAuthProviderDetails) {
                if (authProviderDetail.getImageUrl() != null && !authProviderDetail.getImageUrl().isEmpty()) {
                    return authProviderDetail.getImageUrl();
                }
            }
        }
        return null;
    }
    
    public String getPriorityContactNumber() {
        String contact = "";
        if (this.contactNumbers != null && !this.contactNumbers.isEmpty()) {
            Iterator<UserContactNumber> it = this.contactNumbers.iterator();
            contact = it.next().getContactNumber();
            while(it.hasNext()) {
                UserContactNumber userContact = it.next();
                if (userContact.getPriority() == UserContactNumber.primaryContactPriority) {
                    contact = userContact.getContactNumber();
                    break;
                }
            }
        }
        return contact;
    }
    
    public WhoAmIDetail createWhoAmI() {
        return  new WhoAmIDetail(this.fullName, getProfileImageUrl());
    }

    public ForumUser toForumUser() {
        ForumUser forumUser = new ForumUser();
        forumUser.setUserId(this.getId());
        forumUser.setUsername(this.getFullName());
        if(this.getCountryId() != null){
            forumUser.setCountryId(this.getCountryId());    
        }
        forumUser.setImage("");
        return forumUser;
    }
}