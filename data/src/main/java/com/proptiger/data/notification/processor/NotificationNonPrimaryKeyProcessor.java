package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;

@Service
public class NotificationNonPrimaryKeyProcessor implements NotificationProcessor{

    @Override
    public void processIntraMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationMessage> mergedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void processIntraSuppressing(
            List<NotificationMessage> notificationMessages,
            List<NotificationMessage> suppressedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void processInterMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationMessage> mergedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void processInterSuppressing(
            List<NotificationMessage> notificationMessages,
            List<NotificationMessage> suppressedNotifications,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        
    }

}
