package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;

/**
 * 
 * @author sahil
 *
 */
public interface EventGeneratedDao extends PagingAndSortingRepository<EventGenerated, Integer>{
    public List<EventGenerated> findByEventStatusOrderByCreatedDateAsc(EventStatus status);
    public List<EventGenerated> findByEventStatusAndExpiryDateLessThanEqualOrderByCreatedDateAsc(EventStatus status, Date expiryDate);
    public List<EventGenerated> findByEventStatusAndExpiryDateGreaterThanOrderByCreatedDateAsc(EventStatus status, Date expiryDate);
         
    @Modifying
    @Query("Update EventGenerated E set E.eventStatus = ?1 where E.eventStatus = ?2 and E.id=?3 ")
    public Integer updateEventStatusByIdAndOldStatus(EventStatus newEventStatus, EventStatus oldEventStatus, int id );
	
    @Query("Select count(id) from EventGenerated E where E.eventStatus = ?1 ")
	public Integer getEventCountByEventStatus(EventStatus eventStatus);
}
