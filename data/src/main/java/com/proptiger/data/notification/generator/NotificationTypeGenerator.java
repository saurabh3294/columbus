package com.proptiger.data.notification.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.service.NotificationTypeGeneratedService;
import com.proptiger.data.notification.service.SubscriberConfigService;

@Service
public class NotificationTypeGenerator {

    private static Logger                    logger = LoggerFactory.getLogger(NotificationTypeGenerator.class);

    @Autowired
    private NotificationTypeGeneratedService ntGeneratedService;

    @Autowired
    private SubscriberConfigService          subscriberConfigService;

    @Autowired
    private EventGeneratedService            eventGeneratedService;

    public boolean isNotificationGenerationRequired() {
        Long activeNTCount = ntGeneratedService.getActiveNotificationTypeCount();
        Integer maxActiveNTCount = subscriberConfigService.getMaxActiveNotificationTypeCount();

        if (activeNTCount < maxActiveNTCount) {
            logger.debug("NotificationType Generation required as activeNTCount is " + activeNTCount);
            return true;
        }
        logger.debug("NotificationType Generation not required as activeNTCount " + activeNTCount
                + " is greater then or equal to maxActiveNTCount "
                + maxActiveNTCount);
        return false;
    }

    @Transactional
    public Integer generateNotificationTypes() {
        Integer ntCount = 0;
        Date fromDate = subscriberConfigService.getLastEventDateReadByNotification();

        List<EventGenerated> eventGeneratedList = eventGeneratedService.getVerifiedEventsFromDate(fromDate);
        logger.debug("Found " + eventGeneratedList.size() + " EventGenerateds from Date " + fromDate);

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
            List<NotificationTypeGenerated> ntGeneratedList = ntGeneratedService
                    .getNotificationTypesForEventGenerated(eventGenerated);
            logger.debug("Generated " + ntGeneratedList.size()
                    + " NotificationTypeGenerateds from eventGeneratedId "
                    + eventGenerated.getId());

            ntCount += ntGeneratedList.size();
            ntGeneratedService.persistNotificationTypes(eventGenerated, ntGeneratedList);
        }

        return ntCount;
    }

}
