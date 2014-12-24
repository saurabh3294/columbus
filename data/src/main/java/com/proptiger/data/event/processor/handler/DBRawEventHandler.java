package com.proptiger.data.event.processor.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventType;
import com.proptiger.data.event.model.DefaultEventTypeConfig;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.service.EventGeneratedService;

/**
 * @author mandeep
 * 
 */
@Service
public class DBRawEventHandler extends DBEventProcessorHandler {

    private static Logger         logger = LoggerFactory.getLogger(DBRawEventHandler.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    /**
     * Get the Raw events from DB and groups them by their event type. Calls the
     * processor corresponding to the event type to process the raw events.
     */
    @Override
    public void handleEvents() {
        List<EventGenerated> eventsGenerated = eventGeneratedService.getRawEvents();
        Map<String, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);

        // TODO to make the loop as multi threaded or Async
        DefaultEventTypeConfig defaultEventTypeConfig = null;
        for (Map.Entry<String, List<EventGenerated>> entry : EventsGroupedByEventType.entrySet()) {
            EventType eventType = entry.getValue().get(0).getEventType();
            defaultEventTypeConfig = (DefaultEventTypeConfig)eventType.getEventTypeConfig();
            DBEventProcessor processor = defaultEventTypeConfig.getProcessorObject();
            logger.debug("Processing Raw events for eventType: " + entry.getKey()
                    + " using processor: "
                    + processor.getClass().getName());
            processor.processRawEvents(eventType.getId(), entry.getValue());
        }
    }

}
