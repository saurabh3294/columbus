package com.proptiger.data.notification.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.notification.model.Subscriber;
import com.proptiger.data.notification.model.Subscriber.SubscriberName;
import com.proptiger.data.notification.model.SubscriberConfig;
import com.proptiger.data.notification.model.SubscriberConfig.ConfigName;
import com.proptiger.data.notification.repo.SubscriberConfigDao;
import com.proptiger.data.notification.repo.SubscriberDao;

@Service
public class SubscriberConfigService {

    @Autowired
    private SubscriberConfigDao                    subscriberConfigDao;

    @Autowired
    private SubscriberDao                          subscriberDao;

    @Autowired
    private EventGeneratedService                  eventGeneratedService;

    private static Map<String, String>             subscriberConfigMap = new HashMap<String, String>();
    private static Map<SubscriberName, Subscriber> subscriberMap       = new HashMap<SubscriberName, Subscriber>();

    @PostConstruct
    public void constructSubscriberConfig() {
        Iterable<SubscriberConfig> subscriberConfigList = subscriberConfigDao.findAll();
        Iterator<SubscriberConfig> subscriberConfigIterator = subscriberConfigList.iterator();

        while (subscriberConfigIterator.hasNext()) {
            SubscriberConfig subscriberConfig = subscriberConfigIterator.next();
            SubscriberName subscriberName = subscriberConfig.getSubscriber().getSubscriberName();
            ConfigName configName = subscriberConfig.getConfigName();
            subscriberConfigMap.put(generateKey(subscriberName, configName), subscriberConfig.getConfigValue());
        }

        Iterable<Subscriber> subscriberList = subscriberDao.findAll();
        Iterator<Subscriber> subscriberIterator = subscriberList.iterator();

        while (subscriberIterator.hasNext()) {
            Subscriber subscriber = subscriberIterator.next();
            subscriberMap.put(subscriber.getSubscriberName(), subscriber);
        }
    }

    public Integer getMaxActiveNotificationTypeCount() {
        SubscriberName subscriberName = Subscriber.SubscriberName.Notification;
        ConfigName configName = SubscriberConfig.ConfigName.MaxActiveNotificationTypeCount;
        String configValue = subscriberConfigMap.get(generateKey(subscriberName, configName));
        if (configValue == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.valueOf(configValue);
    }

    public Integer getMaxActiveNotificationMessageCount() {
        SubscriberName subscriberName = Subscriber.SubscriberName.Notification;
        ConfigName configName = SubscriberConfig.ConfigName.MaxActiveNotificationMessageCount;
        String configValue = subscriberConfigMap.get(generateKey(subscriberName, configName));
        if (configValue == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.valueOf(configValue);
    }

    public Date getLastEventDateReadByNotification() {
        Date lastEventDate = subscriberMap.get(Subscriber.SubscriberName.Notification).getLastEventDate();
        if (lastEventDate == null) {
            EventGenerated eventGenerated = eventGeneratedService.getLatestEventGenerated();
            if (eventGenerated != null) {
                lastEventDate = eventGenerated.getCreatedDate();
            }
            else {
                lastEventDate = new Date();
            }
            setLastEventDateReadByNotification(lastEventDate);
        }
        return lastEventDate;
    }

    public void setLastEventDateReadByNotification(Date lastEventDate) {
        Subscriber subscriber = subscriberMap.get(Subscriber.SubscriberName.Notification);
        subscriber.setLastEventDate(lastEventDate);
        subscriberDao.updateLastEventDateById(subscriber.getId(), lastEventDate);
    }

    public Map<String, String> getSubscriberConfigMap() {
        return subscriberConfigMap;
    }

    private String generateKey(SubscriberName subscriberName, ConfigName configName) {
        return subscriberName + "." + configName;
    }

}
