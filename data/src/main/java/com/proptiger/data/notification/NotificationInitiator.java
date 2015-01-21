package com.proptiger.data.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.config.scheduling.QuartzScheduledClass;
import com.proptiger.core.config.scheduling.QuartzScheduledJob;
import com.proptiger.core.enums.notification.MediumType;
import com.proptiger.data.notification.generator.NotificationGenerator;
import com.proptiger.data.notification.generator.NotificationMessageGenerator;
import com.proptiger.data.notification.generator.NotificationTypeGenerator;
import com.proptiger.data.notification.scheduler.NotificationScheduler;
import com.proptiger.data.notification.sender.NotificationSender;

/**
 * It is responsible for generating Notifications. Functions of this class are
 * called at regular intervals in order to generate Notifications
 * 
 * @author sahil
 */

@Component
@QuartzScheduledClass
public class NotificationInitiator {

    private static Logger                logger = LoggerFactory.getLogger(NotificationInitiator.class);

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
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification}",
            initialDelayString = "${scheduler.initialdelay.notification.notificationTypeGenerator}")
    public void notificationTypeGenerator() {

        logger.debug("NotificationInitiator started generating notificationType.");
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
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification}",
            initialDelayString = "${scheduler.initialdelay.notification.notificationMessageGenerator}")
    public void notificationMessageGenerator() {

        logger.debug("NotificationInitiator started generating notificationMessage.");
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
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification}",
            initialDelayString = "${scheduler.initialdelay.notification.notificationGenerator}")
    public void notificationGenerator() {
        Thread.currentThread().setName("Notification Generator");
        
        logger.debug("NotificationInitiator started generating notificationGenerated.");
        if (!notificationGenerator.isNotificationGeneratedGenerationRequired()) {
            logger.info("NotificationGeneratedGenerator: Skipping NotificationGenerated Generation.");
            return;
        }

        logger.info("NotificationGenerator : Initiating Notification Generation.");
        Integer numberOfNotifications = notificationGenerator.generateNotifications();
        logger.info(" Number of Notification Generated are : " + numberOfNotifications);
    }

    /**
     * Get all the Notifications with NotificationGenerated status and mark them
     * as Scheduled with appropriate Schedule time
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification.marketplace}",
            initialDelayString = "${scheduler.initialdelay.notification.notificationSchedular}")
    public void notificationSchedular() {
        logger.info("NotificationSchedular: Scheduling Generated Notification.");
        Integer numberOfScheduledNtGenerated = notificationSchedular.scheduleNotifications();
        logger.info("NotificationSchedular: Scheduled " + numberOfScheduledNtGenerated + " Generated Notifications");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification.marketplace}",
            initialDelayString = "${scheduler.initialdelay.notification.emailNotificationSender}")
    public void emailNotificationSender() {
        Thread.currentThread().setName("EmailSender");
        logger.info("NotificationSender : Sending Scheduled Generated Notification via Email.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(MediumType.Email);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated + " Generated Notifications via Email");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    @QuartzScheduledJob(cron = "${scheduler.cron.notification.androidNotificationSender}")
    public void androidNotificationSender() {
        Thread.currentThread().setName("AndroidSender");
        logger.info("NotificationSender : Sending Scheduled Generated Notification via Android.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(MediumType.Android);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated + " Generated Notifications via Android");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification.marketplace}",
            initialDelayString = "${scheduler.initialdelay.notification.smsNotificationSender}")
    public void smsNotificationSender() {
        Thread.currentThread().setName("SmsSender");
        logger.info("NotificationSender : Sending Scheduled Generated Notification via SMS.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(MediumType.Sms);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated + " Generated Notifications via SMS");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.notification.marketplace}",
            initialDelayString = "${scheduler.initialdelay.notification.marketplaceAppNotificationSender}")
    public void marketplaceAppNotificationSender() {
        Thread.currentThread().setName("MarketPlaceAppSender");
        logger.info("NotificationSender : Sending Scheduled Generated Notification via MarketplaceApp.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(MediumType.MarketplaceApp);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated
                + " Generated Notifications via MarketplaceApp");
    }

    /**
     * Send Notification Generated which are scheduled and Ready to be send in
     * the respective medium
     */
    @QuartzScheduledJob(cron = "${scheduler.cron.notification.proptigerAppNotificationSender}")
    public void proptigerAppNotificationSender() {
        Thread.currentThread().setName("ProptigerAppSender");
        logger.info("NotificationSender : Sending Scheduled Generated Notification via ProptigerApp.");
        Integer numberOfSentNtGenerated = notificationSender.sendNotification(MediumType.ProptigerApp);
        logger.info("NotificationSender: Sent " + numberOfSentNtGenerated + " Generated Notifications via ProptigerApp");
    }

}
