package com.proptiger.data.notification.model.payload;

import java.util.ArrayList;
import java.util.List;

import com.proptiger.data.model.BaseModel;

public class NotificationMessagePayload extends BaseModel{

    /**
     * 
     */
    private static final long serialVersionUID = 6554304335434986632L;
    
    private NotificationTypePayload notificationTypePayload;
    
    private List<NotificationMessagePayload> notificationMessagePayloads = new ArrayList<NotificationMessagePayload>();
    
    private List<NotificationMessageUpdateHistory> notificationMessageUpdateHistories = new ArrayList<NotificationMessageUpdateHistory>();

    public NotificationTypePayload getNotificationTypePayload() {
        return notificationTypePayload;
    }

    public void setNotificationTypePayload(NotificationTypePayload notificationTypePayload) {
        this.notificationTypePayload = notificationTypePayload;
    }

    public List<NotificationMessagePayload> getNotificationMessagePayloads() {
        return notificationMessagePayloads;
    }

    public void setNotificationMessagePayloads(List<NotificationMessagePayload> notificationMessagePayloads) {
        this.notificationMessagePayloads = notificationMessagePayloads;
    }

    public List<NotificationMessageUpdateHistory> getNotificationMessageUpdateHistories() {
        return notificationMessageUpdateHistories;
    }

    public void setNotificationMessageUpdateHistories(
            List<NotificationMessageUpdateHistory> notificationMessageUpdateHistories) {
        this.notificationMessageUpdateHistories = notificationMessageUpdateHistories;
    }
}
