package com.proptiger.data.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.notification.generator.NotificationGenerator;
import com.proptiger.data.notification.generator.NotificationMessageGenerator;
import com.proptiger.data.notification.generator.NotificationTypeGenerator;
import com.proptiger.data.notification.sender.NotificationSender;

/**
 * It is responsible for generating Notifications. Functions of this class are
 * called at regular intervals in order to generate Notifications
 * 
 * @author sahil
 */

@Component
public class NotificationInitiator {

    private static Logger                logger = LoggerFactory.getLogger(NotificationInitiator.class);

    @Autowired
    private NotificationTypeGenerator    notificationTypeGenerator;

    @Autowired
    private NotificationMessageGenerator notificationMessageGenerator;

    @Autowired
    private NotificationGenerator        notificationGenerator;

	@Autowired
    private NotificationSender           notificationSender;

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

    /**
     * Generates the Notification Messages from NotificationTypes at regular
     * intervals
     */
    public void notificationMessageGenerator() {

        if (!notificationMessageGenerator.isNotificationMessageGenerationRequired()) {
            logger.info("NotificationMessageGenerator: Skipping NotificationMessage Generation.");
            return;
        }

        logger.info("NotificationMessageGenerator: Generating Notification Messages.");
        Integer numberOfNotificationMessages = notificationMessageGenerator.generateNotificationMessages();
        logger.info("NotificationMessageGenerator: Generated " + numberOfNotificationMessages
                + " NotificationMessages.");
    }

    /**
     * Generates the NotificationGenerated from NotificationMessages at regular
     * intervals
     */
    public void notificationGenerator() {
        Integer numberOfNotifications = notificationGenerator.generateNotifications();
    }
    
    /**
     * Send Notification Generated which are scheduled and Ready
     * to be send in the respective medium
     */
    public void sendNotification () {
        logger.info("NotificationSender : Sending Generated Notification.");
        Integer numberOfSendNtGenerated = notificationSender.sendNotification();
        logger.info("Notification Sender: Send " + numberOfSendNtGenerated + " Generated Notifications");
    }

}
