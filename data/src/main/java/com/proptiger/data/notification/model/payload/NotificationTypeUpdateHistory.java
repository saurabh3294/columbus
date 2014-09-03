package com.proptiger.data.notification.model.payload;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.notification.enums.NotificationStatus;

public class NotificationTypeUpdateHistory {

    private NotificationStatus notificationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date               updatedDate;

    public NotificationTypeUpdateHistory() {

    }

    public NotificationTypeUpdateHistory(NotificationStatus notificationStatus, Date updateDate) {
        this.notificationStatus = notificationStatus;
        this.updatedDate = updateDate;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
