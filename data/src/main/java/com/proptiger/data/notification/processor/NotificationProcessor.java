package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;

public interface NotificationProcessor {
    public void processIntraMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationMessage> mergedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);
    
    public void processIntraSuppressing(List<NotificationMessage> notificationMessages,
            List<NotificationMessage> suppressedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

    public void processInterMerging(List<NotificationMessage> notificationMessages,
            List<NotificationMessage> mergedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

    public void processInterSuppressing(List<NotificationMessage> notificationMessages,
            List<NotificationMessage> suppressedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

}
