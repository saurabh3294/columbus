package com.proptiger.data.model.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;

import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "user.user_emails")
public class UserEmail extends BaseModel {
    private static final long serialVersionUID     = 1L;

    public static final int   primaryEmailPriority = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               id;

    @Column(name = "user_id")
    private int               userId;

    @Email
    private String            email;

    private int               priority             = primaryEmailPriority;

    @Column(name = "created_by")
    private Integer               createdBy;

    @Column(name = "created_at")
    private Date              createdAt ;

    public UserEmail() {

    }

    public UserEmail(String email, int userId) {
        this.email = email;
        this.userId = userId;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    @PrePersist
    public void prePersist(){
        this.createdAt = new Date();
    }
}
