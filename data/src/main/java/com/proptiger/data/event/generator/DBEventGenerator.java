package com.proptiger.data.event.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.constants.EventConstants;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.repo.EventGeneratedDao;

/**
 * This is the implementation of EventGenerator to generate events from DB source
 * @author sahil
 * 
 */

@Component
public class DBEventGenerator implements EventGeneratorInterface {
		
	@Autowired
	private EventGeneratedDao eventGeneratedDao;
	
	@Autowired
	private RawDBEventGenerator rawDBEventGenerator;
	
	@Override
	public boolean isEventGenerationRequired() {
		
		Integer rawEventCount = eventGeneratedDao.getEventCountByEventStatus(EventGenerated.EventStatus.Raw);
		
		if (rawEventCount > EventConstants.MAX_RAW_EVENT_COUNT) {
			return false;
		}
		
		return true;
	}

	@Override
	public Integer generateEvents() {
		
		Integer eventCount = 0;
		
		// TODO:
		List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();
		
		// TODO: Run below code in multiple threads
		
		for (RawDBEvent rawDBEvent : rawDBEvents) {
			
			// TODO:
			rawDBEventGenerator.populateRawDBEventData(rawDBEvent);
			
			List<EventGenerated> events = generateEventFromRawDBEvent(rawDBEvent);
			eventCount += events.size();
			
			for (EventGenerated event: events) { 
				populateEventSpecificData(event);
				persistEvent(event);
			}
		}
		
		return eventCount;
	}

	private List<EventGenerated> generateEventFromRawDBEvent(RawDBEvent rawDBEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateEventSpecificData(EventGenerated event) {
		DBEventProcessor dbEventProcessor = event.getEventType().getName().getProcessorObject();
		dbEventProcessor.populateEventSpecificData(event);		
	}

	@Override
	public void persistEvent(EventGenerated event) {
		eventGeneratedDao.save(event);		
	}

}
