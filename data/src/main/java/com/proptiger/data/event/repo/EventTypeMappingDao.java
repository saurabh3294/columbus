package com.proptiger.data.event.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventTypeMapping;

public interface EventTypeMappingDao extends PagingAndSortingRepository<EventTypeMapping, Integer> {

}
