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

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.util.Serializer;

@Component
public abstract class DBEventProcessor implements EventProcessor {

    private static Logger         logger = LoggerFactory.getLogger(DBEventProcessor.class);

    @Autowired
    protected EventGeneratedService eventGeneratedService;

    /**
     * Takes the list of Raw events of a particular eventType and Processed
     * events from DB which are still in holding state and marks the latest
     * event of a particular primary key as PROCESSED and remaining events as
     * DISCARDED
     * 
     * @param events
     * @return
     */
    public List<EventGenerated> processRawEvents(Integer eventTypeId, List<EventGenerated> events) {

        // Gets the Processed events of a particular type from DB which are
        // still in holding state
        List<EventGenerated> processedEvents = eventGeneratedService.getProcessedEventsToBeMerged(eventTypeId);

        // Groups the events by their primary key
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
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

    /**
     * Takes the list of all events of a particular eventType that are in
     * Processed state and whose holding period has expired and checks the
     * latest event of a particular primary key for verification and marks the
     * remaining events as DISCARDED. An event is marked as PENDING_VERIFICATION
     * if verification is required else VERIFIED if no verification is required.
     * 
     * @param events
     * @return
     */
    public List<EventGenerated> processProcessedEvents(Integer eventTypeId, List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        EventStatus eventStatus = null;
        
        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {
            eventStatus = EventStatus.Verified;
            if( entry.getValue().get(0).getEventType().getVerficationRequired() > 0){
                eventStatus = EventStatus.PendingVerification;
            }
            handleProcessedEventsStrategy(entry, eventStatus);
        }

        // Updating the discarded events in the database. Here there is no need
        // to check their old status.
        eventGeneratedService.saveOrUpdateEvents(events);
        return events;
    }

    public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events) {
        return events;
    }
   /**
     * This can be used to populate the event specific data for an event. In
     * most of the cases this function will overridden by the child classes for
     * defining what all and how the data needs to be populated for a specific
     * event type.
     * 
     * @param event
     * @return
     */
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        return event;
    }
    
    protected void handleProcessedEventsStrategy(Map.Entry<String, List<EventGenerated>> entry, EventStatus verificationStatus){
        
        EventType eventType = entry.getValue().get(0).getEventType();
        
        if(eventType.getStrategy().equals(EventType.Strategy.SUPPRESS)){
            suppressProcessedEvents(entry, verificationStatus);
        }
        else if(eventType.getStrategy().equals(EventType.Strategy.MERGE)){
            mergeProcessedEvents(entry, verificationStatus);
        }
        else {
            noStrategyProcessedEvents(entry, verificationStatus);
        }
        
    }
    
    protected void noStrategyProcessedEvents(Map.Entry<String, List<EventGenerated>> entry, EventStatus verficationEventStatus) {
        for(EventGenerated eventGenerated: entry.getValue()){
            updateEventStatus(eventGenerated, verficationEventStatus);
        }
    }
    protected void suppressProcessedEvents( Map.Entry<String, List<EventGenerated>> entry, EventStatus verficationEventStatus){
        List<EventGenerated> discardedEvents = new ArrayList<EventGenerated>();

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
        updateEventStatus(firstEvent, verficationEventStatus);
        
        // Updating the Event in the database.
        EventGenerated newEventGenerated = eventGeneratedService.saveOrUpdateOneEvent(firstEvent);
        // Event has been marked Successfully for pending verification.
        // Hence, sending it to verfication.
        if (newEventGenerated.getEventStatus().name().equals(verficationEventStatus.name())) {
            newEventGenerated.getEventType().getEventTypeConfig().getEventVerificationObject()
                    .verifyEvents(newEventGenerated);
        }
    }

    protected void mergeProcessedEvents(Map.Entry<String, List<EventGenerated>> entry, EventStatus verficationEventStatus) {
        List<EventGenerated> mergedEvents = new ArrayList<EventGenerated>();
        List<EventTypePayload> childEventTypePayloads = new ArrayList<EventTypePayload>();

        for (EventGenerated eventGenerated : entry.getValue()) {
            eventGenerated.setEventStatus(EventStatus.Merged);
            mergedEvents.add(eventGenerated);
            childEventTypePayloads.add(eventGenerated.getEventTypePayload());
        }

        /*
         * In Price Change, Only first latest event(by date) has to be
         * considered for verification. Rest have to be discarded.
         */
        EventGenerated firstEvent = entry.getValue().get(0);
        // removing the first Event from discarded list.
        mergedEvents.remove(firstEvent);
        childEventTypePayloads.remove(0);
        updateEventStatus(firstEvent, verficationEventStatus);
        firstEvent.getEventTypePayload().setChildEventTypePayloads(childEventTypePayloads);
        
        // Updating the Event in the database.
        EventGenerated newEventGenerated = eventGeneratedService.saveOrUpdateOneEvent(firstEvent);
        // Event has been marked Successfully for pending verification.
        // Hence, sending it to verfication.
        if (newEventGenerated.getEventStatus().name().equals(verficationEventStatus.name())) {
            newEventGenerated.getEventType().getEventTypeConfig().getEventVerificationObject()
                    .verifyEvents(newEventGenerated);
        }
    }
    
    protected void handleRawEventsStrategy(Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap){
        
        EventType eventType = entry.getValue().get(0).getEventType();
        
        if(eventType.getStrategy().equals(EventType.Strategy.SUPPRESS)){
            suppressRawEvents(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap);
        }
        else if(eventType.getStrategy().equals(EventType.Strategy.MERGE)){
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
        lastEvent.getEventTypePayload().setChildEventTypePayloads(childEventTypePayloads);
        updateEventStatus(lastEvent, EventStatus.Processed);
    }
    
    
    /**
     * This function can be used to group the events by their corresponding
     * primaryKeys
     * 
     * @param events
     * @return
     */
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

    /**
     * Updates the event histories in an event if the status of an event
     * changes. This is to track the state changes of an event
     * 
     * @param eventGenerated
     * @param eventStatus
     */
    protected void updateEventHistories(EventGenerated eventGenerated, EventStatus eventStatus) {
        List<EventTypeUpdateHistory> eventTypeUpdateHistories = eventGenerated.getEventTypePayload()
                .getEventTypeUpdateHistories();
        if (eventTypeUpdateHistories == null) {
            eventTypeUpdateHistories = new ArrayList<EventTypeUpdateHistory>();
        }
        EventTypeUpdateHistory newHistory = new EventTypeUpdateHistory(eventStatus, new Date());
        eventTypeUpdateHistories.add(newHistory);

        eventGenerated.getEventTypePayload().setEventTypeUpdateHistories(eventTypeUpdateHistories);
        eventGenerated.setData(Serializer.toJson(eventGenerated.getEventTypePayload()));

    }

    /**
     * Updates the expiry time for a particular event based on the validation
     * cycle config present in DB for a particular event type
     * 
     * @param eventGenerated
     */
    protected void updateEventExpiryTime(EventGenerated eventGenerated) {
        Date expiredDate = DateUtils.addHours(new Date(), eventGenerated.getEventType().getValidationCycleHours());
        logger.info("EVENT TYPE ID " + eventGenerated.getId()
                + " Number of Hours"
                + eventGenerated.getEventType().getValidationCycleHours()
                + "SETTING EXPIRY DATE "
                + expiredDate);
        eventGenerated.setExpiryDate(expiredDate);
    }
    
    protected void updateEventStatus(EventGenerated eventGenerated, EventStatus eventStatus){
        eventGenerated.setEventStatus(eventStatus);
        updateEventHistories(eventGenerated, eventStatus);
        updateEventExpiryTime(eventGenerated);
    }

}
