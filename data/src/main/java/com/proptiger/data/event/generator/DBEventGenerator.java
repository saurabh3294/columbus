package com.proptiger.data.event.generator;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private static Logger         logger = LoggerFactory.getLogger(DBEventGenerator.class);

    @Autowired
    private EventGeneratedService eventGeneratedService;

    @Autowired
    private RawDBEventGenerator   rawDBEventGenerator;

    @Autowired
    private RawDBEventService     rawDBEventService;

    @Value("${event.raw.maxCount}")
    private Integer               MAX_RAW_EVENT_COUNT;

    @Override
    public boolean isEventGenerationRequired() {
        Long rawEventCount = eventGeneratedService.getRawEventCount();
        if (rawEventCount > MAX_RAW_EVENT_COUNT) {
            logger.error("Skipping creation of EventGenerated as current Raw event count in DB is " + rawEventCount
                    + " but the Max permissible Raw event count is "
                    + MAX_RAW_EVENT_COUNT);
            return false;
        }
        return true;
    }

    /**
     * 1. Generates the RawDBEvents, 2. Populate data for each RawDBEvent e.g.
     * old values for Update events, 3. Generates the EventGenerateds
     * corresponding to each RawDBEvent, 4. Populate the event specific data in
     * the EventGenerated
     */
    @Override
    public Integer generateEvents() {
        Integer eventCount = 0;
        List<RawDBEvent> rawDBEvents = rawDBEventGenerator.getRawDBEvents();

        // TODO: Run below code in multiple threads
        for (RawDBEvent rawDBEvent : rawDBEvents) {
            Object transactionId = rawDBEvent.getTransactionKeyValue();
            rawDBEvent = rawDBEventService.populateRawDBEventData(rawDBEvent);

            // Skipping the raw DB Event if its old value is null
            if (rawDBEvent == null) {
                logger.error("Skipping RawDBEvent with transactionId " + transactionId);
                continue;
            }

            List<EventGenerated> events = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);

            // Populating event specific data in EventGenerateds. Remove
            // EventGenerated if we are unable to populate event specific data
            Iterator<EventGenerated> it = events.iterator();
            while (it.hasNext()) {
                EventGenerated event = it.next();
                DBEventProcessor dbEventProcessor = event.getEventType().getEventTypeConfig().getProcessorObject();
                event = dbEventProcessor.populateEventSpecificData(event);
                if (event == null) {
                    logger.error("Skipping EventGenerated with transactionId " + transactionId);
                    it.remove();
                }
            }

            eventCount += events.size();

            // Persist the events and update the last read transaction value in
            // dbRawEventTableLog
            eventGeneratedService.persistEvents(events, rawDBEvent.getRawEventTableDetails());
        }

        return eventCount;
    }

}
