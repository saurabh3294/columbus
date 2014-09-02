package com.proptiger.data.notification.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;
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
    private NotificationGeneratedService ntGeneratedService;

    @Autowired
    private SentNotificationLogService   sentNotificationLogService;

    @Transactional
    public Integer sendNotification(MediumType medium) {
        Integer numberOfSendNtGen = 0;
        List<NotificationGenerated> ntGeneratedList = ntGeneratedService.getScheduledAndReadyNotifications(medium);
        logger.info("NotificationSender : Number of Scheduled and Ready Notifications " + ntGeneratedList.size());
        for (NotificationGenerated ntGenerated : ntGeneratedList) {
            try {
                MailBody mailBody = templateGenerator.generateMailBodyFromTemplate(ntGenerated);
                if (mailBody == null) {
                    logger.info("Mail body is null so discarding it to send -" + ntGenerated.getId());
                }
                else {
                    ForumUser forumUser = ntGenerated.getForumUser();
                    ntGenerated.getNotificationMedium().getMediumTypeConfig().getMediumSenderObject()
                            .send(mailBody, forumUser);
                    // Sent NotificationGenerated logging handling will be done
                    // later.
                    // currently notification status of sent NG is marked as
                    // sent in DB.
                    /*
                     * sentNotificationLogService.save(new
                     * SentNotificationLog(ntGenerated.getId(), ntGenerated
                     * .getNotificationMedium().getId(),
                     * ntGenerated.getNotificationMessage().getForumUser()
                     * .getUserId(), new Date()));
                     */
                    ntGeneratedService.updateNotificationGeneratedStatusOnOldStatus(
                            ntGenerated.getId(),
                            NotificationStatus.Sent,
                            ntGenerated.getNotificationStatus());
                    numberOfSendNtGen++;
                }
            }
            catch (Exception e) {
                logger.info("Send Notification failed for Notification ID " + ntGenerated.getId());
                e.printStackTrace();
            }
        }
        return numberOfSendNtGen;
    }
}
