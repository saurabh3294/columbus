package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;
import com.proptiger.data.event.processor.handler.DBEventProcessorHandler;

public abstract class DBEventProcessor implements EventProcessor {
    private static Logger         logger = LoggerFactory.getLogger(DBEventProcessor.class);


    abstract public List<EventGenerated> processRawEvents(List<EventGenerated> events);

    abstract public List<EventGenerated> processProcessedEvents(List<EventGenerated> events);

    abstract public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events);

    abstract public boolean populateEventSpecificData(EventGenerated event);

    Map<String, List<EventGenerated>> groupEventsByKey(List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventsByUniqueKey = new HashMap<String, List<EventGenerated>>();

        List<EventGenerated> eventsGeneratedByKeyGroup = null;
        for (EventGenerated eventGenerated : events) {
            eventsGeneratedByKeyGroup = groupEventsByUniqueKey.get(eventGenerated.getEventTypeUniqueKey());

            if (eventsGeneratedByKeyGroup == null) {
                eventsGeneratedByKeyGroup = new ArrayList<EventGenerated>();
            }

            eventsGeneratedByKeyGroup.add(eventGenerated);
            groupEventsByUniqueKey.put(eventGenerated.getEventTypeUniqueKey(), eventsGeneratedByKeyGroup);

        }

        return groupEventsByUniqueKey;
    }

    void updateEventHistories(EventGenerated eventGenerated, EventStatus eventStatus) {
        List<EventTypeUpdateHistory> eventTypeUpdateHistories = eventGenerated.getEventTypePayload()
                .getEventTypeUpdateHistories();
        if (eventTypeUpdateHistories == null) {
            eventTypeUpdateHistories = new ArrayList<EventTypeUpdateHistory>();
        }
        EventTypeUpdateHistory newHistory = new EventTypeUpdateHistory(eventStatus, new Date());
        logger.info(" EVENT ID NEW LOG "+eventGenerated.getId()+new Gson().toJson(newHistory));
        eventTypeUpdateHistories.add(newHistory);
        logger.info(" EVENT ID ALL LOG "+eventGenerated.getId()+new Gson().toJson(eventTypeUpdateHistories));

        eventGenerated.getEventTypePayload().setEventTypeUpdateHistories(eventTypeUpdateHistories);
        eventGenerated.setData(new Gson().toJson(eventGenerated.getEventTypePayload()));
        logger.info(" EVENT ID PAYLOAD : "+new Gson().toJson(eventGenerated));

    }

    void updateEventExpiryTime(EventGenerated eventGenerated) {
        Date expiredDate = DateUtils
                .addHours(new Date(), eventGenerated.getEventType().getQueuedItemsValidationCycle());
        eventGenerated.setExpiryDate(expiredDate);
    }

}
