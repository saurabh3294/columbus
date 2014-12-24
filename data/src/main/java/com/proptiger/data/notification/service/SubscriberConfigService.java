package com.proptiger.data.notification.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.event.subscriber.Subscriber;
import com.proptiger.core.model.event.subscriber.SubscriberConfig;
import com.proptiger.core.model.event.subscriber.Subscriber.SubscriberName;
import com.proptiger.core.model.event.subscriber.SubscriberConfig.ConfigName;
import com.proptiger.core.util.Constants;
import com.proptiger.data.notification.repo.SubscriberConfigDao;
import com.proptiger.data.notification.repo.SubscriberDao;

@Service
public class SubscriberConfigService {

    private static Logger                          logger        = LoggerFactory
                                                                         .getLogger(SubscriberConfigService.class);

    @Autowired
    private SubscriberConfigDao                    subscriberConfigDao;

    @Autowired
    private SubscriberDao                          subscriberDao;

    @Autowired
    private ApplicationContext                     applicationContext;

     private static Map<SubscriberName, Subscriber> subscriberMap = new HashMap<SubscriberName, Subscriber>();

    @PostConstruct
    public void constructSubscriberConfig() {
        Iterable<Subscriber> subscriberList = findAllSubscriber();
        Iterator<Subscriber> subscriberIterator = subscriberList.iterator();

        while (subscriberIterator.hasNext()) {
            Subscriber subscriber = subscriberIterator.next();
            subscriberMap.put(subscriber.getSubscriberName(), subscriber);
        }
    }

    public Iterable<Subscriber> findAllSubscriber() {
        return subscriberDao.findAll();
    }

    /**
     * Returns the count of Max active notification type count present in DB
     * 
     * @return
     */
    public Integer getMaxActiveNotificationTypeCount() {
        SubscriberName subscriberName = Subscriber.SubscriberName.Notification;
        ConfigName configName = SubscriberConfig.ConfigName.MaxActiveNotificationTypeCount;
        String configValue = applicationContext.getBean(SubscriberConfigService.class).getSubscriberConfig(
                subscriberName,
                configName);
        if (configValue == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(configValue);
    }

    /**
     * Returns the count of Max active notification message count present in DB
     * 
     * @return
     */
    public Integer getMaxActiveNotificationMessageCount() {
        SubscriberName subscriberName = Subscriber.SubscriberName.Notification;
        ConfigName configName = SubscriberConfig.ConfigName.MaxActiveNotificationMessageCount;
        String configValue = applicationContext.getBean(SubscriberConfigService.class).getSubscriberConfig(
                subscriberName,
                configName);
        if (configValue == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(configValue);
    }

    public Integer getMaxSubscriberEventTypeCount(SubscriberName subscriberName) {
        ConfigName configName = SubscriberConfig.ConfigName.MaxVerifedEventCount;
        String configValue = applicationContext.getBean(SubscriberConfigService.class).getSubscriberConfig(
                subscriberName,
                configName);
        if (configValue == null) {
            return Integer.MAX_VALUE;
        }
        return Integer.valueOf(configValue);
    }
    
	@Cacheable(value = Constants.CacheName.NOTIFICATION_SUBSCRIBER_CONFIG, key = "#subscriberName+':'+#configName")
    public String getSubscriberConfig(SubscriberName subscriberName, ConfigName configName) {
        logger.debug("GETTING SUBSCRIBER CONFIG FOR SUBSCRIBER: " + subscriberName + " and CONFIG: " + configName);
        List<SubscriberConfig> configs = subscriberConfigDao.findConfigBySubscriber(subscriberName, configName);
        if (configs == null || configs.isEmpty()) {
            logger.error("Config " + configName + " not found in DB for Subscriber " + subscriberName);
            return null;
        }
        return configs.get(0).getConfigValue();
    }

    public void setLastEventGeneratedIdBySubscriberName(Integer lastEventGeneratedId, SubscriberName subscriberName) {
        setLastEventGeneratedIdBySubscriber(lastEventGeneratedId, getSubscriber(subscriberName));
    }

    public void setLastEventGeneratedIdBySubscriber(Integer lastEventGeneratedId, Subscriber subscriber) {
        subscriber.setLastEventGeneratedId(lastEventGeneratedId);
        subscriberDao.updateLastEventGeneratedId(subscriber.getId(), lastEventGeneratedId);
    }

    public Subscriber getSubscriber(SubscriberName subscriberName) {
        return subscriberMap.get(subscriberName);
    }
}
