package com.proptiger.data.notification.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeConfig;
import com.proptiger.data.notification.repo.NotificationTypeDao;

@Service
public class NotificationTypeService {
    private static Logger      logger = LoggerFactory.getLogger(NotificationTypeService.class);

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private NotificationTypeDao notificationTypeDao;

    public NotificationType populateNotificationTypeConfig(NotificationType notificationType) {

        // Get the notificationTypeName whose config needs to be used
        String configName = notificationType.getName();
        if (notificationType.getOverwriteConfigName() != null) {
            configName = notificationType.getOverwriteConfigName();
        }

        // Find the config from the static map.
        // Use defaults if no config is found.
        NotificationTypeConfig savedNTConfig = NotificationTypeConfig.getNotificationTypeConfigMap().get(configName);
        if (savedNTConfig == null) {
            logger.error("NotificationType ID " + notificationType.getId()
                    + " do not have mapping of Notification Type Config. Using Defaults.");
            savedNTConfig = new NotificationTypeConfig();
        }

        savedNTConfig = setNotificationTypeConfigObjectAttributes(savedNTConfig);
        notificationType.setNotificationTypeConfig(savedNTConfig);
        return notificationType;
    }

    private NotificationTypeConfig setNotificationTypeConfigObjectAttributes(
            NotificationTypeConfig notificationTypeConfig) {

        try {
            notificationTypeConfig.setNotificationTypePayloadObject(notificationTypeConfig.getDataClassName()
                    .newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return notificationTypeConfig;
    }
    
    public Iterable<NotificationType> findAllNotificationTypes(){
        return notificationTypeDao.findAll();
    }
}
