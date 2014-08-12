package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.SubscriberConfig;

public interface SubscriberConfigDao extends PagingAndSortingRepository<SubscriberConfig, Integer> {

}
