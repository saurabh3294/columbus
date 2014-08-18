package com.proptiger.data.notification.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;

public interface NotificationGeneratedDao extends JpaRepository<NotificationGenerated, Integer> {
    public List<NotificationGenerated> findByStatusAndExpiryTimeLessThan(NotificationStatus notificationStatus, Date date);
    
    @Query("UPDATE NotificationGenerated set status = ?2 WHERE status = ?3 AND id = ?1 ")
    public Integer updateByStatusOnOldStatus(Integer id, NotificationStatus newStatus, NotificationStatus oldStatus);

    public List<NotificationGenerated> findByStatusAndExpiryTimeGreaterThanEqual(NotificationStatus scheduled, Date date);
}
