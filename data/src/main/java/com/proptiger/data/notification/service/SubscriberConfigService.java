package com.proptiger.data.notification.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.SubscriberConfig;
import com.proptiger.data.notification.repo.SubscriberConfigDao;

@Service
public class SubscriberConfigService {

    @Autowired
    private SubscriberConfigDao        subscriberConfigDao;

    private static Map<String, String> subscriberConfigMap = new HashMap<String, String>();

    @PostConstruct
    public void constructSubscriberConfig() {
        Iterable<SubscriberConfig> subscriberConfigList = subscriberConfigDao.findAll();
        Iterator<SubscriberConfig> subscriberConfigIterator = subscriberConfigList.iterator();
        
    }

    public Integer getMaxActiveNotificationTypeCount() {
        return null;
    }

    public Date getLastEventDateReadByNotification() {
        return null;
    }

    public Date setLastEventDateReadByNotification(Date lastEventDate) {
        return null;
    }

    public Map<String, String> getSubscriberConfigMap() {
        return subscriberConfigMap;
    }

}
