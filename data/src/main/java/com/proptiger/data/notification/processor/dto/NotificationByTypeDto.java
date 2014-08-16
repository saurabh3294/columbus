package com.proptiger.data.notification.processor.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;

public class NotificationByTypeDto implements Serializable {

    private String                      notificationTypeName;
    private Map<Object, NotificationByKey>     notificationMessageByKeys = new LinkedHashMap<Object, NotificationByKey>();
    private List<NotificationMessage>   notificationMessages;
    private List<NotificationGenerated> notificationGenerateds;
    
    public String getNotificationTypeName() {
        return notificationTypeName;
    }

    public void setNotificationTypeName(String notificationTypeName) {
        this.notificationTypeName = notificationTypeName;
    }

    public List<NotificationMessage> getNotificationMessages() {
        return notificationMessages;
    }

    public void setNotificationMessages(List<NotificationMessage> notificationMessages) {
        this.notificationMessages = notificationMessages;
    }

    public List<NotificationGenerated> getNotificationGenerateds() {
        return notificationGenerateds;
    }

    public void setNotificationGenerateds(List<NotificationGenerated> notificationGenerateds) {
        this.notificationGenerateds = notificationGenerateds;
    }

    public Map<Object, NotificationByKey> getNotificationMessageByKeys() {
        return notificationMessageByKeys;
    }

    public void setNotificationMessageByKeys(Map<Object, NotificationByKey> notificationMessageByKeys) {
        this.notificationMessageByKeys = notificationMessageByKeys;
    }

    
}
