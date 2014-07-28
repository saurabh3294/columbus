package com.proptiger.data.event.generator;

import com.proptiger.data.event.model.EventGenerated;

/**
 * This is the interface class for generating events from various sources.
 * Each source has its own implementation for generating Events
 * 
 * @author sahil
 * 
 */
public interface EventGeneratorInterface {
	
	/**
	 * Checks if event generation is required or not.
	 * Returns false if there are already too many unprocessed events present.
	 * @return isEventGenerationRequired
	 */
	public boolean isEventGenerationRequired();
	
	/**
	 * Generate the events
	 * @return numberOfEventsGenerated
	 */
	public Integer generateEvents();
	
	/**
	 * Populate the Event with necessary details
	 * e.g. lastMonthsPrice for PriceChangeEvent
	 * @param event
	 */
	public void populateEventSpecificData(EventGenerated event);
	
	/**
	 * Persists the event in the DB
	 * @param event
	 */
	public void persistEvent(EventGenerated event);
}
