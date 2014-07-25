/**
 * 
 */
package com.proptiger.data.event.eventprocessor;

import java.util.List;

import com.proptiger.data.event.model.Event;

/**
 * @author mandeep
 * Responsible for taking actions on certain event types like producing notifications
 */
public interface EventSubscriber {
    void handle(List<? extends Event> events);
}
