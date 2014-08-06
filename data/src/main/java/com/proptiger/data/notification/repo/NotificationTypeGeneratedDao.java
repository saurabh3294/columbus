package com.proptiger.data.notification.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.notification.enums.NotificationStatus;

public interface NotificationTypeGeneratedDao extends PagingAndSortingRepository<EventGenerated, Integer> {

    @Query("Select count(id) from NotificationTypeGenerated N where N.notificationStatus = ?1 ")
    public Integer getNotificationTypeCountByNotificationStatus(NotificationStatus status);
}
