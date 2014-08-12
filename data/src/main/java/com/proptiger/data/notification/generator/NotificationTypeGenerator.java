package com.proptiger.data.notification.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.service.NotificationTypeGenerationService;
import com.proptiger.data.notification.service.SubscriberConfigService;

@Service
public class NotificationTypeGenerator {

    @Autowired
    private NotificationTypeGenerationService ntGenerationService;

    @Autowired
    private SubscriberConfigService           subscriberConfigService;

    @Autowired
    private EventGeneratedService             eventGeneratedService;

    public boolean isNotificationGenerationRequired() {
        Integer activeNTCount = ntGenerationService.getActiveNotificationTypeCount();
        Integer maxActiveNTCount = subscriberConfigService.getMaxActiveNotificationTypeCount();

        if (activeNTCount < maxActiveNTCount) {
            return true;
        }
        return false;
    }

    public Integer generateNotificationTypes() {
        Integer ntCount = 0;
        Date fromDate = subscriberConfigService.getLastEventDateReadByNotification();

        List<EventGenerated> eventGeneratedList = eventGeneratedService.getVerifiedEventsFromDate(fromDate);

        Collections.sort(eventGeneratedList, new Comparator<EventGenerated>() {
            public int compare(EventGenerated event1, EventGenerated event2) {
                if (event1.getUpdatedDate().after(event2.getUpdatedDate()))
                    return 1;
                else if (event1.getUpdatedDate().before(event2.getUpdatedDate()))
                    return -1;
                else
                    return 0;
            }
        });

        for (EventGenerated eventGenerated : eventGeneratedList) {
            List<NotificationTypeGenerated> ntGeneratedList = ntGenerationService
                    .getNotificationTypesForEventGenerated(eventGenerated);
            ntCount += ntGeneratedList.size();
            ntGenerationService.persistNotificationTypes(eventGenerated, ntGeneratedList);
        }

        return ntCount;
    }

}
