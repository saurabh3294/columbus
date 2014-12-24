package com.proptiger.data.notification.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.subscriber.Subscriber.SubscriberName;
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

    /**
     * Returns if the generation of notification is required or not
     * 
     * @return
     */
    public boolean isNotificationGenerationRequired() {
        Long activeNTCount = ntGeneratedService.getActiveNotificationTypeCount();
        Integer maxActiveNTCount = subscriberConfigService.getMaxActiveNotificationTypeCount();

        if (activeNTCount < maxActiveNTCount) {
            logger.debug("NotificationType Generation required as activeNTCount is " + activeNTCount);
            return true;
        }
        logger.error("NotificationType Generation not required as activeNTCount " + activeNTCount
                + " is greater then or equal to maxActiveNTCount "
                + maxActiveNTCount);
        return false;
    }

    /**
     * Generates notification types from event types
     * 
     * @return
     */
    @Transactional
    public Integer generateNotificationTypes() {
        Integer ntCount = 0;

        // Get all the new verified events
        List<EventGenerated> eventGeneratedList = eventGeneratedService.getLatestVerifiedEventGeneratedsBySubscriber(
                SubscriberName.Notification,
                null, null);
        logger.debug("Found " + eventGeneratedList.size() + " EventGenerateds");

        // Sort them in ascending order by id of eventGenerated
        Collections.sort(eventGeneratedList, new Comparator<EventGenerated>() {
            public int compare(EventGenerated event1, EventGenerated event2) {
                return (event1.getId() - event2.getId());
            }
        });

        // For each event, generate the notification types
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
