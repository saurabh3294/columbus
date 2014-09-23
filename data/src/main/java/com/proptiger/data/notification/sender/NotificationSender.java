package com.proptiger.data.notification.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.SentNotificationLogService;

@Service
public class NotificationSender {

    private static Logger                logger = LoggerFactory.getLogger(NotificationSender.class);

    @Autowired
    private TemplateGenerator            templateGenerator;

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    @Autowired
    private SentNotificationLogService   sentNotificationLogService;

    public Integer sendNotification(MediumType medium) {
        Integer numberOfSendNtGen = 0;
        List<NotificationGenerated> nGeneratedList = nGeneratedService.getScheduledAndReadyNotifications(medium);
        logger.info("NotificationSender : Number of Scheduled and Ready Notifications " + nGeneratedList.size());
        for (NotificationGenerated nGenerated : nGeneratedList) {
            try {
                String template = templateGenerator.generatePopulatedTemplate(nGenerated);

                if (sendAndUpdateNotificationGenerated(nGenerated, template)) {
                    numberOfSendNtGen++;
                }
            }
            catch (Exception e) {
                logger.info("Send Notification failed for Notification ID " + nGenerated.getId());
                e.printStackTrace();
            }
        }
        return numberOfSendNtGen;
    }

    public boolean sendAndUpdateNotificationGenerated(NotificationGenerated nGenerated, String template) {
        if (template == null) {
            logger.info("Template is null so discarding it to send for notificationGenerated Id : " + nGenerated
                    .getId());
            return false;
        }

        Integer userId = nGenerated.getUserId();
        boolean isSent = nGenerated.getNotificationMedium().getMediumTypeConfig().getMediumSenderObject()
                .send(template, userId, nGenerated.getNotificationType().getName());
        
        // Sent NotificationGenerated logging handling will be done
        // later.
        // currently notification status of sent NG is marked as
        // sent in DB.
        /*
         * sentNotificationLogService.save(new
         * SentNotificationLog(ntGenerated.getId(), ntGenerated
         * .getNotificationMedium().getId(),
         * ntGenerated.getNotificationMessage().getForumUser() .getUserId(), new
         * Date()));
         */

        NotificationStatus notificationStatus = NotificationStatus.Failed;
        if (isSent) {
            notificationStatus = NotificationStatus.Sent;
        }

        nGeneratedService.updateNotificationGeneratedStatusOnOldStatus(
                nGenerated.getId(),
                notificationStatus,
                nGenerated.getNotificationStatus());

        return isSent;

    }
}
