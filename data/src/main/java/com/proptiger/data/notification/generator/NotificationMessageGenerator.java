package com.proptiger.data.notification.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Integer ntCount = 0;

        // get typeGen notifications from ntGenService
        List<NotificationTypeGenerated> ntGeneratedList = ntGeneratedService.getActiveNotificationTypeGenerated();

        // TODO:
        // get user list based on notification type

        // get msg by type gen from msgService
        // add data to msg by notification type
        // Persist msg and update typeGen Status

        // Date fromDate =
        // subscriberConfigService.getLastEventDateReadByNotification();
        //
        // List<EventGenerated> eventGeneratedList =
        // eventGeneratedService.getVerifiedEventsFromDate(fromDate);
        //
        // Collections.sort(eventGeneratedList, new Comparator<EventGenerated>()
        // {
        // public int compare(EventGenerated event1, EventGenerated event2) {
        // if (event1.getUpdatedDate().after(event2.getUpdatedDate()))
        // return 1;
        // else if (event1.getUpdatedDate().before(event2.getUpdatedDate()))
        // return -1;
        // else
        // return 0;
        // }
        // });
        //
        // for (EventGenerated eventGenerated : eventGeneratedList) {
        // List<NotificationTypeGenerated> ntGeneratedList = ntGenerationService
        // .getNotificationTypesForEventGenerated(eventGenerated);
        // ntCount += ntGeneratedList.size();
        // ntGenerationService.persistNotificationTypes(eventGenerated,
        // ntGeneratedList);
        // }

        return ntCount;
    }

}
