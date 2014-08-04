package com.proptiger.data.event.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.constants.EventConstants;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.event.service.RawDBEventService;

/**
 * This is the implementation of EventGenerator to generate events from DB
 * source
 * 
 * @author sahil
 * 
 */

@Service
public class DBEventGenerator implements EventGeneratorInterface {

    @Autowired
    private EventGeneratedService eventGeneratedService;

    @Autowired
    private RawDBEventGenerator   rawDBEventGenerator;

    @Autowired
    private RawDBEventService     rawDBEventService;

    @Override
    public boolean isEventGenerationRequired() {

        Integer rawEventCount = eventGeneratedService.getRawEventCount();

        if (rawEventCount > EventConstants.MAX_RAW_EVENT_COUNT) {
            return false;
        }

        return true;
    }
   
    @Override
    public Integer generateEvents() {

        Integer eventCount = 0;

        List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();

        // TODO: Run below code in multiple threads
        for (RawDBEvent rawDBEvent : rawDBEvents) {

            rawDBEvent = rawDBEventService.populateRawDBEventData(rawDBEvent);

            List<EventGenerated> events = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);
            eventCount += events.size();

            for (EventGenerated event : events) {
                DBEventProcessor dbEventProcessor = event.getEventType().getEventTypeConfig().getProcessorObject();
                dbEventProcessor.populateEventSpecificData(event);
            }
            
            // persist the events and update the last date in dbRawEventTableLog
            eventGeneratedService.persistEvents(events, rawDBEvent.getDbRawEventTableLog());
        }

        return eventCount;
    }

}
