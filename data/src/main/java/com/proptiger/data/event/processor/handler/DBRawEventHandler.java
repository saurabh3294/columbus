/**
 * 
 */
package com.proptiger.data.event.processor.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.service.EventGeneratedService;

/**
 * @author mandeep
 *
 */
@Service
public class DBRawEventHandler extends DBEventProcessorHandler {
    
    @Autowired
    private EventGeneratedService eventGeneratedService;
    
    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     * TODO splitting the code to handle db generated Events and processing in seperate methods. to handle
     * multithreading.
     */
    @Override
    public void handleEvents() {
         List<EventGenerated> eventsGenerated = eventGeneratedService.getRawEvents();
         Map<EventType, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);
         try {
             System.out.println(new ObjectMapper().writeValueAsString(eventsGenerated));
            System.out.println(new ObjectMapper().writeValueAsString(EventsGroupedByEventType));
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         // TODO to make the loop as multi threaded or Async
         for(Map.Entry<EventType, List<EventGenerated>> entry: EventsGroupedByEventType.entrySet()){
             System.out.println(" EVENT BEING PROCESSED "+entry.getKey().getEventTypeConfig().getProcessorObject().getClass().getName()  );
             entry.getKey().getEventTypeConfig().getProcessorObject().processRawEvents(entry.getValue());
         }
        // TODO Auto-generated method stub
    }
    
    

}
