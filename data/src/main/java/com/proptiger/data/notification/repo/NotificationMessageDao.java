package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationMessage;

public interface NotificationMessageDao extends PagingAndSortingRepository<NotificationMessage, Integer> {
    List<NotificationMessage> findByStatus(NotificationStatus notificationStatus, Pageable pageable);
}
