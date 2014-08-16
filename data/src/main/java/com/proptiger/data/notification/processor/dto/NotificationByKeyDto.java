package com.proptiger.data.notification.processor.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;

public class NotificationByKeyDto implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -6433846292647851701L;
    
    private String objectTypeName;
    private Object objectId;
    private List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();
    private List<NotificationGenerated> notificationGenerateds = new ArrayList<NotificationGenerated>();
    private List<NotificationMessage> discardedMessage = new ArrayList<NotificationMessage>();
    private Map<NotificationStatus, List<NotificationGenerated>> discardGeneratedMap = new HashMap<NotificationStatus, List<NotificationGenerated>>();
    
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
    public List<NotificationMessage> getDiscardedMessage() {
        return discardedMessage;
    }
    public void setDiscardedMessage(List<NotificationMessage> discardedMessage) {
        this.discardedMessage = discardedMessage;
    }
    public Map<NotificationStatus, List<NotificationGenerated>> getDiscardGeneratedMap() {
        return discardGeneratedMap;
    }
    public void setDiscardGeneratedMap(Map<NotificationStatus, List<NotificationGenerated>> discardGeneratedMap) {
        this.discardGeneratedMap = discardGeneratedMap;
    }
}
