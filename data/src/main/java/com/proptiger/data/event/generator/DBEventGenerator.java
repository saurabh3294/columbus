package com.proptiger.data.event.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.dao.EventGeneratedDao;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;

/**
 * This is the implementation of EventGenerator to generate events from DB source
 * @author sahil
 * 
 */

@Component
public class DBEventGenerator implements EventGeneratorInterface {

	// TODO: Get from config
	private static final Integer MAX_RAW_EVENT_COUNT = 100;
	
	@Autowired
	private EventGeneratedDao eventGeneratedDao;
	
	@Autowired
	private RawDBEventGenerator rawDBEventGenerator;
	
	@Override
	public boolean isEventGenerationRequired() {
		
		Integer rawEventCount = eventGeneratedDao.getEventCountByEventStatus(EventGenerated.EventStatus.Raw);
		
		if (rawEventCount > MAX_RAW_EVENT_COUNT) {
			return false;
		}
		
		return true;
	}

	@Override
	public Integer generateEvents() {
		
		List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();
		
		// TODO: Run below code in multiple threads
		
		for (RawDBEvent rawDBEvent : rawDBEvents) {
			
			// TODO: add rawDBEventGenerator.populateDBEvents
		
			List<EventGenerated> events = generateEventFromRawDBEvent(rawDBEvent);
			
			for (EventGenerated event: events) { 
				populateSpecificEventData(event);
				persistEvent(event);
			}
		}
		
		// TODO: Add proper return statement
		return null;
	}

	private List<EventGenerated> generateEventFromRawDBEvent(RawDBEvent rawDBEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateSpecificEventData(EventGenerated event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void persistEvent(EventGenerated event) {
		// TODO Auto-generated method stub
		
	}


}
