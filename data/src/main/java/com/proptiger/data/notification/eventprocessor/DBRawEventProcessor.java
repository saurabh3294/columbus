/**
 * 
 */
package com.proptiger.data.notification.eventprocessor;

import java.util.List;

import com.proptiger.data.model.event.DBEventGenerated;
import com.proptiger.data.model.event.Event;

/**
 * @author mandeep
 *
 */
public class DBRawEventProcessor implements RawEventProcessor {

    /* (non-Javadoc)
     * @see com.proptiger.data.processor.notification.RawEventProcessor#process(java.util.List)
     */
    @Override
    public List<DBEventGenerated> process(List<Event> events) {
        // TODO Auto-generated method stub
        return null;
    }

}