package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.SubscriberConfig;

public interface SubscriberConfigDao extends PagingAndSortingRepository<SubscriberConfig, Integer> {
    
    @Query("SELECT M FROM SubscriberConfig M LEFT JOIN FETCH M.subscriber")
    public List<SubscriberConfig> findAllMapping();
}
