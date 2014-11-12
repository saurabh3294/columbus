package com.proptiger.data.event.processor.seo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.dto.EventTypePayloadDataDto;
import com.proptiger.data.event.processor.DBEventProcessor;

public class SeoProjectContentChangeProcessor extends DBEventProcessor {
    private static Logger                     logger     = LoggerFactory.getLogger(SeoProjectContentChangeProcessor.class);

    protected void mergeProcessedEvents(Map.Entry<String, List<EventGenerated>> entry, EventStatus verficationEventStatus) {
        /*
         * In Price Change, Only first latest event(by date) has to be
         * considered for verification. Rest have to be discarded.
         */
        EventGenerated firstEvent = entry.getValue().get(0);
        
        List<EventGenerated> mergedEvents = new ArrayList<EventGenerated>();
        List<EventTypePayloadDataDto> allPayloadDataDtos = (List<EventTypePayloadDataDto>)firstEvent.getEventTypePayload().getPayloadValues();
        List<EventTypePayloadDataDto> currentPayloadDataDtos;
        
        for (EventGenerated eventGenerated : entry.getValue()) {
            eventGenerated.setEventStatus(EventStatus.Merged);
            mergedEvents.add(eventGenerated);
            currentPayloadDataDtos = (List<EventTypePayloadDataDto>)eventGenerated.getEventTypePayload().getPayloadValues();
            allPayloadDataDtos.add(currentPayloadDataDtos.get(0));
        }

        
        // removing the first Event from discarded list.
        mergedEvents.remove(firstEvent);
        allPayloadDataDtos.remove(1);
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
    
    protected void mergeRawEvents(Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {
        
        List<EventGenerated> processedEventsByEventStatus = null;
        int size = entry.getValue().size() - 1;
        EventGenerated lastEvent = entry.getValue().get(size);
        List<EventTypePayloadDataDto> allPayloadDataDtos = (List<EventTypePayloadDataDto>)lastEvent.getEventTypePayload().getPayloadValues();
        List<EventTypePayloadDataDto> currentPayloadDataDtos;
        
        // All old Raw Events to be merged.
        for (EventGenerated eventGenerated : entry.getValue()) {
            currentPayloadDataDtos = (List<EventTypePayloadDataDto>)eventGenerated.getEventTypePayload().getPayloadValues();
            allPayloadDataDtos.add(currentPayloadDataDtos.get(0));
            eventGenerated.setEventStatus(EventStatus.Merged);
        }
        // Removing the last payload from the payload as this is one being kept.
        allPayloadDataDtos.remove(1);

        // All old processed Events to be merged.
        processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Merged);
                currentPayloadDataDtos = (List<EventTypePayloadDataDto>)eventGenerated.getEventTypePayload().getPayloadValues();
                allPayloadDataDtos.addAll(currentPayloadDataDtos);
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }
        /*
         * In Price Change, Only first latest event(by date) has to be
         * considered. Rest have to be discarded.
         */
        updateEventStatus(lastEvent, EventStatus.Processed);
        logger.info(new Gson().toJson(lastEvent.getEventTypePayload()));
    }
}
