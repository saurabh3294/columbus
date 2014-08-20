package com.proptiger.data.notification.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;

public interface NotificationGeneratedDao extends PagingAndSortingRepository<NotificationGenerated, Integer> {
    public List<NotificationGenerated> findByNotificationStatusAndScheduleTimeLessThan(NotificationStatus notificationStatus, Date date);
    
    @Modifying
    @Query("UPDATE NotificationGenerated set notificationStatus = ?2 WHERE notificationStatus = ?3 AND id = ?1 ")
    public Integer updateByNotificationStatusOnOldNotificationStatus(Integer id, NotificationStatus newStatus, NotificationStatus oldStatus);

    @Query("SELECT NG FROM NotificationGenerated NG JOIN NG.notificationMedium NM WHERE NG.notificationStatus = ?1 AND NG.scheduleTime >= ?2 AND NM.id = ?3")
    public List<NotificationGenerated> findByStatusAndExpiryTimeGreaterThanEqualAndMediumId(
            NotificationStatus scheduled,
            Date date,
            int mediumId);

    @Query("SELECT NG FROM NotificationGenerated NG JOIN NG.notificationType NT JOIN NG.forumUser FU JOIN NG.notificationMedium NM WHERE NG.notificationStatus in ?1 AND NM.id = ?2 AND FU.userId = ?3 AND NT.id = ?4 AND NG.objectId = ?5 ORDER BY NG.updatedAt DESC")
    public List<NotificationGenerated> getLastNotificationGenerated(
            List<NotificationStatus> notificationStatusList,
            int mediumTypeId,
            Integer userId,
            int notificationTypeId,
            Integer objectId,
            Pageable pageable);

    @Query("SELECT NG FROM NotificationGenerated NG JOIN NG.forumUser FU JOIN NG.notificationMedium NM WHERE NG.notificationStatus in ?1 AND FU.userId = ?2 AND NM.id = ?3 ORDER BY NG.updatedAt DESC")
    public List<NotificationGenerated> getLastSentNotificationGeneratedInMedium(
            List<NotificationStatus> notificationStatusList,
            Integer userId,
            int mediumId,
            Pageable pageable);

    public List<NotificationGenerated> findByNotificationStatus(NotificationStatus generated);

    @Query("UPDATE NotificationGenerated set notificationStatus = ?2, scheduleTime = ?3 WHERE id = ?1 ")
    public void updatedByNotificationStatusAndScheduleTime(Integer id, NotificationStatus scheduled, Date scheduledTime);
    
    @Query("UPDATE NotificationGenerated set notificationStatus = ?2, WHERE id = ?1 ")
    public void updateByNotificationStatus(int id, NotificationStatus schedulersuppressed);
}
