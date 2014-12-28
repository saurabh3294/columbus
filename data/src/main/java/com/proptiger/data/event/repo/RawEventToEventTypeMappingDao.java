package com.proptiger.data.event.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.event.RawEventToEventTypeMapping;

public interface RawEventToEventTypeMappingDao extends PagingAndSortingRepository<RawEventToEventTypeMapping, Integer> {

    public List<RawEventToEventTypeMapping> findByEventTypeId(Integer eventTypeId);
    
    @Query("SELECT M FROM RawEventToEventTypeMapping M LEFT JOIN FETCH M.eventType LEFT JOIN FETCH M.rawEventTableDetails")
    public List<RawEventToEventTypeMapping> findAllMapping();

}
