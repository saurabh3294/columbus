package com.proptiger.data.event.generator;

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
	
}
