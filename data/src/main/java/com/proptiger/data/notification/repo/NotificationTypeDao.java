package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationType;

public interface NotificationTypeDao extends PagingAndSortingRepository<NotificationType, Integer> {
    
    List<NotificationType> findByName(String name);
    
}
