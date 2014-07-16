/**
 * 
 */
package com.proptiger.data.processor.notification;

import java.util.List;

import com.proptiger.data.model.event.Event;

/**
 * @author mandeep
 *
 * This handles merging/suppression of events
 */
public interface RawEventProcessor {
    List<? extends Event> process(List<Event> events);
}
