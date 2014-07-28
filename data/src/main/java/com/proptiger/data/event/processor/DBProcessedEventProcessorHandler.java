package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.repo.EventGeneratedDao;

public class DBProcessedEventProcessorHandler extends DBEventProcessorHandler{
    @Autowired
    private EventGeneratedDao eventGeneratedDao;
    
    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     */
    @Override
    public void handleEvents() {
         List<EventGenerated> eventsGenerated = eventGeneratedDao.findByStatusAndExpiryDateOrderByCreatedDateAsc(EventGenerated.EventStatus.Processed.name(), new Date());
         Map<EventType, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);
         
         // TODO to make the loop as multi threaded or Async
         for(Map.Entry<EventType, List<EventGenerated>> entry: EventsGroupedByEventType.entrySet()){
             entry.getKey().getName().getProcessorObject().processRawEvents(entry.getValue());
         }
        // TODO Auto-generated method stub
    }
        
}
