package com.proptiger.data.event.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.event.model.Event;
import com.proptiger.data.event.model.EventGenerated;

public class PhotoChangeProcessor extends DBEventProcessor {

    @Override
    List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    List<EventGenerated> processProcessedEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    List<EventGenerated> processVerifiedEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void populateEventSpecificData(EventGenerated event) {
		// TODO Auto-generated method stub
		
	}
   
}
