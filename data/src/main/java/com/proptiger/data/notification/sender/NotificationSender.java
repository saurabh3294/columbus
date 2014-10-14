package com.proptiger.data.notification.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.MediumTypeConfig;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;
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
            NotificationStatus notificationStatus = NotificationStatus.Failed;
            try {
                if (sendNotificationGenerated(nGenerated)) {
                    notificationStatus = NotificationStatus.Sent;
                    numberOfSendNtGen++;
                }
            }
            catch (Exception e) {
                logger.error("Send Notification failed for nGenerated id: " + nGenerated.getId()
                        + " with Exception: "
                        + e + " StackTrace: " + e.getStackTrace());
            }

            if (NotificationStatus.Failed.equals(notificationStatus)) {
                logger.error("Not able to send Notification for nGenerated id: " + nGenerated.getId()
                        + " Marking its status as Failed.");
            }

            nGeneratedService.updateNotificationGeneratedStatusOnOldStatus(
                    nGenerated.getId(),
                    notificationStatus,
                    nGenerated.getNotificationStatus());
        }

        return numberOfSendNtGen;
    }

    public boolean sendNotificationGenerated(NotificationGenerated nGenerated) {

        String template = templateGenerator.generatePopulatedTemplate(nGenerated);

        if (template == null) {
            return false;
        }

        Integer userId = nGenerated.getUserId();
        MediumTypeConfig config = nGenerated.getNotificationMedium().getMediumTypeConfig();

        NotificationSenderPayload payload = config.getNotificationSenderPayloadObject();
        if (payload != null) {
            payload = payload.populatePayload(nGenerated);
        }
        return config.getMediumSenderObject().send(
                template,
                userId,
                nGenerated.getNotificationType().getName(),
                payload);

    }
}
