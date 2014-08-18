package com.proptiger.data.notification.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeGeneratedService;
import com.proptiger.data.notification.service.SubscriberConfigService;

@Service
public class NotificationMessageGenerator {

    @Autowired
    private NotificationTypeGeneratedService ntGeneratedService;

    @Autowired
    private SubscriberConfigService          subscriberConfigService;

    @Autowired
    private NotificationMessageService       notificationMessageService;

    public boolean isNotificationMessageGenerationRequired() {
        Integer activeNMCount = notificationMessageService.getActiveNotificationMessageCount();
        Integer maxActiveNMCount = subscriberConfigService.getMaxActiveNotificationMessageCount();

        if (activeNMCount < maxActiveNMCount) {
            return true;
        }
        return false;
    }

    public Integer generateNotificationMessages() {
        Integer messageCount = 0;

        List<NotificationTypeGenerated> ntGeneratedList = ntGeneratedService.getActiveNotificationTypeGenerated();

        for (NotificationTypeGenerated ntGenerated : ntGeneratedList) {
            List<NotificationMessage> notificationMessages = notificationMessageService
                    .getNotificationMessagesForNotificationTypeGenerated(ntGenerated);
            messageCount += notificationMessages.size();
            notificationMessageService.persistNotificationMessages(notificationMessages, ntGenerated);
        }

        return messageCount;
    }

}
