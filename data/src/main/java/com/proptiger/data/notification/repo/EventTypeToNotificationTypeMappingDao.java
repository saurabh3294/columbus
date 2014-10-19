package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.EventTypeToNotificationTypeMapping;

public interface EventTypeToNotificationTypeMappingDao extends
        PagingAndSortingRepository<EventTypeToNotificationTypeMapping, Integer> {

    @Query("SELECT M FROM EventTypeToNotificationTypeMapping M LEFT JOIN FETCH M.eventType LEFT JOIN FETCH M.notificationType")
    public List<EventTypeToNotificationTypeMapping> findAllMapping();

}
