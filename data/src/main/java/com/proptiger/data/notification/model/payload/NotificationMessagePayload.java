package com.proptiger.data.notification.model.payload;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.proptiger.data.model.BaseModel;

public class NotificationMessagePayload extends BaseModel{

    /**
     * 
     */
    private static final long serialVersionUID = 6693378653337352914L;

    private NotificationTypePayload notificationTypePayload;
    
    private Map<String, Object>     payloadDataMap;
	private List<NotificationMessagePayload> notificationMessagePayloads = new ArrayList<NotificationMessagePayload>();
    
    private List<NotificationMessageUpdateHistory> notificationMessageUpdateHistories = new ArrayList<NotificationMessageUpdateHistory>();

    public Map<String, Object> getPayloadDataMap() {
        return payloadDataMap;
    }

    public void setPayloadDataMap(Map<String, Object> payloadDataMap) {
        this.payloadDataMap = payloadDataMap;
    }

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
