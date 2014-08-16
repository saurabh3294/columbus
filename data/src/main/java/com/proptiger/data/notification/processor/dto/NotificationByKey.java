package com.proptiger.data.notification.processor.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.NotFileFilter;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;

public class NotificationByKey implements Serializable{
    private String objectTypeName;
    private Object objectId;
    private List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();
    private List<NotificationGenerated> notificationGenerateds = new ArrayList<NotificationGenerated>();
    
    public String getObjectTypeName() {
        return objectTypeName;
    }
    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }
    public Object getObjectId() {
        return objectId;
    }
    public void setObjectId(Object objectId) {
        this.objectId = objectId;
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
}
