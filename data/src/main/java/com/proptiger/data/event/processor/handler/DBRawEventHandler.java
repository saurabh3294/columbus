/**
 * 
 */
package com.proptiger.data.event.processor.handler;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proptiger.data.event.EventInitiator;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.service.EventGeneratedService;

/**
 * @author mandeep
 * 
 */
@Service
public class DBRawEventHandler extends DBEventProcessorHandler {

    private static Logger         logger = LoggerFactory.getLogger(DBEventProcessorHandler.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.proptiger.data.processor.notification.RawEventProcessor#process(java
     * .util.List) TODO splitting the code to handle db generated Events and
     * processing in seperate methods. to handle multithreading.
     */
    @Override
    public void handleEvents() {
        List<EventGenerated> eventsGenerated = eventGeneratedService.getRawEvents();
        Map<String, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);
        try {
            logger.info(" ALL EVENTS READ: "+new Gson().toJson(eventsGenerated));
            logger.info("### GROUPING###: " + new Gson().toJson(EventsGroupedByEventType));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO to make the loop as multi threaded or Async
        for (Map.Entry<String, List<EventGenerated>> entry : EventsGroupedByEventType.entrySet()) {
            
            EventGenerated eventGenerated = entry.getValue().get(0);
            logger.info(" EVENT BEING PROCESSED " + eventGenerated.getEventType().getEventTypeConfig().getProcessorObject()
                    .getClass().getName());
            eventGenerated.getEventType().getEventTypeConfig().getProcessorObject().processRawEvents(entry.getValue());
        }
        // TODO Auto-generated method stub
    }

}
