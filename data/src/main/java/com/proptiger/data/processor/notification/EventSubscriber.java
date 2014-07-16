/**
 * 
 */
package com.proptiger.data.processor.notification;

import java.util.List;

import com.proptiger.data.model.event.Event;

/**
 * @author mandeep
 * Responsible for taking actions on certain event types like producing notifications
 */
public interface EventSubscriber {
    void handle(List<? extends Event> events);
}
