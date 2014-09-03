package com.proptiger.data.event.processor.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.service.EventGeneratedService;

@Service
public class DBProcessedEventHandler extends DBEventProcessorHandler{
    @Autowired
    private EventGeneratedService eventGeneratedService;
    
    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     */
    @Override
    public void handleEvents() {
         List<EventGenerated> eventsGenerated = eventGeneratedService.getProcessedEvents();
         Map<String, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);
         
         // TODO to make the loop as multi threaded or Async
         for(Map.Entry<String, List<EventGenerated>> entry: EventsGroupedByEventType.entrySet()){
             EventGenerated eventGenerated = entry.getValue().get(0);
             eventGenerated.getEventType().getEventTypeConfig().getProcessorObject().processProcessedEvents(entry.getValue());
         }
        // TODO Auto-generated method stub
    }
        
}
