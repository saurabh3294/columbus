package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "sent_notification_log")
public class SentNotificationLog extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -5934928738940876352L;
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int               id;

    @Column(name = "notification_type_id")
    private int               notificationTypeId;

    @Column(name = "medium_type_id")
    private int               mediumTypeId;

    @Column(name = "user_id")
    private Integer               userId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdAt;

    public SentNotificationLog(int ntTypeId, int mediumTypeId, Integer userId, Date date) {
        this.notificationTypeId = ntTypeId;
        this.mediumTypeId = mediumTypeId;
        this.userId = userId;
        this.createdAt = date;
    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(int notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public int getMediumTypeId() {
        return mediumTypeId;
    }

    public void setMediumTypeId(int mediumTypeId) {
        this.mediumTypeId = mediumTypeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
