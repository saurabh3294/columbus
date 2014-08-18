package com.proptiger.data.notification.model.payload;

import java.util.Date;

import com.proptiger.data.notification.enums.NotificationStatus;

public class NotificationMessageUpdateHistory {
    private NotificationStatus notificationStatus;
    private Date               updatedDate;

    public NotificationMessageUpdateHistory() {
        super();
    }

    public NotificationMessageUpdateHistory(NotificationStatus notificationStatus, Date updatedDate) {
        super();
        this.notificationStatus = notificationStatus;
        this.updatedDate = updatedDate;
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
