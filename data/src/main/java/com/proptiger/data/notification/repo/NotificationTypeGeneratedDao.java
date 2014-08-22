package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationTypeGenerated;

public interface NotificationTypeGeneratedDao extends JpaRepository<NotificationTypeGenerated, Integer> {

    public List<NotificationTypeGenerated> findByNotificationStatusOrderByCreatedAtAsc(NotificationStatus status);

    @Query("Select count(id) from NotificationTypeGenerated N where N.notificationStatus = ?1 ")
    public Long getNotificationTypeCountByNotificationStatus(NotificationStatus status);
}
