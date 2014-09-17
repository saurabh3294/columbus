package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private Random                   random             = new Random();

    public static final List<String> NOTIFICATION_TYPES = new ArrayList<String>();

    static {
        NOTIFICATION_TYPES.add("mockNotificationTypeName");
    }

    public List<NotificationType> getMockNotificationTypes() {
        List<NotificationType> notificationTypes = new ArrayList<NotificationType>();
        notificationTypes.add(getMockNotificationType(NOTIFICATION_TYPES.get(0)));
        return notificationTypes;
    }

    public NotificationType getMockNotificationType() {
        return getMockNotificationType(NOTIFICATION_TYPES.get(random.nextInt(NOTIFICATION_TYPES.size())));
    }

    private NotificationType getMockNotificationType(String notificationTypeName) {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(random.nextInt(1000) + 1);
        notificationType.setName(notificationTypeName);
        notificationType.setNotificationTypeConfig(getMockNotificationTypeConfig());
        return notificationType;
    }

    private NotificationTypeConfig getMockNotificationTypeConfig() {
        NotificationTypeConfig config = new NotificationTypeConfig();
        config.setNotificationTypePayloadObject(new NotificationTypePayload());
        return config;
    }
}
