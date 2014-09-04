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

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "delete n.* from marketplace.notifications n left join marketplace.lead_offers lo on n.object_id = lo.next_task_id left join marketplace.lead_tasks lt on lo.next_task_id = lt.id and lt.scheduled_for between ?1 and ?2 where n.notification_type_id = ?3 and lt.id is null")
    public void deleteTaskNotificationNotScheduledBetween(Date validStartTime, Date validEndTime, int notificationTypeId);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "delete n.* from marketplace.notifications n left join marketplace.lead_offers lo on n.object_id = lo.next_task_id left join marketplace.lead_tasks lt on lo.next_task_id = lt.id and lt.scheduled_for between ?1 and ?2 left join marketplace.master_lead_task_status_mappings mlts on mlts.id = lt.lead_task_status_id and mlts.master_task_id in (?4) where n.notification_type_id = ?3 and lt.id is null")
    public void deleteTaskNotificationNotScheduledBetween(
            Date validStartTime,
            Date validEndTime,
            int notificationTypeId,
            List<Integer> masterTaskIds);

    @Query(
            value = "SELECT N FROM LeadTask LT INNER JOIN LT.notifications N WHERE LT.leadOfferId = ?1 AND N.objectId != ?2 AND N.notificationTypeId = ?3")
    public List<Notification> getInvalidTaskNotificationForLeadOffer(
            int leadOfferId,
            int validTaskId,
            int notificationTypeId);

    public Notification findByObjectIdAndNotificationTypeId(int objectId, int notificationTypeId);

    public List<Notification> findByObjectIdInAndNotificationTypeId(List<Integer> objectIds, int notificationTypeId);

    public List<Notification> findByNotificationTypeId(int notificationTypeId);

    @Query(
            value = "SELECT N from LeadTask LT INNER JOIN LT.notifications N INNER JOIN LT.taskStatus LTS WHERE N.notificationTypeId = ?1 AND LTS.masterTaskId IN (?2)")
    public List<Notification> findByNotificationTypeIdAndMasterTaskIdIn(
            int notificationTypeId,
            List<Integer> masterTaskIds);

    public List<Notification> findByIdIn(List<Integer> ids);
}