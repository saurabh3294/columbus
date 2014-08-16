package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;

public interface NotificationProcessor {
    public void processIntraMerging(
            NotificationByKeyDto notificationByKey);
    
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey);

    public List<NotificationMessage> processInterMerging(List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

    public List<NotificationMessage> processInterSuppressing(List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications);

}
