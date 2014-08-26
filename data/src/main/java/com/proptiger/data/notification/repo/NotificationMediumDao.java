package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationMedium;

public interface NotificationMediumDao extends PagingAndSortingRepository<NotificationMedium, Integer> {
   
}
