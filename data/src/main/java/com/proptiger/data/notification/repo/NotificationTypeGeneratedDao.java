package com.proptiger.data.notification.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationTypeGenerated;

public interface NotificationTypeGeneratedDao extends PagingAndSortingRepository<NotificationTypeGenerated, Integer> {

    @Query("Select count(id) from NotificationTypeGenerated N where N.notificationStatus = ?1 ")
    public Integer getNotificationTypeCountByNotificationStatus(NotificationStatus status);
}
