package com.proptiger.data.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proptiger.data.notification.generator.NotificationGenerator;
import com.proptiger.data.notification.generator.NotificationMessageGenerator;
import com.proptiger.data.notification.generator.NotificationTypeGenerator;
import com.proptiger.data.notification.schedular.NotificationScheduler;
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
    
    private static int                   EMAIL_NOTIFICATION_MEDIUM_ID = 1;

    @Autowired
    private NotificationTypeGenerator    notificationTypeGenerator;

    @Autowired
    private NotificationMessageGenerator notificationMessageGenerator;

    @Autowired
    private NotificationGenerator        notificationGenerator;

    @Autowired
    private NotificationScheduler        notificationSchedular;

    @Autowired
    private NotificationSender           notificationSender;

    /**
     * Generates the Notification Types from events at regular intervals
     */
    //@Scheduled(fixedDelay = 600000)
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
    //@Scheduled(fixedDelay = 600000)
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
    @Scheduled(fixedDelay=30000)
    public void notificationGenerator() {
        Thread.currentThread().setName("Notification Generator");
        logger.info("NotificationGenerator : Initiating Notification Generation.");
        Integer numberOfNotifications = notificationGenerator.generateNotifications();
        logger.info(" Number of Notification Generated are : " +  numberOfNotifications);
    }
    
    /**
     * Get all the Notifications with NotificationGenerated status and mark them
     * as Scheduled with appropriate Schedule time
     */
    //@Scheduled(fixedDelay = 600000)
    public void notificationSchedular() {
        logger.info("NotificationSchedular: Scheduling Generated Notification.");
        Integer numberOfScheduledNtGenerated = notificationSchedular.scheduleNotifications();
        logger.info("NotificationSchedular: Scheduled " + numberOfScheduledNtGenerated + " Generated Notifications");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    //@Scheduled(cron = "* 10 8-22 * * *")
    public void emailNotificationSender() {
        logger.info("NotificationSender : Sending Scheduled Generated Notification via Email.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(EMAIL_NOTIFICATION_MEDIUM_ID);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated + " Generated Notifications via Email");
    }

}
