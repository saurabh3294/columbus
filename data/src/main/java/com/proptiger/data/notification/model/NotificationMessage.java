package com.proptiger.data.notification.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Entity
@Table(name = "notification.notification_message")
public class NotificationMessage extends BaseModel {

    /**
     * 
     */
    private static final long          serialVersionUID = 5129143086430525445L;

    @Id
    @Column(name = "id")
    @GeneratedValue
    private int                        id;

    @Column(name = "data")
    private String                     data;

    @Column(name = "notification_type_generated_id")
    private Integer                    notificationTypeGeneratedId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type_id")
    private NotificationType           notificationType;

    @Column(name = "user_id")
    private Integer                    userId;

    @Transient
    private NotificationMessagePayload notificationMessagePayload;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus         notificationStatus;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       updatedAt;

    @PreUpdate
    public void autoUpdateFields() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void autoPopulateFields() {
        this.createdAt = new Date();
        if (this.notificationStatus == null) {
            this.notificationStatus = NotificationStatus.MessageGenerated;
        }
        autoUpdateFields();
    }

    public NotificationMessage() {
            // TODO Auto-generated constructor stub
    }

    public NotificationMessage(Integer userId, NotificationMessagePayload payload, NotificationType type) {
        this.userId = userId;
        this.notificationMessagePayload = payload;
        this.notificationType = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NotificationMessagePayload getNotificationMessagePayload() {
        return notificationMessagePayload;
    }

    public void setNotificationMessagePayload(NotificationMessagePayload notificationMessagePayload) {
        this.notificationMessagePayload = notificationMessagePayload;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
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

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Integer getNotificationTypeGeneratedId() {
        return notificationTypeGeneratedId;
    }

    public void setNotificationTypeGeneratedId(Integer notificationTypeGeneratedId) {
        this.notificationTypeGeneratedId = notificationTypeGeneratedId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
