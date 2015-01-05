package com.proptiger.data.event.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.event.EventType;

public interface EventTypeDao extends PagingAndSortingRepository<EventType, Integer> {

}
