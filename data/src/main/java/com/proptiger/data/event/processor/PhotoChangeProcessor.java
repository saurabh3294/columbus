package com.proptiger.data.event.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.Event;
import com.proptiger.data.event.model.EventGenerated;

@Service
public class PhotoChangeProcessor extends DBEventProcessor {

    @Override
    public List<EventGenerated> processRawEvents(List<EventGenerated> events) {
        // TODO Auto-generated method stub
        return null;
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
    public boolean populateEventSpecificData(EventGenerated event) {
        // TODO Auto-generated method stub
        return false;
    }

}
