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
    public List<NotificationMessage> processInterMerging(
            List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NotificationMessage> processInterSuppressing(
            List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NotificationMessage> processIntraMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NotificationMessage> processIntraSuppressing(
            List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
