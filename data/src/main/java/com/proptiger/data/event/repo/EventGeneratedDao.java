package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.notification.model.Subscriber.SubscriberName;

/**
 * 
 * @author sahil
 * 
 */
public interface EventGeneratedDao extends PagingAndSortingRepository<EventGenerated, Integer> {

    public List<EventGenerated> findByEventStatusOrderByCreatedAtAsc(EventStatus status);

    public List<EventGenerated> findByEventStatusAndExpiryDateLessThanEqualOrderByCreatedAtAsc(
            EventStatus status,
            Date expiryDate);

    public List<EventGenerated> findByEventStatusAndEventTypeIdAndExpiryDateGreaterThanOrderByCreatedAtAsc(
            EventStatus status,
            Integer eventTypeId,
            Date expiryDate);

    public List<EventGenerated> findByEventStatusAndUpdatedAtGreaterThanOrderByUpdatedAtAsc(
            EventStatus status,
            Date updatedDate);

    public List<EventGenerated> findByEventStatusOrderByUpdatedAtDesc(EventStatus eventStatus, Pageable pageable);

    @Modifying
    @Query("Update EventGenerated E set E.eventStatus = ?1 where E.eventStatus = ?2 and E.id=?3 ")
    public Integer updateEventStatusByIdAndOldStatus(EventStatus newEventStatus, EventStatus oldEventStatus, int id);

    @Modifying
    @Query("Update EventGenerated E set E.eventStatus = ?1, E.mergedEventId = ?4 where E.eventStatus = ?2 and E.id=?3 ")
    public Integer updateEventStatusAndMergeIdByIdAndOldStatus(
            EventStatus newEventStatus,
            EventStatus oldEventStatus,
            int id,
            int mergeId);

    @Query("Select count(id) from EventGenerated E where E.eventStatus = ?1 ")
    public Long getEventCountByEventStatus(EventStatus eventStatus);

    @Query("Select e from EventGenerated e JOIN Fetch e.subscriberMapping sm JOIN e.eventType et JOIN Fetch sm.subscriber s where e.eventStatus = ?1 and s.subscriberName = ?2 and et.name IN ?3 and e.id > s.lastEventGeneratedId order by e.id asc")
    public List<EventGenerated> getLatestEventGeneratedBySubscriber(
            EventStatus EventStatus,
            SubscriberName subscriberName,
            List<String> listEventTypeNames,
            Pageable pageable);

    @Query("Select e from EventGenerated e JOIN Fetch e.subscriberMapping sm JOIN Fetch sm.subscriber s where e.eventStatus = ?1 and s.subscriberName = ?2 and e.id > s.lastEventGeneratedId order by e.id asc")
    public List<EventGenerated> getLatestEventGeneratedBySubscriber(
            EventStatus EventStatus,
            SubscriberName subscriberName,
            Pageable pageable);
    
    @Modifying
    @Query("Update EventGenerated e JOIN e.eventType ET set status = ?3 WHERE eventTypeUniqueKey = ?2 and ET.name = ?1")
    public Integer updateEventStatusByEventTypeAndUniqueKey(String eventTypeName, String uniqueKey, EventStatus eventStatus);
}
