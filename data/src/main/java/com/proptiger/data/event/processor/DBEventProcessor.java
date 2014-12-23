package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.EventType.HoldingPeriodType;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.util.Serializer;

@Component
public abstract class DBEventProcessor implements EventProcessor {

    private static Logger           logger = LoggerFactory.getLogger(DBEventProcessor.class);

    @Autowired
    protected EventGeneratedService eventGeneratedService;

    /**
     * Takes the list of Raw events and Processed events from DB which are still
     * in holding state and Merge/Suppress the events of a particular primary
     * key based on the config of the event type.
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
     * Takes the list of all events that are in Processed state and whose
     * holding period has expired marks it as PENDING_VERIFICATION if
     * verification is required else VERIFIED if no verification is required.
     * 
     * @param events
     * @return
     */
    public List<EventGenerated> processProcessedEvents(Integer eventTypeId, List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        EventStatus eventStatus = EventStatus.Verified;
        if (events.get(0).getEventType().getVerficationRequired()) {
            eventStatus = EventStatus.PendingVerification;
        }

        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {
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

    protected void handleProcessedEventsStrategy(
            Map.Entry<String, List<EventGenerated>> entry,
            EventStatus verificationStatus) {

        EventType eventType = entry.getValue().get(0).getEventType();
        List<EventGenerated> finalEvents = new ArrayList<EventGenerated>();

        if (eventType.getStrategy().equals(EventType.Strategy.SUPPRESS)) {
            finalEvents.add(suppressProcessedEvents(entry));
        }
        else if (eventType.getStrategy().equals(EventType.Strategy.MERGE)) {
            finalEvents.add(mergeProcessedEvents(entry));
        }
        else {
            finalEvents.addAll(entry.getValue());
        }

        for (EventGenerated eventGenerated : finalEvents) {

            // Update the status of latest event
            updateEventStatus(eventGenerated, verificationStatus);

            // Updating the Event in the database
            EventGenerated newEventGenerated = eventGeneratedService.saveOrUpdateOneEvent(eventGenerated);

            // If event is marked Successfully for pending verification then
            // send it for verification
            if (newEventGenerated.getEventStatus().equals(EventStatus.PendingVerification)) {
                eventGenerated.getEventType().getEventTypeConfig().getEventVerificationObject()
                        .verifyEvents(newEventGenerated);
            }
        }
    }

    protected EventGenerated suppressProcessedEvents(Map.Entry<String, List<EventGenerated>> entry) {
        return suppressEvents(entry.getValue());
    }

    protected EventGenerated mergeProcessedEvents(Map.Entry<String, List<EventGenerated>> entry) {
        return mergeEvents(entry.getValue());
    }

    protected void handleRawEventsStrategy(
            Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {

        EventType eventType = entry.getValue().get(0).getEventType();
        List<EventGenerated> finalEvents = new ArrayList<EventGenerated>();

        if (eventType.getStrategy().equals(EventType.Strategy.SUPPRESS)) {
            finalEvents.add(suppressRawEvents(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap));
        }
        else if (eventType.getStrategy().equals(EventType.Strategy.MERGE)) {
            finalEvents.add(mergeRawEvents(entry, allCurrentProcessedEvents, updateEventsByOldStatusMap));
        }
        else {
            finalEvents.addAll(entry.getValue());
        }

        for (EventGenerated eventGenerated : finalEvents) {
            // Update the status of latest event
            updateEventStatus(eventGenerated, EventStatus.Processed);
        }
    }

    protected EventGenerated suppressRawEvents(
            Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {

        // Suppress old events
        EventGenerated lastEvent = suppressEvents(entry.getValue());

        // All old processed Events to be discarded.
        List<EventGenerated> processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Discarded);
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }

        return lastEvent;
    }

    protected EventGenerated mergeRawEvents(
            Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {

        // Merge old events
        EventGenerated lastEvent = mergeEvents(entry.getValue());

        // Get all child events
        List<EventTypePayload> childEventTypePayloads = lastEvent.getEventTypePayload().getChildEventTypePayloads();

        // All old processed Events to be merged
        List<EventGenerated> processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Merged);
                eventGenerated.setMergedEventId(lastEvent.getId());
                childEventTypePayloads.add(eventGenerated.getEventTypePayload());
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }

        return lastEvent;
    }

    protected EventGenerated suppressEvents(List<EventGenerated> events) {
        // Suppressing old events and returning the latest event
        EventGenerated lastEvent = null;
        for (EventGenerated eventGenerated : events) {
            if (lastEvent == null || (lastEvent.getId() < eventGenerated.getId())) {
                lastEvent = eventGenerated;
            }
            eventGenerated.setEventStatus(EventStatus.Discarded);
        }
        return lastEvent;
    }

    protected EventGenerated mergeEvents(List<EventGenerated> events) {

        // Finding the latest event
        EventGenerated lastEvent = null;
        for (EventGenerated eventGenerated : events) {
            if (lastEvent == null || (lastEvent.getId() < eventGenerated.getId())) {
                lastEvent = eventGenerated;
            }
        }

        // Merging payloads of all the mergable events in the payload of the
        // latest event
        List<EventTypePayload> childEventTypePayloads = new ArrayList<EventTypePayload>();
        for (EventGenerated eventGenerated : events) {
            if (lastEvent.getId() != eventGenerated.getId()) {
                childEventTypePayloads.add(eventGenerated.getEventTypePayload());
                eventGenerated.setEventStatus(EventStatus.Merged);
                eventGenerated.setMergedEventId(lastEvent.getId());
            }
        }

        List<EventTypePayload> childPayloads = lastEvent.getEventTypePayload().getChildEventTypePayloads();
        if (childPayloads == null) {
            lastEvent.getEventTypePayload().setChildEventTypePayloads(childEventTypePayloads);
        }
        else {
            childPayloads.addAll(childEventTypePayloads);
        }

        return lastEvent;
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
     * Updates the expiry time for a particular event based on the holding
     * period config present in DB for a particular event type
     * 
     * @param eventGenerated
     */
    protected void updateEventExpiryTime(EventGenerated eventGenerated) {
        HoldingPeriodType holdingPeriodType = eventGenerated.getEventType().getHoldingPeriodType();
        Integer holdingPeriodValue = eventGenerated.getEventType().getHoldingPeriodValue();
        Date expiryDate = new Date();

        switch (holdingPeriodType) {
            case SECONDS:
                expiryDate = DateUtils.addSeconds(expiryDate, holdingPeriodValue);
                break;
            case MINUTES:
                expiryDate = DateUtils.addMinutes(expiryDate, holdingPeriodValue);
                break;
            case HOURS:
                expiryDate = DateUtils.addHours(expiryDate, holdingPeriodValue);
                break;
            case DAYS:
                expiryDate = DateUtils.addDays(expiryDate, holdingPeriodValue);
                break;
            case WEEKS:
                expiryDate = DateUtils.addWeeks(expiryDate, holdingPeriodValue);
                break;
            case MONTHS:
                expiryDate = DateUtils.addMonths(expiryDate, holdingPeriodValue);
                break;
            case UPCOMING_MONTHS:
                expiryDate = DateUtils.addMonths(expiryDate, holdingPeriodValue);
                expiryDate = DateUtils.truncate(expiryDate, Calendar.MONTH);
                break;
        }

        logger.debug("EVENT TYPE ID " + eventGenerated.getId()
                + ",  HoldingPeriodType: "
                + holdingPeriodType
                + ",  holdingPeriodValue: "
                + holdingPeriodValue
                + ". SETTING EXPIRY DATE: "
                + expiryDate);

        eventGenerated.setExpiryDate(expiryDate);
    }

    protected void updateEventStatus(EventGenerated eventGenerated, EventStatus eventStatus) {
        eventGenerated.setEventStatus(eventStatus);
        updateEventHistories(eventGenerated, eventStatus);
        updateEventExpiryTime(eventGenerated);
    }

}
