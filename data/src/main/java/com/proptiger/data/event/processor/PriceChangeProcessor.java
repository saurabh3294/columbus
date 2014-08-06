package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.processor.handler.DBEventProcessorHandler;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.event.service.EventTypeProcessorService;

@Component
public class PriceChangeProcessor extends DBEventProcessor {
    private static Logger         logger = LoggerFactory.getLogger(PriceChangeProcessor.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;
    
    @Autowired
    private EventTypeProcessorService eventTypeProcessorService;

    @Override
    public List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        List<EventGenerated> processedEvents = eventGeneratedService.getProcessedEventsToBeMerged();

        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        System.out.println(" MAPPING "+new Gson().toJson(groupEventMap));
        Map<String, List<EventGenerated>> allCurrentProcessedEvents = groupEventsByKey(processedEvents);

        // Map for Updating the Events by their old status.
        Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap = new HashMap<EventGenerated.EventStatus, List<EventGenerated>>();
        updateEventsByOldStatusMap.put(EventStatus.Processed, new ArrayList<EventGenerated>());

        List<EventGenerated> processedEventsByEventStatus = null;
        int size;
        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {
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
            size = entry.getValue().size()-1;
            EventGenerated lastEvent = entry.getValue().get(size);
            lastEvent.setEventStatus(EventStatus.Processed);
            updateEventHistories(lastEvent, EventStatus.Processed);
            updateEventExpiryTime(lastEvent);
            logger.info(new Gson().toJson(lastEvent.getEventTypePayload()));
            
        }

        // Updating processed Raw Events.
        eventGeneratedService.saveOrUpdateEvents(events);
        // Updating processed Processed Events
        eventGeneratedService.updateEventsOnOldEventStatus(updateEventsByOldStatusMap);
        return events;
    }

    @Override
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
            firstEvent.setEventStatus(EventStatus.PendingVerification);
            updateEventHistories(firstEvent, EventStatus.PendingVerification);
            updateEventExpiryTime(firstEvent);
            // Updating the Event in the database.
            EventGenerated newEventGenerated = eventGeneratedService.saveOrUpdateOneEvent(firstEvent);
            // Event has been marked Successfully for pending verification.
            // Hence, sending it to verfication.
            if (newEventGenerated.getEventStatus().name().equals(EventStatus.PendingVerification.name())) {
                newEventGenerated.getEventType().getEventTypeConfig().getEventVerificationObject()
                        .verifyEvents(newEventGenerated);
            }
        }

        // Updating the discarded events in the database. Here there is no need
        // to check their old status.
        eventGeneratedService.saveOrUpdateEvents(events);
        return events;
    }

    @Override
    public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        Double oldValue = eventTypeProcessorService.getPriceChangeOldValue(event);
        if(oldValue == null){
            return false;
        }
        DefaultEventTypePayload defaultEventTypePayload = (DefaultEventTypePayload)event.getEventTypePayload();
        defaultEventTypePayload.setOldValue(oldValue);
        
        return true;
    }

}
