package com.proptiger.data.notification.generator;

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

        Date lastEventDate = fromDate;
        for (EventGenerated eventGenerated : eventGeneratedList) {
            if (lastEventDate.before(eventGenerated.getUpdatedDate())) {
                lastEventDate = eventGenerated.getUpdatedDate();
            }
            List<NotificationTypeGenerated> ntGeneratedList = ntGenerationService
                    .getNotificationTypesForEventGenerated(eventGenerated);
            ntCount += ntGeneratedList.size();
            ntGenerationService.persistNotificationTypes(ntGeneratedList);
        }

        subscriberConfigService.setLastEventDateReadByNotification(lastEventDate);
        return ntCount;
    }

}
