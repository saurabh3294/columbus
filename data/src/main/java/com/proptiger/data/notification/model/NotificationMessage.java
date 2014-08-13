package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Entity
@Table(name = "notification_message")
public class NotificationMessage extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 4800603265035626921L;
    
    @Id
    @Column(name = "id")
    private int id;
        
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "notification_generated_id", updatable = false)
    private NotificationTypeGenerated notificationTypeGenerated;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private ForumUser forumUser;
    
    @Transient
    private NotificationMessagePayload notificationMessagePayload;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NotificationTypeGenerated getNotificationTypeGenerated() {
        return notificationTypeGenerated;
    }

    public void setNotificationTypeGenerated(NotificationTypeGenerated notificationTypeGenerated) {
        this.notificationTypeGenerated = notificationTypeGenerated;
    }

    public ForumUser getForumUser() {
        return forumUser;
    }

    public void setForumUser(ForumUser forumUser) {
        this.forumUser = forumUser;
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
    
}
