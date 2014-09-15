package com.proptiger.data.model.user;

import java.util.Date;
import java.util.List;

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

import com.proptiger.data.internal.dto.Register;
import com.proptiger.data.model.BaseModel;

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

    private String                       password;

    @Column(name = "country_id")
    private Integer                      countryId;

    @Column(name = "is_registered")
    private boolean                      registered       = true;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserEmail>              emails;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserContactNumber>      contactNumbers;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserAuthProviderDetail> userAuthProviderDetails;

    @Column(name = "created_at")
    private Date                         createdAt        = new Date();

    @Column(name = "updated_at")
    private Date                         updatedAt = new Date();

    @Column(name = "email")
    private String email;


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

    public List<UserContactNumber> getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(List<UserContactNumber> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public List<UserAuthProviderDetail> getUserAuthProviderDetails() {
        return userAuthProviderDetails;
    }

    public void setUserAuthProviderDetails(List<UserAuthProviderDetail> userAuthProviderDetails) {
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

    public void copyFieldsFromRegisterToUser(Register register) {
        this.setFullName(register.getUserName());
        this.setPassword(register.getPassword());
        this.setCountryId(register.getCountryId());
        this.setRegistered(register.getRegisterMe());
        this.setEmail(register.getEmail());
    }
}