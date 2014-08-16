package com.proptiger.data.notification.processor.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;

public class NotificationByTypeDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5283164851641487979L;
    private NotificationType                      notificationType;
    private Map<Object, NotificationByKeyDto>     notificationMessageByKeys = new LinkedHashMap<Object, NotificationByKeyDto>();
    private List<NotificationMessage>   notificationMessages;
    private List<NotificationGenerated> notificationGenerateds;
    
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

    public Map<Object, NotificationByKeyDto> getNotificationMessageByKeys() {
        return notificationMessageByKeys;
    }

    public void setNotificationMessageByKeys(Map<Object, NotificationByKeyDto> notificationMessageByKeys) {
        this.notificationMessageByKeys = notificationMessageByKeys;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    
}
