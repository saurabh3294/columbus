package com.proptiger.data.event.generator;

import java.util.Date;
import java.util.List;

import com.proptiger.data.event.model.Event;

/**
 * Generates events in a time period
 * @author mandeep
 *
 */
public interface EventGenerator {
    List<? extends Event> generateEvents(Date startDate, Date endDate);
}
