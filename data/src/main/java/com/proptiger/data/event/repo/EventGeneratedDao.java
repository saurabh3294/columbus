package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventGenerated;

/**
 * 
 * @author sahil
 *
 */
public interface EventGeneratedDao extends PagingAndSortingRepository<EventGenerated, Integer>{
    public List<EventGenerated> findByStatusOrderByCreatedDateAsc(String status);
    public List<EventGenerated> findByStatusAndExpiryDateOrderByCreatedDateAsc(String status, Date expiryDate);
	
	public Integer getEventCountByEventStatus(EventGenerated.EventStatus eventStatus);
}
