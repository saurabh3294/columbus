/**
 * 
 */
package com.proptiger.data.event.processor;

import java.util.List;

import com.proptiger.data.event.model.Event;

/**
 * @author mandeep
 *
 * This handles merging/suppression of events
 */
public interface RawEventProcessor {
    List<? extends Event> process(List<Event> events);
}
