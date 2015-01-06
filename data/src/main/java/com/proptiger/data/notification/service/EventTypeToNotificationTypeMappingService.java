package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.event.EventType;
import com.proptiger.core.util.Constants;
import com.proptiger.data.notification.model.EventTypeToNotificationTypeMapping;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.repo.EventTypeToNotificationTypeMappingDao;

@Service
public class EventTypeToNotificationTypeMappingService {

    private static Logger                         logger = LoggerFactory
                                                                 .getLogger(EventTypeToNotificationTypeMappingService.class);

    @Autowired
    private EventTypeToNotificationTypeMappingDao ntMappingDao;

    @Autowired
    private NotificationTypeService               notificationTypeService;

    @Autowired
    private ApplicationContext                    applicationContext;

    /**
     * Returns the list of notification types mapped to a particular event type
     * 
     * @param eventType
     * @return
     */
    public List<NotificationType> getNotificationTypesByEventType(EventType eventType) {
        List<NotificationType> notificationTypes = new ArrayList<NotificationType>();

        List<EventTypeToNotificationTypeMapping> mappings = applicationContext.getBean(
                EventTypeToNotificationTypeMappingService.class).getMappingsByEventType(eventType.getId());
        if (mappings == null || mappings.isEmpty()) {
            logger.error("Cannot find NotificationTypes for eventType " + eventType.getName());
            return notificationTypes;
        }

        for (EventTypeToNotificationTypeMapping mapping : mappings) {
            NotificationType notificationType = mapping.getNotificationType();
            notificationType = notificationTypeService.populateNotificationTypeConfig(notificationType);
            notificationTypes.add(notificationType);
        }
        return notificationTypes;
    }

    @Cacheable(value = Constants.CacheName.NOTIFICATION_TYPES, key = "#eventTypeId")
    public List<EventTypeToNotificationTypeMapping> getMappingsByEventType(Integer eventTypeId) {
        logger.debug("GETTING NOTIFICATION TYPE MAPPINGS FOR EVENT TYPE ID: " + eventTypeId);
        return ntMappingDao.findAllMappingsByEventTypeId(eventTypeId);
    }

}
