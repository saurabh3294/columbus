package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventType;
import com.proptiger.data.notification.model.EventTypeToNotificationTypeMapping;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.repo.EventTypeToNotificationTypeMappingDao;

@Service
public class EventTypeToNotificationTypeMappingService {

    private static Logger                               logger                         = LoggerFactory
                                                                                               .getLogger(EventTypeToNotificationTypeMappingService.class);

    @Autowired
    private EventTypeToNotificationTypeMappingDao       ntMappingDao;

    @Autowired
    private NotificationTypeService                     notificationTypeService;

    private static Map<Integer, List<NotificationType>> eventTypeToNotificationTypeMap = new HashMap<Integer, List<NotificationType>>();

    @PostConstruct
    private void constuctMappingFromDB() {
        Iterable<EventTypeToNotificationTypeMapping> ntMappingList = ntMappingDao.findAllMapping();
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
        logger.debug(eventTypeToNotificationTypeMap.toString());
        if (notificationTypes == null) {
            logger.debug("Cannot find NotificationTypes for eventType " + eventType.getName());
            return new ArrayList<NotificationType>();
        }
        logger.debug("Found " + notificationTypes.size() + " NotificationTypes for eventType " + eventType.getName());
        return notificationTypes;
    }

    public Map<Integer, List<NotificationType>> getEventTypeToNotificationTypeMap() {
        return eventTypeToNotificationTypeMap;
    }

    public void setEventTypeToNotificationTypeMap(Map<Integer, List<NotificationType>> eventTypeToNotificationTypeMap) {
        EventTypeToNotificationTypeMappingService.eventTypeToNotificationTypeMap = eventTypeToNotificationTypeMap;
    }

}
