package com.proptiger.data.event.generator;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
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
    private static Logger         logger = Logger.getLogger(DBEventGenerator.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    @Autowired
    private RawDBEventGenerator   rawDBEventGenerator;

    @Autowired
    private RawDBEventService     rawDBEventService;

    @Override
    public boolean isEventGenerationRequired() {

        Long rawEventCount = eventGeneratedService.getRawEventCount();

        if (rawEventCount > EventConstants.MAX_RAW_EVENT_COUNT) {
            return false;
        }

        return true;
    }

    @Override
    public Integer generateEvents() {

        Integer eventCount = 0;

        List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();
        logger.info(" RETRIEVED RAW DB EVENTS "+new Gson().toJson(rawDBEvents));
        // TODO: Run below code in multiple threads
        for (RawDBEvent rawDBEvent : rawDBEvents) {
            System.out.println("\n*************\n");
            System.out.println(" EACH RAW "+new Gson().toJson(rawDBEvent));
            System.out.flush();
            rawDBEvent = rawDBEventService.populateRawDBEventData(rawDBEvent);

            List<EventGenerated> events = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);

            for (EventGenerated event : events) {
                DBEventProcessor dbEventProcessor = event.getEventType().getEventTypeConfig().getProcessorObject();
                if (!dbEventProcessor.populateEventSpecificData(event)) {
                    events.remove(event);
                }
            }

            eventCount += events.size();

            // persist the events and update the last date in dbRawEventTableLog
            eventGeneratedService.persistEvents(events, rawDBEvent.getDbRawEventTableLog());
        }

        return eventCount;
    }

}
