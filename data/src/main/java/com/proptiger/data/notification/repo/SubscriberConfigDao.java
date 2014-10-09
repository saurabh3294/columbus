package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.SubscriberConfig;

public interface SubscriberConfigDao extends PagingAndSortingRepository<SubscriberConfig, Integer> {
    
    public List<SubscriberConfig> findAll();
}
