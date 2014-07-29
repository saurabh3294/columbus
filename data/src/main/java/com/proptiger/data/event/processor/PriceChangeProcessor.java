package com.proptiger.data.event.processor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.service.EventGeneratedService;

@Component
public class PriceChangeProcessor extends DBEventProcessor {

    @Autowired
    private EventGeneratedService eventGeneratedService;
    
    @Override
    // TODO Does transactional works on list of objects.
    public List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        List<EventGenerated> processedEvents = eventGeneratedService.getProcessedEventsToBeMerged();
        
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        Map<String, List<EventGenerated>> groupProcessedEvents = groupEventsByKey(processedEvents) ;       

        List<EventGenerated> processedEventsByKey = null;
        // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {
            // All old Raw Events to be discarded.
            for (EventGenerated eventGenerated : entry.getValue()) {
                eventGenerated.setEventStatus(EventStatus.Discarded);
            }
            // All old processed Events to be discarded.
            processedEventsByKey = groupProcessedEvents.get(entry.getKey());
            if(processedEventsByKey != null){
                for (EventGenerated eventGenerated: groupProcessedEvents.get(entry.getKey())){
                    eventGenerated.setEventStatus(EventStatus.Discarded);
                }
            }
            /*
             * In Price Change, Only first latest event(by date) has to be
             * considered. Rest have to be discarded.
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
        Map<String, List<EventGenerated>> groupEventMap = groupEventsByKey(events);
        
     // TODO to process them in separate threads
        for (Map.Entry<String, List<EventGenerated>> entry : groupEventMap.entrySet()) {

            for (EventGenerated eventGenerated : entry.getValue()) {
                eventGenerated.setEventStatus(EventStatus.Discarded);
            }
            /*
             * In Price Change, Only first latest event(by date) has to be
             * considered for verification. Rest have to be discarded.
             */
            EventGenerated firstEvent = entry.getValue().get(0);
            firstEvent.setEventStatus(EventStatus.PendingVerification);
            updateEventHistories(firstEvent, EventStatus.PendingVerification);
            updateEventExpiryTime(firstEvent);
        }

        return events;
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
