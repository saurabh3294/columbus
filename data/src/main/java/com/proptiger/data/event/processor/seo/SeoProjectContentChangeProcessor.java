package com.proptiger.data.event.processor.seo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.core.mode.event.payload.dto.EventTypePayloadDataDto;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventGenerated.EventStatus;
import com.proptiger.data.event.processor.DBEventProcessor;

@Service
public class SeoProjectContentChangeProcessor extends DBEventProcessor {

    protected EventGenerated mergeProcessedEvents(Map.Entry<String, List<EventGenerated>> entry) {
        return mergeEvents(entry.getValue());
    }

    @SuppressWarnings("unchecked")
    protected EventGenerated mergeRawEvents(
            Map.Entry<String, List<EventGenerated>> entry,
            Map<String, List<EventGenerated>> allCurrentProcessedEvents,
            Map<EventStatus, List<EventGenerated>> updateEventsByOldStatusMap) {

        EventGenerated lastEvent = mergeEvents(entry.getValue());
        Map<String, EventTypePayloadDataDto> allPayloadDataDtos = (Map<String, EventTypePayloadDataDto>) lastEvent
                .getEventTypePayload().getPayloadValues();

        // All old processed Events to be merged.
        List<EventGenerated> processedEventsByEventStatus = allCurrentProcessedEvents.get(entry.getKey());
        if (processedEventsByEventStatus != null) {
            for (EventGenerated eventGenerated : processedEventsByEventStatus) {
                eventGenerated.setEventStatus(EventStatus.Merged);
                eventGenerated.setMergedEventId(lastEvent.getId());
                allPayloadDataDtos.putAll((Map<String, EventTypePayloadDataDto>) eventGenerated.getEventTypePayload()
                        .getPayloadValues());
            }
            updateEventsByOldStatusMap.get(EventStatus.Processed).addAll(processedEventsByEventStatus);
        }
        return lastEvent;
    }

    @SuppressWarnings("unchecked")
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
        Map<String, EventTypePayloadDataDto> allPayloadDataDtos = (Map<String, EventTypePayloadDataDto>) lastEvent
                .getEventTypePayload().getPayloadValues();
        for (EventGenerated eventGenerated : events) {
                allPayloadDataDtos.putAll((Map<String, EventTypePayloadDataDto>) eventGenerated.getEventTypePayload()
                        .getPayloadValues());
                eventGenerated.setEventStatus(EventStatus.Merged);
                eventGenerated.setMergedEventId(lastEvent.getId());
        }

        return lastEvent;
    }
}
