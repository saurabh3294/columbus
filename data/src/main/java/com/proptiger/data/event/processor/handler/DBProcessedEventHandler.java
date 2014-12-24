package com.proptiger.data.event.processor.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventType;
import com.proptiger.core.model.event.EventTypeConfig;
import com.proptiger.data.event.model.DefaultEventTypeConfig;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.service.EventGeneratedService;

@Service
public class DBProcessedEventHandler extends DBEventProcessorHandler {

    private static Logger         logger = LoggerFactory.getLogger(DBProcessedEventHandler.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    /**
     * Get the Processed events from DB whose Holding period has expired and
     * groups them by their event type. Calls the processor corresponding to the
     * event type to process these events.
     */
    @Override
    public void handleEvents() {
        List<EventGenerated> eventsGenerated = eventGeneratedService.getProcessedEvents();
        Map<String, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);

        // TODO to make the loop as multi threaded or Async
        DefaultEventTypeConfig defaultEventTypeConfig = null;
        for (Map.Entry<String, List<EventGenerated>> entry : EventsGroupedByEventType.entrySet()) {
            EventType eventType = entry.getValue().get(0).getEventType();
            defaultEventTypeConfig = (DefaultEventTypeConfig)eventType.getEventTypeConfig();
            DBEventProcessor processor = defaultEventTypeConfig.getProcessorObject();
            logger.debug("Processing Processed events whose holding period has expired for eventType: " + entry
                    .getKey() + " using processor: " + processor.getClass().getName());
            processor.processProcessedEvents(eventType.getId(), entry.getValue());
        }
    }
}
