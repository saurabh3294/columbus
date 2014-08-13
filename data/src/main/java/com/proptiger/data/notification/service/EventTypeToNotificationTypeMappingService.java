package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventType;
import com.proptiger.data.notification.model.EventTypeToNotificationTypeMapping;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.repo.EventTypeToNotificationTypeMappingDao;

@Service
public class EventTypeToNotificationTypeMappingService {

    @Autowired
    private EventTypeToNotificationTypeMappingDao       ntMappingDao;

    @Autowired
    private NotificationTypeService                     notificationTypeService;

    private static Map<Integer, List<NotificationType>> eventTypeToNotificationTypeMap;

    @PostConstruct
    private void constuctMappingFromDB() {
        Iterable<EventTypeToNotificationTypeMapping> ntMappingList = ntMappingDao.findAll();
        Iterator<EventTypeToNotificationTypeMapping> iterator = ntMappingList.iterator();

        while (iterator.hasNext()) {
            EventTypeToNotificationTypeMapping ntMapping = iterator.next();
            Integer eventTypeId = ntMapping.getEventType().getId();
            NotificationType notificationType = ntMapping.getNotificationType();
            notificationType = notificationTypeService.populateNotificationTypeConfig(notificationType);

            List<NotificationType> ntList = eventTypeToNotificationTypeMap.get(eventTypeId);

            if (ntList == null) {
                ntList = new ArrayList<NotificationType>();
                ntList.add(notificationType);
                eventTypeToNotificationTypeMap.put(eventTypeId, ntList);
            }
            else {
                ntList.add(notificationType);
            }
        }
    }

    public List<NotificationType> getNotificationTypesByEventType(EventType eventType) {
        List<NotificationType> notificationTypes = eventTypeToNotificationTypeMap.get(eventType.getId());
        if (notificationTypes == null) {
            return new ArrayList<NotificationType>();
        }
        return notificationTypes;
    }

    public Map<Integer, List<NotificationType>> getEventTypeToNotificationTypeMap() {
        return eventTypeToNotificationTypeMap;
    }

    public void setEventTypeToNotificationTypeMap(Map<Integer, List<NotificationType>> eventTypeToNotificationTypeMap) {
        EventTypeToNotificationTypeMappingService.eventTypeToNotificationTypeMap = eventTypeToNotificationTypeMap;
    }

}
