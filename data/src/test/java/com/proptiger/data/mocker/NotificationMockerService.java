package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeConfig;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

/**
 * 
 * @author sahil
 *
 */
@Service
public class NotificationMockerService {
    
public static final List<String> NOTIFICATION_TYPES = new ArrayList<String>();
    
    static {
        NOTIFICATION_TYPES.add("mockNotificationTypeName");
    }
    
    public List<NotificationType> getMockNotificationTypes() {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(369);
        notificationType.setName(NOTIFICATION_TYPES.get(0));
        notificationType.setNotificationTypeConfig(getMockNotificationTypeConfig());
        
        List<NotificationType> notificationTypes = new ArrayList<NotificationType>();
        notificationTypes.add(notificationType);
        return notificationTypes;
    }
    
    public NotificationTypeConfig getMockNotificationTypeConfig() {
        NotificationTypeConfig config = new NotificationTypeConfig();
        config.setNotificationTypePayloadObject(new NotificationTypePayload());
        return config;
    }
}
