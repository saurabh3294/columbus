/**
 * 
 */
package com.proptiger.data.event.processor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.service.EventGeneratedService;

/**
 * @author mandeep
 *
 */
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
         
         // TODO to make the loop as multi threaded or Async
         for(Map.Entry<EventType, List<EventGenerated>> entry: EventsGroupedByEventType.entrySet()){
             entry.getKey().getEventTypeConfig().getProcessorObject().processRawEvents(entry.getValue());
         }
        // TODO Auto-generated method stub
    }
    
    

}
