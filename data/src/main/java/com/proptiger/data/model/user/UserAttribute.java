package com.proptiger.data.model.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "user.user_attributes")
public class UserAttribute extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3839054993336339162L;
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @Column(name = "user_id")
    @JsonIgnore
    private int userId;
    
    @Column(name = "attribute_name")
    private String attributeName;
    
    @Column(name = "attribute_value")
    private String attributeValue;
    
    @Column(name = "created_at")
    @JsonIgnore
    private Date createdAt;
    
    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;
    
    @PreUpdate
    public void populateUpdateFields(){
        this.updatedAt = new Date();
    }
    
    @PrePersist
    public void populatePersistFields(){
        this.createdAt = new Date();
        populateUpdateFields();
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

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
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
