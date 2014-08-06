/**
 * 
 */
package com.proptiger.data.event.processor.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.event.model.Event;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;

/**
 * @author mandeep
 * 
 *         This handles merging/suppression of events
 */
public abstract class DBEventProcessorHandler implements EventProcessorHandler {
        
    protected Map<String, List<EventGenerated>> groupEventsByEventType(List<EventGenerated> eventsGenerated) {
        Map<String, List<EventGenerated>> mapEvents = new HashMap<String, List<EventGenerated>>();
        List<EventGenerated> groupEvents;// = new ArrayList<EventGenerated>();

        for (EventGenerated eventGenerated : eventsGenerated) {
            groupEvents = mapEvents.get(eventGenerated.getEventType().getName());
            if (groupEvents == null) {
                groupEvents = new ArrayList<EventGenerated>();
            }
            groupEvents.add(eventGenerated);
            mapEvents.put(eventGenerated.getEventType().getName(), groupEvents);
        }

        return mapEvents;
    }
}
