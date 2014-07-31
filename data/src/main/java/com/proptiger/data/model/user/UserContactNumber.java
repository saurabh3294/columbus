package com.proptiger.data.model.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */

@Entity(name = "user.user_contact_numbers")
public class UserContactNumber extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "user_id")
    private int               userId;

    @Column(name = "contact_number")
    private String            contactNumber;

    private int               priority;

    @Column(name = "created_by")
    private int               createdBy;

    @Column(name = "created_at")
    private Date              createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contact_number) {
        this.contactNumber = contact_number;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}