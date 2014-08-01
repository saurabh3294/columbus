package com.proptiger.data.event.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.constants.EventConstants;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.service.EventGeneratedService;

/**
 * This is the implementation of EventGenerator to generate events from DB
 * source
 * 
 * @author sahil
 * 
 */

@Component
public class DBEventGenerator implements EventGeneratorInterface {

    @Autowired
    private EventGeneratedService eventGeneratedService;

    @Autowired
    private RawDBEventGenerator   rawDBEventGenerator;

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

            rawDBEventGenerator.populateRawDBEventData(rawDBEvent);

            List<EventGenerated> events = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);
            eventCount += events.size();

            for (EventGenerated event : events) {
                DBEventProcessor dbEventProcessor = event.getEventType().getEventTypeConfig().getProcessorObject();
                dbEventProcessor.populateEventSpecificData(event);
            }

            eventGeneratedService.persistEvents(events);
        }

        return eventCount;
    }

}
