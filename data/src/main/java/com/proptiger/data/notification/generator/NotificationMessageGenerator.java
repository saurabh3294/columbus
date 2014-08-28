package com.proptiger.data.notification.generator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeGeneratedService;
import com.proptiger.data.notification.service.SubscriberConfigService;

@Service
public class NotificationMessageGenerator {

    private static Logger                    logger = LoggerFactory.getLogger(NotificationMessageGenerator.class);

    @Autowired
    private NotificationTypeGeneratedService ntGeneratedService;

    @Autowired
    private SubscriberConfigService          subscriberConfigService;

    @Autowired
    private NotificationMessageService       notificationMessageService;

    public boolean isNotificationMessageGenerationRequired() {
        Long activeNMCount = notificationMessageService.getActiveNotificationMessageCount();
        Integer maxActiveNMCount = subscriberConfigService.getMaxActiveNotificationMessageCount();

        if (activeNMCount < maxActiveNMCount) {
            logger.debug("NotificationMessage Generation required as activeNMCount " + activeNMCount
                    + " is less than maxActiveNMCount "
                    + maxActiveNMCount);
            return true;
        }
        logger.debug("NotificationMessage Generation not required as activeNMCount " + activeNMCount
                + " is greater then or equal to maxActiveNMCount "
                + maxActiveNMCount);
        return false;
    }

    public Integer generateNotificationMessages() {
        Integer messageCount = 0;

        List<NotificationTypeGenerated> ntGeneratedList = ntGeneratedService.getActiveNotificationTypeGenerated();
        logger.debug("Found " + ntGeneratedList.size() + " NotificationTypeGenerated in DB.");
        
        for (NotificationTypeGenerated ntGenerated : ntGeneratedList) {
            List<NotificationMessage> notificationMessages = notificationMessageService
                    .getNotificationMessagesForNotificationTypeGenerated(ntGenerated);
            logger.debug("Generated " + notificationMessages.size() + " NotificationMessages for NotificationTypeGenerated ID " + ntGenerated.getId());
            messageCount += notificationMessages.size();
            notificationMessageService.persistNotificationMessages(notificationMessages, ntGenerated);
        }

        return messageCount;
    }

}
