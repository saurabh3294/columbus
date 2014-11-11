package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;
import com.proptiger.data.event.service.EventGeneratedService;

@Component
public abstract class DBEventProcessor implements EventProcessor {

    private static Logger         logger = LoggerFactory.getLogger(DBEventProcessor.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    public List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        List<EventGenerated> processedEvents = eventGeneratedService.getProcessedEventsToBeMerged();

        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        logger.info(" MAPPING " + new Gson().toJson(groupEventMap));
        Map<String, List<EventGenerated>> allCurrentProcessedEvents = groupEventsByKey(processedEvents);

        // Map for Updating the Events by their old status.
        Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap = new HashMap<EventGenerated.EventStatus, List<EventGenerated>>();
        updateEventsByOldStatusMap.put(EventStatus.Processed, new ArrayList<EventGenerated>());
        
        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {
            handleRawEventsStrategy(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap);
        }
        // Updating processed Raw Events.
        eventGeneratedService.saveOrUpdateEvents(events);
        // Updating processed Processed Events
        eventGeneratedService.updateEventsOnOldEventStatus(updateEventsByOldStatusMap);
        
        return events;
    }

    public List<EventGenerated> processProcessedEvents(List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        List<EventGenerated> discardedEvents = new ArrayList<EventGenerated>();

        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {

            for (EventGenerated eventGenerated : entry.getValue()) {
                eventGenerated.setEventStatus(EventStatus.Discarded);
                discardedEvents.add(eventGenerated);
            }

            /*
             * In Price Change, Only first latest event(by date) has to be
             * considered for verification. Rest have to be discarded.
             */
            EventGenerated firstEvent = entry.getValue().get(0);
            // removing the first Event from discarded list.
            discardedEvents.remove(firstEvent);
            firstEvent.setEventStatus(EventStatus.Verified);
            updateEventHistories(firstEvent, EventStatus.Verified);
            updateEventExpiryTime(firstEvent);
            // Updating the Event in the database.
            EventGenerated newEventGenerated = eventGeneratedService.saveOrUpdateOneEvent(firstEvent);
            // Event has been marked Successfully for pending verification.
            // Hence, sending it to verfication.
            if (newEventGenerated.getEventStatus().name().equals(EventStatus.Verified.name())) {
                newEventGenerated.getEventType().getEventTypeConfig().getEventVerificationObject()
                        .verifyEvents(newEventGenerated);
            }
        }

        // Updating the discarded events in the database. Here there is no need
        // to check their old status.
        eventGeneratedService.saveOrUpdateEvents(events);
        return events;
    }

    public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events) {
        return events;
    }

    public boolean populateEventSpecificData(EventGenerated event) {
        return true;
    }
    
    protected void handleRawEventsStrategy(Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap){
        
        EventType eventType = entry.getValue().get(0).getEventType();
        
        if(eventType.getStrategy() == EventType.Strategy.SUPPRESS){
            suppressRawEvents(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap);
        }
        else if(eventType.getStrategy() == EventType.Strategy.MERGE){
            mergeRawEvents(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap);
        }
        else {
            noStrategyRawEvents(entry);
        }
        
    }
    
    protected void noStrategyRawEvents(Map.Entry<String, List<EventGenerated>> entry) {
        for(EventGenerated eventGenerated: entry.getValue()){
            updateEventStatus(eventGenerated, EventStatus.Processed);
        }
    }
    protected void suppressRawEvents(
            Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {
        
        List<EventGenerated> processedEventsByEventStatus = null;
        int size;
        
        // All old Raw Events to be discarded.
        for (EventGenerated eventGenerated : entry.getValue()) {
            eventGenerated.setEventStatus(EventStatus.Discarded);
        }

        // All old processed Events to be discarded.
        processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Discarded);
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }
        /*
         * In Price Change, Only first latest event(by date) has to be
         * considered. Rest have to be discarded.
         */
        size = entry.getValue().size() - 1;
        EventGenerated lastEvent = entry.getValue().get(size);
        updateEventStatus(lastEvent, EventStatus.Processed);
        logger.info(new Gson().toJson(lastEvent.getEventTypePayload()));
    }

    protected void mergeRawEvents(Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {
            
        List<EventGenerated> processedEventsByEventStatus = null;
        int size = entry.getValue().size() - 1;
        List<EventTypePayload> childEventTypePayloads = new ArrayList<EventTypePayload>();
        
        // All old Raw Events to be merged.
        for (EventGenerated eventGenerated : entry.getValue()) {
            childEventTypePayloads.add(eventGenerated.getEventTypePayload());
            eventGenerated.setEventStatus(EventStatus.Merged);
        }
        // Removing the last payload from the payload as this is one being kept.
        childEventTypePayloads.remove(size);

        // All old processed Events to be merged.
        processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Merged);
                childEventTypePayloads.add(eventGenerated.getEventTypePayload());
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }
        /*
         * In Price Change, Only first latest event(by date) has to be
         * considered. Rest have to be discarded.
         */
        EventGenerated lastEvent = entry.getValue().get(size);
        updateEventStatus(lastEvent, EventStatus.Processed);
        logger.info(new Gson().toJson(lastEvent.getEventTypePayload()));
    }
    
    
    
    protected Map<String, List<EventGenerated>> groupEventsByKey(List<EventGenerated> events) {
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

    protected void updateEventHistories(EventGenerated eventGenerated, EventStatus eventStatus) {
        List<EventTypeUpdateHistory> eventTypeUpdateHistories = eventGenerated.getEventTypePayload()
                .getEventTypeUpdateHistories();
        if (eventTypeUpdateHistories == null) {
            eventTypeUpdateHistories = new ArrayList<EventTypeUpdateHistory>();
        }
        EventTypeUpdateHistory newHistory = new EventTypeUpdateHistory(eventStatus, new Date());
        logger.info(" EVENT ID NEW LOG " + eventGenerated.getId() + new Gson().toJson(newHistory));
        eventTypeUpdateHistories.add(newHistory);
        logger.info(" EVENT ID ALL LOG " + eventGenerated.getId() + new Gson().toJson(eventTypeUpdateHistories));

        eventGenerated.getEventTypePayload().setEventTypeUpdateHistories(eventTypeUpdateHistories);
        eventGenerated.setData(new Gson().toJson(eventGenerated.getEventTypePayload()));
        logger.info(" EVENT ID PAYLOAD : " + new Gson().toJson(eventGenerated));

    }

    protected void updateEventExpiryTime(EventGenerated eventGenerated) {
        Date expiredDate = DateUtils.addHours(new Date(), eventGenerated.getEventType().getValidationCycleHours());
        logger.info("EVENT TYPE ID " + eventGenerated.getId()
                + " Number of Hours"
                + eventGenerated.getEventType().getValidationCycleHours()
                + "SETTING EXPIRY DATE "
                + expiredDate);
        eventGenerated.setExpiryDate(expiredDate);
    }
    
    private void updateEventStatus(EventGenerated eventGenerated, EventStatus eventStatus){
        eventGenerated.setEventStatus(eventStatus);
        updateEventHistories(eventGenerated, eventStatus);
        updateEventExpiryTime(eventGenerated);
    }
}
