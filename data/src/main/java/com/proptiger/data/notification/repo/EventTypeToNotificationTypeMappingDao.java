package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.EventTypeToNotificationTypeMapping;

public interface EventTypeToNotificationTypeMappingDao extends
        PagingAndSortingRepository<EventTypeToNotificationTypeMapping, Integer> {

}
