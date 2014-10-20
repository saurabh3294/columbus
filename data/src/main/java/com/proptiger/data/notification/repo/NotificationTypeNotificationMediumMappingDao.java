package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;

public interface NotificationTypeNotificationMediumMappingDao extends
        PagingAndSortingRepository<NotificationTypeNotificationMediumMapping, Integer> {

    @Query("SELECT M FROM NotificationTypeNotificationMediumMapping M LEFT JOIN FETCH M.notificationMedium LEFT JOIN FETCH M.notificationType")
    public List<NotificationTypeNotificationMediumMapping> findAllMapping();
}
