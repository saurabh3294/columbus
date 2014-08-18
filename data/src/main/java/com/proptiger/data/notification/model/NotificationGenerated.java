package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
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
import com.proptiger.data.notification.model.legacy.NotificationMediumOld;
import com.proptiger.data.notification.model.legacy.NotificationMessageOld;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Entity
@Table(name = "notification_generated")
public class NotificationGenerated extends BaseModel {

    /**
     * 
     */
    private static final long          serialVersionUID = -779686848270519833L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int                        id;

    @OneToOne
    @JoinColumn(name = "notification_message_id")
    private NotificationMessage        notificationMessage;
    
    @OneToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType           notificationType;

    @OneToOne
    @JoinColumn(name = "user_id")
    private ForumUser                  forumUser;

    @OneToOne
    @JoinColumn(name = "notification_medium_id")
    private NotificationMedium         notificationMedium;

    @Column(name = "data")
    private String                     data;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       updatedAt;

    @Column(name = "expiry_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       expiry_time;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus         notificationStatus;

    @Transient
    private NotificationMessagePayload notificationMessagePayload;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NotificationMessage getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(NotificationMessage notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public NotificationMedium getNotificationMedium() {
        return notificationMedium;
    }

    public void setNotificationMedium(NotificationMedium notificationMedium) {
        this.notificationMedium = notificationMedium;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public Date getExpiry_time() {
        return expiry_time;
    }

    public void setExpiry_time(Date expiry_time) {
        this.expiry_time = expiry_time;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public NotificationMessagePayload getNotificationMessagePayload() {
        return notificationMessagePayload;
    }

    public void setNotificationMessagePayload(NotificationMessagePayload notificationMessagePayload) {
        this.notificationMessagePayload = notificationMessagePayload;
    }

    public ForumUser getForumUser() {
        return forumUser;
    }

    public void setForumUser(ForumUser forumUser) {
        this.forumUser = forumUser;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}
