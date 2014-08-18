package com.proptiger.data.notification.sender;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationGeneratedService;

@Service
public class NotificationSender {
    private static Logger                logger = LoggerFactory.getLogger(NotificationSender.class);
    @Autowired
    private TemplateGenerator            templateGenerator;

    @Autowired
    private NotificationGeneratedService ntGeneratedService;

    public Integer sendNotification() {
        Integer numberOfSendNtGen = 0;
        List<NotificationGenerated> ntGeneratedList = ntGeneratedService.getScheduledAndReadyNotifications();
        logger.info("NotificationSender : Number of Scheduled and Ready Notifications " + ntGeneratedList.size());
        for (NotificationGenerated ntGenerated : ntGeneratedList) {
            MailBody mailBody = templateGenerator.generateMailBodyFromTemplate(ntGenerated);
            if (mailBody == null) {
                logger.info("Mail body is null so discarding it to send -" + ntGenerated.getId());
            }
            else {
                try {
                    ForumUser forumUser = ntGenerated.getNotificationMessage().getForumUser();
                    ntGenerated.getNotificationMedium().getMediumTypeConfig().getMediumSenderObject().send(mailBody, forumUser);
                    numberOfSendNtGen++;
                }
                catch (Exception e) {
                    logger.info("Email sending failed for Notification Generated of ID-" + ntGenerated.getId());
                    e.printStackTrace();
                }
            }
        }
        return numberOfSendNtGen;
    }
}
