package com.proptiger.data.event.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.RawEventToEventTypeMapping;

public interface RawEventToEventTypeMappingDao extends PagingAndSortingRepository<RawEventToEventTypeMapping, Integer> {

    public List<RawEventToEventTypeMapping> findByEventTypeId(Integer eventTypeId);
    
    public List<RawEventToEventTypeMapping> findAll();

}
