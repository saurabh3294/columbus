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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Entity
@Table(name = "notification_type_generated")
public class NotificationTypeGenerated extends BaseModel {

    /**
     * 
     */
    private static final long       serialVersionUID = -7790921100550942961L;

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int                     id;

    @Column(name = "data")
    private String                  data;

    private EventGenerated          eventGenerated;

    @OneToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType        notificationType;

    @Column(name = "notification_status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus      notificationStatus;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date                    createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                    updatedAt;

    @Transient
    private NotificationTypePayload notificationTypePayload;

    @PreUpdate
    public void autoUpdateFields() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void autoPopulateFields() {
        this.createdAt = new Date();
        this.notificationStatus = NotificationStatus.NotificationTypeGenerated;
        autoUpdateFields();
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

    public EventGenerated getEventGenerated() {
        return eventGenerated;
    }

    public void setEventGenerated(EventGenerated eventGenerated) {
        this.eventGenerated = eventGenerated;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdDate) {
        this.createdAt = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedAt;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedAt = updatedDate;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public NotificationTypePayload getNotificationTypePayload() {
        return notificationTypePayload;
    }

    public void setNotificationTypePayload(NotificationTypePayload notificationTypePayload) {
        this.notificationTypePayload = notificationTypePayload;
    }

}
