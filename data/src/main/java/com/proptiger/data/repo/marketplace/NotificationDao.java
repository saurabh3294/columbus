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
    @Query(
            value = "SELECT DISTINCT NT FROM MarketplaceNotificationType NT INNER JOIN FETCH NT.notifications N WHERE N.userId = ?1")
    public List<MarketplaceNotificationType> getNotificationTypesForUser(int userId);

    @Query(
            value = "SELECT DISTINCT NT FROM MarketplaceNotificationType NT INNER JOIN FETCH NT.notifications N WHERE N.userId = ?1 AND NT.id = ?2")
    public List<MarketplaceNotificationType> getNotificationTypesForUser(int userId, int notificationTypeId);

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

    @Query(
            value = "SELECT N FROM LeadTask LT INNER JOIN LT.notifications N INNER JOIN LT.taskStatus TS WHERE LT.leadOfferId = ?1 AND N.objectId != ?2 AND N.notificationTypeId = ?3 AND TS.masterTaskId IN (?4)")
    public List<Notification> getInvalidTaskNotificationForLeadOffer(
            int leadOfferId,
            int validTaskId,
            int notificationTypeId,
            List<Integer> masterTaskIds);

    public Notification findByObjectIdAndNotificationTypeId(int objectId, int notificationTypeId);

    public List<Notification> findByObjectIdInAndNotificationTypeId(List<Integer> objectIds, int notificationTypeId);

    public List<Notification> findByNotificationTypeId(int notificationTypeId);

    @Query(
            value = "SELECT N from LeadTask LT INNER JOIN LT.notifications N INNER JOIN LT.taskStatus LTS WHERE N.notificationTypeId = ?1 AND LTS.masterTaskId IN (?2)")
    public List<Notification> findByNotificationTypeIdAndMasterTaskIdIn(
            int notificationTypeId,
            List<Integer> masterTaskIds);

    public List<Notification> findByIdIn(List<Integer> ids);

    @Query(value = "SELECT N FROM Notification N JOIN FETCH N.notificationType NT WHERE N.userId = ?1")
    public List<Notification> getNotificationWithTypeForUser(int userId);

    public List<Notification> findByObjectIdInAndNotificationTypeIdAndReadFalse(
            List<Integer> objectIds,
            int notificationTypeId);

    public List<Notification> findByUserIdAndNotificationTypeId(int userId, int notificationTypeId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value ="delete marketplace.notifications from marketplace.notifications join marketplace.lead_offers on (marketplace.notifications.object_id = marketplace.lead_offers.id) and marketplace.notifications.notification_type_id = ?2 and marketplace.lead_offers.lead_id in (?1) and marketplace.lead_offers.status_id = ?3")
    public void deleteUsingNotificationTypeAndObjectId(String leadIdString, int notificationTypeId,int status_id);
}