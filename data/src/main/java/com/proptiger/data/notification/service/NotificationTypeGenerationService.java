package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.repo.NotificationTypeGeneratedDao;

@Service
public class NotificationTypeGenerationService {

    @Autowired
    private NotificationTypeGeneratedDao              notificationTypeGeneratedDao;

    @Autowired
    private SubscriberConfigService                   subscriberConfigService;

    @Autowired
    private EventTypeToNotificationTypeMappingService ntMappingService;

    private Gson                                      serializer = new Gson();

    public Integer getActiveNotificationTypeCount() {
        return notificationTypeGeneratedDao
                .getNotificationTypeCountByNotificationStatus(NotificationStatus.NotificationTypeGenerated);
    }

    public List<NotificationTypeGenerated> getNotificationTypesForEventGenerated(EventGenerated eventGenerated) {
        List<NotificationTypeGenerated> notificationTypeGeneratedList = new ArrayList<NotificationTypeGenerated>();
        List<NotificationType> notificationTypeList = ntMappingService.getNotificationTypesByEventType(eventGenerated
                .getEventType());
        for (NotificationType notificationType : notificationTypeList) {
            NotificationTypePayload payload = notificationType.getNotificationTypeConfig()
                    .getNotificationTypePayloadObject();
            // TODO: add data to payload
            NotificationTypeGenerated ntGenerated = new NotificationTypeGenerated();
            ntGenerated.setEventGenerated(eventGenerated);
            ntGenerated.setNotificationType(notificationType);
            ntGenerated.setNotificationTypePayload(payload);
            notificationTypeGeneratedList.add(ntGenerated);
        }
        return notificationTypeGeneratedList;
    }

    public void persistNotificationTypes(EventGenerated eventGenerated, List<NotificationTypeGenerated> ntGeneratedList) {
        saveOrUpdateEvents(ntGeneratedList);
        subscriberConfigService.setLastEventDateReadByNotification(eventGenerated.getUpdatedDate());
    }

    public Iterable<NotificationTypeGenerated> saveOrUpdateEvents(Iterable<NotificationTypeGenerated> notificationTypes) {
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

    private void populateNotificationTypeDataBeforeSave(NotificationTypeGenerated notificationTypeGenerated) {
        notificationTypeGenerated.setData(serializer.toJson(notificationTypeGenerated.getNotificationTypePayload()));
    }

}
