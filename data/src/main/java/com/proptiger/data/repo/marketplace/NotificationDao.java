package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.model.marketplace.NotificationType;

public interface NotificationDao extends JpaRepository<Notification, Integer> {
    @Query(
            value = "SELECT DISTINCT NT FROM NotificationType NT INNER JOIN FETCH NT.notifications N WHERE N.userId = ?1")
    public List<NotificationType> getNotificationTypesForUser(int userId);
}
