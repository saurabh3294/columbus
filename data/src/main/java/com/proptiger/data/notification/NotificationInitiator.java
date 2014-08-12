package com.proptiger.data.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.notification.generator.NotificationTypeGenerator;

/**
 * It is responsible for generating Notifications. Functions of this class are
 * called at regular intervals in order to generate Notifications
 * 
 * @author sahil
 */

@Component
public class NotificationInitiator {

    private static Logger             logger = LoggerFactory.getLogger(NotificationInitiator.class);

    @Autowired
    private NotificationTypeGenerator notificationTypeGenerator;

    /**
     * Generates the Notification Types from events at regular intervals
     */
    public void notificationTypeGenerator() {

        if (!notificationTypeGenerator.isNotificationGenerationRequired()) {
            logger.info("NotificationTypeGenerator: Skipping NotificationType Generation.");
            return;
        }

        logger.info("NotificationTypeGenerator: Generating Notification Types.");
        Integer numberOfNotificationTypes = notificationTypeGenerator.generateNotificationTypes();
        logger.info("NotificationTypeGenerator: Generated " + numberOfNotificationTypes + " NotificationTypes.");
    }

}
