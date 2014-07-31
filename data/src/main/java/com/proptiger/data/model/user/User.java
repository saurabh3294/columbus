package com.proptiger.data.model.user;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.proptiger.data.enums.Status;
import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "user.users")
public class User extends BaseModel {
    private static final long       serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                     id;

    @Column(name="full_name")
    private String                  fullName;

    private String                  password;

    @Column(name = "country_id")
    private Integer                     countryId;

    @Enumerated(EnumType.STRING)
    private Status                  status;

    private boolean                 subscribed;

    @OneToMany(mappedBy = "userId")
    private List<UserEmail>         emails;

    @OneToMany
    private List<UserContactNumber> contactNumbers;

    @Column(name = "created_at")
    private Date                    createdAt;

    @Column(name = "updated_at")
    private Date                    updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
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
}