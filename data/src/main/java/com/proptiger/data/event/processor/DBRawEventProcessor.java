/**
 * 
 */
package com.proptiger.data.event.processor;

import java.util.List;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.Event;

/**
 * @author mandeep
 *
 */
public class DBRawEventProcessor implements RawEventProcessor {

    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     */
    @Override
    public List<EventGenerated> process(List<Event> events) {
        // TODO Auto-generated method stub
        return null;
    }

}
