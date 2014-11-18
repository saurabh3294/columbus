package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.util.Constants;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;
import com.proptiger.data.notification.repo.NotificationTypeNotificationMediumMappingDao;

@Service
public class NotificationTypeNotificationMediumMappingService {

    private static Logger                                logger = LoggerFactory
                                                                        .getLogger(NotificationTypeNotificationMediumMappingService.class);

    @Autowired
    private NotificationTypeNotificationMediumMappingDao nMappingDao;

    @Cacheable(value = Constants.CacheName.NOTIFICATION_MEDIUMS, key = "#notificationTypeId")
    public List<NotificationMedium> getMediumsByNotificationTypeId(Integer notificationTypeId) {
        logger.debug("GETTING NOTIFICATION MEDIUMS FOR NOTIFICATION TYPE ID: " + notificationTypeId);
        List<NotificationTypeNotificationMediumMapping> mappings = nMappingDao
                .findMappingsByNotificationTypeId(notificationTypeId);
        List<NotificationMedium> mediums = new ArrayList<NotificationMedium>();
        if (mappings == null || mappings.isEmpty()) {
            logger.error("No NotificationMediums found for NotificationTypeId: " + notificationTypeId);
            return mediums;
        }

        for (NotificationTypeNotificationMediumMapping mapping : mappings) {
            mediums.add(mapping.getNotificationMedium());
        }
        return mediums;
    }

    @Cacheable(value = Constants.CacheName.NOTIFICATION_TEMPLATE, key = "#notificationTypeId+':'+#notificationMediumId")
    public String getTemplateByNotificationTypeIdAndNotificationMediumId(
            Integer notificationTypeId,
            Integer notificationMediumId) {
        logger.debug("GETTING NOTIFICATION TEMPLATE FOR NOTIFICATION TYPE ID: " + notificationTypeId
                + " AND NOTIFICATION MEDIUM ID: "
                + notificationMediumId);
        List<NotificationTypeNotificationMediumMapping> mappings = nMappingDao
                .findMappingsByNotificationTypeIdAndNotificationMediumId(notificationTypeId, notificationMediumId);
        if (mappings == null || mappings.size() != 1) {
            logger.error("Zero or multiple templates found for NotificationTypeId: " + notificationTypeId
                    + " and NotificationMediumId: "
                    + notificationMediumId);
            return null;
        }

        NotificationTypeNotificationMediumMapping mapping = mappings.get(0);
        return mapping.getSendTemplate();
    }
}
