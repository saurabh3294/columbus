package com.proptiger.data.notification.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;

public interface NotificationGeneratedDao extends PagingAndSortingRepository<NotificationGenerated, Integer> {
    public List<NotificationGenerated> findByStatusAndExpiryTimeLessThan(NotificationStatus notificationStatus, Date date);
}
