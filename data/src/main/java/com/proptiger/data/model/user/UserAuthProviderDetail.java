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

@Entity(name = "user_auth_provider_details")
public class UserAuthProviderDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "user_id")
    private int               userId;

    @Column(name = "provider_id")
    private int               providerId;

    @Column(name = "provider_user_id")
    private String            providerUserId;

    @Column(name = "image_url")
    private String            imageUrl;

    @Column(name = "all_details")
    private String            allDetails;

    @Column(name = "created_at")
    private Date              createdAt;

    @Column(name = "updated_at")
    private Date              updatedAt;

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

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAllDetails() {
        return allDetails;
    }

    public void setAllDetails(String allDetails) {
        this.allDetails = allDetails;
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