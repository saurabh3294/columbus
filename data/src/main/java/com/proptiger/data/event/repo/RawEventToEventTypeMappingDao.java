package com.proptiger.data.event.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.RawEventToEventTypeMapping;

public interface RawEventToEventTypeMappingDao extends PagingAndSortingRepository<RawEventToEventTypeMapping, Integer> {

    public List<RawEventToEventTypeMapping> findByEventTypeId(Integer eventTypeId);
    
    @Query("SELECT M FROM RawEventToEventTypeMapping M LEFT JOIN FETCH M.eventType LEFT JOIN FETCH M.dbRawEventTableLog")
    public List<RawEventToEventTypeMapping> findAllMapping();

}
