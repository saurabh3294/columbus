package com.proptiger.data.notification.model;

import java.util.Date;

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

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Entity
@Table(name = "notification_generated")
public class NotificationGenerated extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7829394463604901590L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int                        id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_message_id")
    private NotificationMessage        notificationMessage;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_medium_id")
    private NotificationMedium         notificationMedium;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type_id")
    private NotificationType           notificationType;

    @Column(name = "user_id")
    private Integer                    userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ForumUser                  forumUser;

    @Column(name = "data")
    private String                     data;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       updatedAt;

    @Column(name = "schedule_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       scheduleTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus         notificationStatus;

    @Column(name = "merged_notification_message_id")
    private Integer                    mergeNotificationMessageId;

    @Transient
    private NotificationMessagePayload notificationMessagePayload;

    @Column(name = "object_id")
    private Integer                    objectId = 0;

    @PreUpdate
    public void populatePreUpdateFields() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void populatePrePersistFields() {
        this.createdAt = new Date();
        this.notificationStatus = NotificationStatus.Generated;
        populatePreUpdateFields();
    }

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

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
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

    public Integer getObjectId() {
        return objectId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMergeNotificationMessageId() {
        return mergeNotificationMessageId;
    }

    public void setMergeNotificationMessageId(Integer mergeNotificationMessageId) {
        this.mergeNotificationMessageId = mergeNotificationMessageId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
}
