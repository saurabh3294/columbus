package com.proptiger.data.event.generator;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
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
        logger.info(" Generate Raw Events.");

        Integer eventCount = 0;
        List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();
        // TODO: Run below code in multiple threads
        for (RawDBEvent rawDBEvent : rawDBEvents) {

            rawDBEvent = rawDBEventService.populateRawDBEventData(rawDBEvent);
            // Skipping the raw DB Event if its old value is null.
            if (rawDBEvent == null) {
                logger.info(" OLD VALUE NOT FOUND For transaction Id" + rawDBEvent.getTransactionKeyValue()
                        + " Hence skipping the raw event.");
                continue;
            }

            List<EventGenerated> events = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);

            Iterator<EventGenerated> it = events.iterator();
            while (it.hasNext()) {
                EventGenerated event = it.next();
                logger.info(" Iterating Event Type For Populating Old Value Based on Event Type " + event
                        .getEventType().getName());

                DBEventProcessor dbEventProcessor = event.getEventType().getEventTypeConfig().getProcessorObject();
                if (!dbEventProcessor.populateEventSpecificData(event)) {
                    logger.info(" Event Being Removed ");
                    it.remove();
                }
            }

            eventCount += events.size();
            
            // persist the events and update the last date in dbRawEventTableLog
            eventGeneratedService.persistEvents(events, rawDBEvent.getDbRawEventTableLog());
        }

        return eventCount;
    }

}
