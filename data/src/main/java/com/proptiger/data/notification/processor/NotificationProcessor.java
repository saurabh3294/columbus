package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;

public interface NotificationProcessor {
    public List<NotificationMessage> processIntraMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications);
    
    public List<NotificationMessage> processIntraSuppressing(List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications);

    public List<NotificationMessage> processInterMerging(List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

    public List<NotificationMessage> processInterSuppressing(List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

}
