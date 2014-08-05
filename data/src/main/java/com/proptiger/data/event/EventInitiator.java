package com.proptiger.data.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.generator.DBEventGenerator;
import com.proptiger.data.event.processor.handler.DBProcessedEventHandler;
import com.proptiger.data.event.processor.handler.DBRawEventHandler;

/**
 * It is responsible for generating Events from various sources like DB 
 * Functions of this class are called at regular intervals in order to generate Events
 * @author sahil
 */

@Component
public class EventInitiator {
	
	private static Logger logger = LoggerFactory.getLogger(EventInitiator.class);
	
	@Autowired
	private DBEventGenerator dbEventGenerator;
	
	@Autowired
	private DBRawEventHandler dbRawEventHandler;
	
	@Autowired 
	private DBProcessedEventHandler dbProcessedEventHandler;
	
	/**
	 * Generates the DB events at regular intervals.
	 */
	public void dbEventGenerator() {
		
		if (!dbEventGenerator.isEventGenerationRequired()) {
			logger.info("DBEventGenerator: Skipping DB Event Generation.");
			return;
		}
		
		logger.info("DBEventGenerator: Generating DB Events.");
		Integer numberOfEvents = dbEventGenerator.generateEvents();
		logger.info("DBEventGenerator: Generated " + numberOfEvents + " DB Events.");
	}
	
	@Scheduled(fixedDelay = 500000)
	public void dbRawEventProcessor(){
	    logger.info("DBRawEventProcessor: Process Raw Events started");
	    dbRawEventHandler.handleEvents();
	    logger.info("DBRawEventProcessor: Process Raw Events ended.");
	}
	
	public void dbProcessedEventProcessor(){
	    
	}
}
