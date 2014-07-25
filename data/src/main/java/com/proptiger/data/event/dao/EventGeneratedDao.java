package com.proptiger.data.event.dao;

import com.proptiger.data.event.model.EventGenerated;

/**
 * 
 * @author sahil
 *
 */
public interface EventGeneratedDao {
	
	public Integer getEventCountByEventStatus(EventGenerated.EventStatus eventStatus);
}
