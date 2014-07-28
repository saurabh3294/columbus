package com.proptiger.data.event.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;

@Component
public class PriceChangeProcessor extends DBEventProcessor {

    @Override
    public List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        
        // TODO to process them in separate threads 
        for(Map.Entry<String, List<EventGenerated>> entry: groupEventMap.entrySet()){
        
            for(EventGenerated eventGenerated: entry.getValue()){
                eventGenerated.setEventStatus(EventStatus.Discarded);
            }
            /*
             * In Price Change, Only first latest event(by date) has to be considered. Rest have to
             * be discarded.
             */
            EventGenerated firstEvent = entry.getValue().get(0);
            firstEvent.setEventStatus(EventStatus.Processed);
            updateEventHistories(firstEvent, EventStatus.Processed);
            updateEventExpiryTime(firstEvent);
        }
        
        return events;
    }

    @Override
    public List<EventGenerated> processProcessedEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventGenerated> processVerifiedEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void populateEventSpecificData(EventGenerated event) {
		// TODO Auto-generated method stub
		
	}
    
}
