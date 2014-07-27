/**
 * 
 */
package com.proptiger.data.event.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.Event;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.repo.EventGeneratedDao;

/**
 * @author mandeep
 *
 */
public class DBRawEventProcessorHandler implements RawEventProcessorHandler {
    
    @Autowired
    private EventGeneratedDao eventGeneratedDao;
    
    @Autowired
    private ApplicationContext applicationContext;
    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     */
    @Override
    public void handleRawEvents() {
         List<EventGenerated> eventsGenerated = eventGeneratedDao.findByStatusOrderByCreatedDateAsc(EventGenerated.EventStatus.Raw.name());
         Map<EventType, List<EventGenerated>> EventsGroupedByEventType = groupEventsByEventType(eventsGenerated);
         
         // TODO to make the loop as multi threaded or Async
         for(Map.Entry<EventType, List<EventGenerated>> entry: EventsGroupedByEventType.entrySet()){
             entry.getKey().getName().getProcessorObject().processRawEvents(entry.getValue());
         }
        // TODO Auto-generated method stub
    }
    
    private Map<EventType, List<EventGenerated>> groupEventsByEventType(List<EventGenerated> eventsGenerated){
        Map<EventType, List<EventGenerated>> mapEvents = new HashMap<EventType, List<EventGenerated>>();
        List<EventGenerated> groupEvents;// = new ArrayList<EventGenerated>();
        
        for(EventGenerated eventGenerated : eventsGenerated){
            groupEvents = mapEvents.get(eventGenerated.getEventType());
            if(groupEvents == null){
                groupEvents = new ArrayList<EventGenerated>();
            }
            groupEvents.add(eventGenerated);
            mapEvents.put(eventGenerated.getEventType(), groupEvents);
        }
        
        return mapEvents;
    }

}
