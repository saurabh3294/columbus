package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;

public abstract class DBEventProcessor implements EventProcessor {
    
    abstract public List<EventGenerated> processRawEvents(List<EventGenerated> events);

    abstract public List<EventGenerated> processProcessedEvents(List<EventGenerated> events);

    abstract public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events);
    
    abstract public void populateEventSpecificData(EventGenerated event);

    Map<String, List<EventGenerated>> groupEventsByKey(List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventsByUniqueKey = new HashMap<String, List<EventGenerated>>();

        List<EventGenerated> eventsGeneratedByKeyGroup = null;
        for (EventGenerated eventGenerated : events) {
            eventsGeneratedByKeyGroup = groupEventsByUniqueKey.get(eventGenerated.getEventTypeUniqueKey());

            if (eventsGeneratedByKeyGroup == null) {
                eventsGeneratedByKeyGroup = new ArrayList<EventGenerated>();
            }

            eventsGeneratedByKeyGroup.add(eventGenerated);
            groupEventsByUniqueKey.put(
                    eventGenerated.getEventTypeUniqueKey(),
                    eventsGeneratedByKeyGroup);

        }

        // TODO Auto-generated method stub
        return groupEventsByUniqueKey;
    }

    void updateEventHistories(EventGenerated eventGenerated, EventStatus eventStatus) {
        List<EventTypeUpdateHistory> eventTypeUpdateHistories = eventGenerated.getEventTypePayload()
                .getEventTypeUpdateHistories();
        if (eventTypeUpdateHistories == null) {
            eventTypeUpdateHistories = new ArrayList<EventTypeUpdateHistory>();
        }
        EventTypeUpdateHistory newHistory = new EventTypeUpdateHistory(eventStatus, new Date());
        eventTypeUpdateHistories.add(newHistory);
        eventGenerated.getEventTypePayload().setEventTypeUpdateHistories(eventTypeUpdateHistories);
    }

    void updateEventExpiryTime(EventGenerated eventGenerated) {
        Date expiredDate = DateUtils
                .addHours(new Date(), eventGenerated.getEventType().getQueuedItemsValidationCycle());
        eventGenerated.setExpiryDate(expiredDate);
    }
       
}
