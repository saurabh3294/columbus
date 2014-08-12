package com.proptiger.data.notification.model.payload;

import com.proptiger.data.model.BaseModel;

public class NotificationMessagePayload extends BaseModel{

    /**
     * 
     */
    private static final long serialVersionUID = 6554304335434986632L;
    
    private NotificationTypePayload notificationTypePayload;

    public NotificationTypePayload getNotificationTypePayload() {
        return notificationTypePayload;
    }

    public void setNotificationTypePayload(NotificationTypePayload notificationTypePayload) {
        this.notificationTypePayload = notificationTypePayload;
    }
}
