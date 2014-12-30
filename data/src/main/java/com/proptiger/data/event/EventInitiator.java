package com.proptiger.data.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.config.scheduling.QuartzScheduledClass;
import com.proptiger.core.config.scheduling.QuartzScheduledJob;
import com.proptiger.data.event.generator.DBEventGenerator;
import com.proptiger.data.event.processor.handler.DBProcessedEventHandler;
import com.proptiger.data.event.processor.handler.DBRawEventHandler;

/**
 * It is responsible for generating Events from various sources like DB
 * Functions of this class are called at regular intervals in order to generate
 * Events
 * 
 * @author sahil
 */

@Component
@QuartzScheduledClass
public class EventInitiator {

    private static Logger           logger = LoggerFactory.getLogger(EventInitiator.class);

    @Autowired
    private DBEventGenerator        dbEventGenerator;

    @Autowired
    private DBRawEventHandler       dbRawEventHandler;

    @Autowired
    private DBProcessedEventHandler dbProcessedEventHandler;

    /**
     * Creates a list of EventGenerateds in Raw State from DB Events at regular
     * intervals.
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.event}",
            initialDelayString = "${scheduler.initialdelay.event.dbEventGenerator}")
    public void dbEventGenerator() {
        logger.info("DBEventGenerator: Starting creation of EventGenerated");
        if (!dbEventGenerator.isEventGenerationRequired()) {
            logger.error("DBEventGenerator: Skipping creation of EventGenerated");
            return;
        }
        Integer numberOfEvents = dbEventGenerator.generateEvents();
        logger.info("DBEventGenerator: Created " + numberOfEvents + " EventGenerateds in DB");
    }

    /**
     * Takes the list of Raw events and Processed events from DB which are still
     * in holding state and Merge/Suppress the events of a particular primary
     * key based on the config of the event type.
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.event}",
            initialDelayString = "${scheduler.initialdelay.event.dbRawEventProcessor}")
    public void dbRawEventProcessor() {
        logger.info("DBRawEventProcessor: Process Raw Events started");
        dbRawEventHandler.handleEvents();
        logger.info("DBRawEventProcessor: Process Raw Events ended.");
    }

    /**
     * Takes the list of all events that are in Processed state and whose
     * holding period has expired marks it as PENDING_VERIFICATION if
     * verification is required else VERIFIED if no verification is required.
     */
    @QuartzScheduledJob(
            fixedDelayString = "${scheduler.fixeddelay.event}",
            initialDelayString = "${scheduler.initialdelay.event.dbProcessedEventProcessor}")
    public void dbProcessedEventProcessor() {
        logger.info("DBProcessedEventProcessor: Process Processed Events started");
        dbProcessedEventHandler.handleEvents();
        logger.info("DBProcessedEventProcessor: Process Processed Events ended.");
    }
}
