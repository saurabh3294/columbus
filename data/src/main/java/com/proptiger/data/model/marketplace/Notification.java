package com.proptiger.data.model.marketplace;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.proptiger.data.annotations.ExcludeFromBeanCopy;
import com.proptiger.data.model.BaseModel;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.notifications")
public class Notification extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ExcludeFromBeanCopy
    private int               id;

    @JsonIgnore
    @Column(name = "user_id")
    @ExcludeFromBeanCopy
    private int               userId;

    @JsonIgnore
    @Column(name = "notification_type_id")
    @ExcludeFromBeanCopy
    private int               notificationTypeId;

    @ExcludeFromBeanCopy
    @Column(name = "object_id")
    private int               objectId;

    @Transient
    @ExcludeFromBeanCopy
    private JsonNode          details;

    @JsonIgnore
    @ExcludeFromBeanCopy
    @Column(name = "details")
    private String            stringDetails;

    @Column(name = "is_read")
    private boolean           read;

    @Column(name = "created_at")
    @ExcludeFromBeanCopy
    private Date              createdAt;

    @Column(name = "updated_at")
    @ExcludeFromBeanCopy
    private Date              updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_type_id", insertable = false, updatable = false)
    private MarketplaceNotificationType  notificationType;

    @PrePersist
    private void prePersist() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = new Date();
    }

    @PostLoad
    public void changeStringDetailsToJsonDetails() {
        if (stringDetails != null) {
            try {
                details = JsonLoader.fromString(stringDetails);
            }
            catch (IOException e) {
                throw new ProAPIException("Error Converting String to JSON in Notification Model", e);
            }
        }
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

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(int notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public JsonNode getDetails() {
        return details;
    }

    public void setDetails(JsonNode details) {
        this.details = details;
        this.stringDetails = details.toString();
    }

    public String getStringDetails() {
        return stringDetails;
    }

    public void setStringDetails(String stringDetails) {
        this.stringDetails = stringDetails;
        changeStringDetailsToJsonDetails();
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public MarketplaceNotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(MarketplaceNotificationType notificationType) {
        this.notificationType = notificationType;
    }
}