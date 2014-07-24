package com.proptiger.data.notification.generator;

import java.util.Date;
import java.util.List;

import com.proptiger.data.model.event.Event;

/**
 * Generates events in a time period
 * @author mandeep
 *
 */
public interface EventGenerator {
    List<? extends Event> generateEvents(Date startDate, Date endDate);
}
