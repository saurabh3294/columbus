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

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;
import com.proptiger.data.notification.repo.NotificationTypeNotificationMediumMappingDao;

@Service
public class NotificationTypeNotificationMediumMappingService {
    private static Logger           logger     = LoggerFactory.getLogger(NotificationTypeNotificationMediumMapping.class);
    
    @Autowired
    private NotificationTypeNotificationMediumMappingDao nMappingDao;
    
    private Map<Integer, List<NotificationMedium>> typeMediumMapping = new HashMap<Integer, List<NotificationMedium>>();
    private static Map<String, String> templatesMap;

    @PostConstruct
    public void buildTypeMediumMapping(){
        Iterable<NotificationTypeNotificationMediumMapping> ib = findAll();
        Iterator<NotificationTypeNotificationMediumMapping> it = ib.iterator();
        
        NotificationTypeNotificationMediumMapping mapping = null;
        List<NotificationMedium> notificationMediums = null;
        while(it.hasNext()){
            mapping = it.next();
            notificationMediums = typeMediumMapping.get(mapping.getNotification_type_id());
            if(notificationMediums == null){
                notificationMediums = new ArrayList<NotificationMedium>();
            }
            notificationMediums.add(mapping.getNotificationMedium());
            typeMediumMapping.put(mapping.getNotification_type_id(), notificationMediums);
        }
    }
    
    public Iterable<NotificationTypeNotificationMediumMapping> findAll(){
        return nMappingDao.findAll();
    }

    public Map<Integer, List<NotificationMedium>> getTypeMediumMapping() {
        return typeMediumMapping;
    }

    public void setTypeMediumMapping(Map<Integer, List<NotificationMedium>> typeMediumMapping) {
        this.typeMediumMapping = typeMediumMapping;
    }

    @PostConstruct
    private void populateTemplatesMap() {
        templatesMap = new HashMap<String, String>();
        Iterable<NotificationTypeNotificationMediumMapping> ntNmMappings = ntNmMappingDao.findAll();
        Iterator<NotificationTypeNotificationMediumMapping> itNtNmMappings = ntNmMappings.iterator();
        while (itNtNmMappings.hasNext()) {
            NotificationTypeNotificationMediumMapping ntNmMapping = itNtNmMappings.next();
            templatesMap.put(ntNmMapping.getNotificationType().getId() + "." + ntNmMapping.getNotificationMedium()
                    .getId(), ntNmMapping.getSendTemplate());
        }
    }
    
    public String getTemplate(String ntType, String ntMediumType) {
        if (ntType == null || ntMediumType == null) {
            logger.info("Notification type or Notification Medium type is null");
            return null;
        }
        return templatesMap.get(ntType + ntMediumType);
    }

    public String getTemplate(NotificationGenerated ntGenerated) {
        if (ntGenerated.getNotificationType().getId() == 0 || ntGenerated.getNotificationMedium().getId() == 0) {
            logger.info("Notification type or Notification Medium type id is zero");
            return null;
        }
        return templatesMap.get(ntGenerated.getNotificationType().getId() + "." +  ntGenerated.getNotificationMedium()
                .getId());
    }
}
