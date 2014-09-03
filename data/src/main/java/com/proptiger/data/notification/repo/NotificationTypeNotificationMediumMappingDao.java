package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationTypeNotificationMediumMapping;

public interface NotificationTypeNotificationMediumMappingDao extends PagingAndSortingRepository<NotificationTypeNotificationMediumMapping, Integer> {

}
