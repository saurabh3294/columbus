package com.proptiger.data.event.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventType;

public interface EventTypeDao extends PagingAndSortingRepository<EventType, Integer> {

}
