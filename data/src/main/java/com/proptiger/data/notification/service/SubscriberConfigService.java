package com.proptiger.data.notification.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.proptiger.data.util.DateUtil;

@Service
public class SubscriberConfigService {

    private static Logger                          logger              = LoggerFactory
                                                                               .getLogger(SubscriberConfigService.class);

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
        Iterable<SubscriberConfig> subscriberConfigList = findAllSubscriberConfig();
        Iterator<SubscriberConfig> subscriberConfigIterator = subscriberConfigList.iterator();

        while (subscriberConfigIterator.hasNext()) {
            SubscriberConfig subscriberConfig = subscriberConfigIterator.next();
            SubscriberName subscriberName = subscriberConfig.getSubscriber().getSubscriberName();
            ConfigName configName = subscriberConfig.getConfigName();
            subscriberConfigMap.put(generateKey(subscriberName, configName), subscriberConfig.getConfigValue());
        }

        Iterable<Subscriber> subscriberList = findAllSubscriber();
        Iterator<Subscriber> subscriberIterator = subscriberList.iterator();

        while (subscriberIterator.hasNext()) {
            Subscriber subscriber = subscriberIterator.next();
            subscriberMap.put(subscriber.getSubscriberName(), subscriber);
        }
    }
    
    public Iterable<SubscriberConfig> findAllSubscriberConfig() {
        return subscriberConfigDao.findAllMapping();
    }
    
    public Iterable<Subscriber> findAllSubscriber() {
        return subscriberDao.findAll();
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
        Subscriber subscriber = subscriberMap.get(Subscriber.SubscriberName.Notification);
        if (subscriber == null) {
            logger.info("Notification Subscriber not found in DB");
        }
        Date lastEventDate = subscriber.getLastEventDate();
        logger.debug("Date of last consumend event by Notification Subscriber is " + lastEventDate);
        
        if (lastEventDate == null) {          
            EventGenerated eventGenerated = eventGeneratedService.getLatestEventGenerated();
            logger.debug("Latest event generated: " + eventGenerated);
            
            if (eventGenerated != null) {
                // Subtracting 1 second to include current events.
                lastEventDate = DateUtil.addSeconds(eventGenerated.getCreatedDate(), -1);
            }
            else {
                lastEventDate = new Date();
            }
            logger.debug("Setting last event date as " + lastEventDate);
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
