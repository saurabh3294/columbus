/**
 * 
 */
package com.proptiger.data.event.legacy;

import java.util.List;


/**
 * @author mandeep
 * Responsible for taking actions on certain event types like producing notifications
 */
public interface EventSubscriber {
    void handle(List<? extends Event> events);
}
