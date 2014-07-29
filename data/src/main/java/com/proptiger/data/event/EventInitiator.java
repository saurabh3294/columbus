package com.proptiger.data.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.event.generator.DBEventGenerator;

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
}
