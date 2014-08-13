package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationType;

public interface NotificationTypeDao extends PagingAndSortingRepository<NotificationType, Integer> {

}
