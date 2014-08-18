package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;
import com.proptiger.data.notification.repo.NotificationTypeNotificationMediumMappingDao;

@Service
public class NotificationTypeNotificationMediumMappingService {

    @Autowired
    private NotificationTypeNotificationMediumMappingDao nMappingDao;
    
    private Map<Integer, List<NotificationMedium>> typeMediumMapping = new HashMap<Integer, List<NotificationMedium>>();
    
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
}
