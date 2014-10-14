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

import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;
import com.proptiger.data.notification.repo.NotificationTypeNotificationMediumMappingDao;

@Service
public class NotificationTypeNotificationMediumMappingService {

    private static Logger                                logger            = LoggerFactory
                                                                                   .getLogger(NotificationTypeNotificationMediumMappingService.class);
    private static final String                          DELIMITER         = ".";

    @Autowired
    private NotificationTypeNotificationMediumMappingDao nMappingDao;

    private Map<Integer, List<NotificationMedium>>       typeMediumMapping = new HashMap<Integer, List<NotificationMedium>>();
    private static Map<String, String>                   templatesMap      = new HashMap<String, String>();

    @PostConstruct
    public void buildTypeMediumMappingAndTemplateMap() {
        Iterable<NotificationTypeNotificationMediumMapping> ib = findAll();
        Iterator<NotificationTypeNotificationMediumMapping> it = ib.iterator();

        NotificationTypeNotificationMediumMapping mapping = null;
        List<NotificationMedium> notificationMediums = null;
        while (it.hasNext()) {
            mapping = it.next();
            notificationMediums = typeMediumMapping.get(mapping.getNotificationType().getId());
            if (notificationMediums == null) {
                notificationMediums = new ArrayList<NotificationMedium>();
            }
            notificationMediums.add(mapping.getNotificationMedium());
            typeMediumMapping.put(mapping.getNotificationType().getId(), notificationMediums);
            templatesMap.put(mapping.getNotificationType().getId() + DELIMITER
                    + mapping.getNotificationMedium().getId(), mapping.getSendTemplate());
        }
    }

    public Iterable<NotificationTypeNotificationMediumMapping> findAll() {
        return nMappingDao.findAll();
    }

    public Map<Integer, List<NotificationMedium>> getTypeMediumMapping() {
        return typeMediumMapping;
    }

    public void setTypeMediumMapping(Map<Integer, List<NotificationMedium>> typeMediumMapping) {
        this.typeMediumMapping = typeMediumMapping;
    }

    public String getTemplate(Integer ntType, Integer ntMediumType) {
        if (ntType == null || ntMediumType == null) {
            logger.error("Notification type or Notification Medium type is null");
            return null;
        }
        return templatesMap.get(ntType + DELIMITER + ntMediumType);
    }
}
