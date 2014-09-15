package com.proptiger.data.model.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "user.user_contact_numbers")
public class UserContactNumber extends BaseModel {
    private static final long serialVersionUID       = 1L;

    public static final int   primaryContactPriority = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               id;

    @Column(name = "user_id")
    private int               userId;

    @Column(name = "contact_number")
    private String            contactNumber;

    private int               priority               = primaryContactPriority;

    @Column(name = "created_by")
    private int               createdBy;

    @Column(name = "created_at")
    private Date              createdAt              = new Date();

    public UserContactNumber() {
    }

    public UserContactNumber(String contactNumber, int userId) {
        this.userId = userId;
        this.contactNumber = contactNumber;
        this.createdBy = userId;
    }

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