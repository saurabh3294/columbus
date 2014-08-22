package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.model.payload.NotificationTypeUpdateHistory;
import com.proptiger.data.notification.repo.NotificationTypeGeneratedDao;

@Service
public class NotificationTypeGeneratedService {

    private static Logger                             logger     = LoggerFactory
                                                                         .getLogger(NotificationTypeGeneratedService.class);

    @Autowired
    private NotificationTypeGeneratedDao              notificationTypeGeneratedDao;

    @Autowired
    private SubscriberConfigService                   subscriberConfigService;

    @Autowired
    private EventTypeToNotificationTypeMappingService ntMappingService;

    @Autowired
    private NotificationTypeService                   notificationTypeService;

    private Gson                                      serializer = new Gson();

    public Long getActiveNotificationTypeCount() {
        return notificationTypeGeneratedDao
                .getNotificationTypeCountByNotificationStatus(NotificationStatus.TypeGenerated);
    }

    public List<NotificationTypeGenerated> getActiveNotificationTypeGenerated() {
        List<NotificationTypeGenerated> ntGeneratedList = notificationTypeGeneratedDao
                .findByNotificationStatusOrderByCreatedAtAsc(NotificationStatus.TypeGenerated);
        for (NotificationTypeGenerated ntGenerated : ntGeneratedList) {
            NotificationType notificationType = ntGenerated.getNotificationType();
            notificationType = notificationTypeService.populateNotificationTypeConfig(notificationType);
            ntGenerated.setNotificationType(notificationType);
        }
        return ntGeneratedList;
    }

    public List<NotificationTypeGenerated> getNotificationTypesForEventGenerated(EventGenerated eventGenerated) {
        logger.debug("Generating NotificationTypes for eventGeneratedId " + eventGenerated.getId());
        List<NotificationTypeGenerated> notificationTypeGeneratedList = new ArrayList<NotificationTypeGenerated>();
        List<NotificationType> notificationTypeList = ntMappingService.getNotificationTypesByEventType(eventGenerated
                .getEventType());
        for (NotificationType notificationType : notificationTypeList) {
            NotificationTypePayload payload = notificationType.getNotificationTypeConfig()
                    .getNotificationTypePayloadObject();

            EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
            payload.setPrimaryKeyName(eventTypePayload.getPrimaryKeyName());
            payload.setPrimaryKeyValue(eventTypePayload.getPrimaryKeyValue());
            payload.populatePayloadValues(eventTypePayload);

            NotificationTypeGenerated ntGenerated = new NotificationTypeGenerated();
            ntGenerated.setEventGeneratedId(eventGenerated.getId());
            ntGenerated.setNotificationType(notificationType);
            ntGenerated.setNotificationTypePayload(payload);
            notificationTypeGeneratedList.add(ntGenerated);
        }
        return notificationTypeGeneratedList;
    }

    @Transactional
    public void persistNotificationTypes(EventGenerated eventGenerated, List<NotificationTypeGenerated> ntGeneratedList) {
        saveOrUpdateTypes(ntGeneratedList);
        subscriberConfigService.setLastEventDateReadByNotification(eventGenerated.getUpdatedDate());
    }

    public Iterable<NotificationTypeGenerated> saveOrUpdateTypes(Iterable<NotificationTypeGenerated> notificationTypes) {
        Iterator<NotificationTypeGenerated> iterator = notificationTypes.iterator();
        while (iterator.hasNext()) {
            populateNotificationTypeDataBeforeSave(iterator.next());
        }
        notificationTypeGeneratedDao.save(notificationTypes);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        return notificationTypes;
    }

    public NotificationTypeGenerated saveOrUpdateType(NotificationTypeGenerated notificationType) {
        populateNotificationTypeDataBeforeSave(notificationType);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        notificationTypeGeneratedDao.save(notificationType);
        return notificationType;
    }

    private void populateNotificationTypeDataBeforeSave(NotificationTypeGenerated ntGenerated) {
        NotificationTypePayload payload = ntGenerated.getNotificationTypePayload();
        ntGenerated.setData(serializer.toJson(payload));
    }

    public void setMessageGeneratedStatusInTypeGenerated(NotificationTypeGenerated ntGenerated) {
        NotificationTypePayload payload = ntGenerated.getNotificationTypePayload();
        NotificationTypeUpdateHistory updateHistory = new NotificationTypeUpdateHistory(
                NotificationStatus.MessageGenerated,
                new Date());
        payload.addNotificationTypeUpdateHistory(updateHistory);

        ntGenerated.setNotificationStatus(NotificationStatus.MessageGenerated);
        saveOrUpdateType(ntGenerated);
    }

}
