package com.proptiger.data.repo.marketplace;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.marketplace.MarketplaceNotificationType;
import com.proptiger.data.model.marketplace.Notification;

/**
 * 
 * @author azi
 * 
 */
public interface NotificationDao extends JpaRepository<Notification, Integer> {
    public Notification findByObjectIdAndNotificationTypeId(int objectId, int notificationTypeId);

    public List<Notification> findByObjectIdInAndNotificationTypeId(List<Integer> objectIds, int notificationTypeId);

    public List<Notification> findByNotificationTypeId(int notificationTypeId);

    public List<Notification> findByObjectIdInAndNotificationTypeIdAndReadFalse(
            List<Integer> objectIds,
            int notificationTypeId);

    public List<Notification> findByUserIdAndNotificationTypeId(int userId, int notificationTypeId);

    @Query(
            value = "SELECT N from LeadTask LT INNER JOIN LT.notifications N INNER JOIN LT.taskStatus LTS WHERE N.notificationTypeId = ?1 AND LTS.masterTaskId IN (?2)")
    public List<Notification> findByNotificationTypeIdAndMasterTaskIdIn(
            int notificationTypeId,
            List<Integer> masterTaskIds);

    public List<Notification> findByIdIn(List<Integer> ids);

    @Query(value = "SELECT N FROM Notification N JOIN FETCH N.notificationType NT WHERE N.userId = ?1")
    public List<Notification> getNotificationWithTypeForUser(int userId);

    @Query(
            value = "SELECT DISTINCT NT FROM MarketplaceNotificationType NT INNER JOIN FETCH NT.notifications N WHERE N.userId = ?1")
    public List<MarketplaceNotificationType> getNotificationTypesForUser(int userId);

    @Query(
            value = "SELECT DISTINCT NT FROM MarketplaceNotificationType NT INNER JOIN FETCH NT.notifications N WHERE N.userId = ?1 AND NT.id = ?2")
    public List<MarketplaceNotificationType> getNotificationTypesForUser(int userId, int notificationTypeId);

    @Query(
            value = "SELECT N FROM LeadTask LT INNER JOIN LT.notifications N INNER JOIN LT.taskStatus TS WHERE LT.leadOfferId = ?1 AND N.objectId != ?2 AND N.notificationTypeId = ?3 AND TS.masterTaskId IN (?4)")
    public List<Notification> getInvalidTaskNotificationForLeadOffer(
            int leadOfferId,
            int validTaskId,
            int notificationTypeId,
            List<Integer> masterTaskIds);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete marketplace.notifications from marketplace.notifications join marketplace.lead_offers on (marketplace.notifications.object_id = marketplace.lead_offers.id) and marketplace.notifications.notification_type_id = ?2 and marketplace.lead_offers.lead_id in (?1) and marketplace.lead_offers.status_id = ?3")
    public void deleteUsingNotificationTypeAndObjectId(String leadIdString, int notificationTypeId, int status_id);

    public Notification findByUserIdAndNotificationTypeIdAndObjectId(int userId, int notificationTypeId, int objectId);

    @Transactional
    @Modifying
    @Query("delete from Notification N where N.userId = ?1 and N.notificationTypeId = ?2")
    public void deleteNotification(int userId, int notificationTypeId);

    @Modifying
    @Transactional
    @Query("delete from Notification N where N.userId = ?1 and N.notificationTypeId = ?2 and N.objectId = ?3")
    public void deleteRMNotification(int userId, int notificationTypeId, int objectId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete marketplace.notifications from marketplace.notifications left join " + "(select agent_id as agent_id from marketplace.lead_offers inner join marketplace.lead_tasks on lead_offers.next_task_id = lead_tasks.id "
                    + " where lead_tasks.scheduled_for < ?1 group by lead_offers.agent_id having count(*) >= ?2) sub "
                    + "on notifications.object_id = sub.agent_id where notifications.notification_type_id = ?3 and sub.agent_id is null")
    public void deleteTooManyTaskNotification(
            Date scheduledForBefore,
            long overDueTaskCount,
            int tooManyNotificationTypeId);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "delete marketplace.notifications from marketplace.notifications left join marketplace.lead_tasks lt on marketplace.notifications.object_id = lt.id and lt.scheduled_for between ?1 and ?2 left join marketplace.master_lead_task_status_mappings mlts on mlts.id = lt.lead_task_status_id and mlts.master_task_id in (?4) where marketplace.notifications.notification_type_id = ?3 and lt.id is null")
    public void deleteTaskNotificationNotScheduledBetween(
            Date validStartTime,
            Date validEndTime,
            int notificationTypeId,
            List<Integer> masterTaskIds);
}