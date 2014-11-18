package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.SubscriberConfig;
import com.proptiger.data.notification.model.Subscriber.SubscriberName;
import com.proptiger.data.notification.model.SubscriberConfig.ConfigName;

public interface SubscriberConfigDao extends PagingAndSortingRepository<SubscriberConfig, Integer> {
    
    @Query("SELECT M FROM SubscriberConfig M LEFT JOIN FETCH M.subscriber")
    public List<SubscriberConfig> findAllMapping();
    
    @Query("SELECT SC FROM SubscriberConfig SC LEFT JOIN FETCH SC.subscriber S WHERE S.subscriberName = ?1 AND SC.configName = ?2")
    public List<SubscriberConfig> findConfigBySubscriber(SubscriberName subscriberName, ConfigName configName);
}
