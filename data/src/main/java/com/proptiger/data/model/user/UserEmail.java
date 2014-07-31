package com.proptiger.data.model.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Email;

import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 *
 */

@Entity(name="user_emails")
public class UserEmail extends BaseModel{
    private static final long serialVersionUID = 1L;

    @Id
    private int id;
    
    @Column(name="user_id")
    private int user_id;
    
    @Email
    private String email;
    
    private int priority;
    
    @Column(name="created_by")
    private int createdBy;
    
    @Column(name="created_at")
    private Date createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
