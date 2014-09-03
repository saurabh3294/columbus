package com.proptiger.data.notification.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.UserNotificationTypeSubscription;

public interface UserNotificationTypeSubscriptionDao extends PagingAndSortingRepository<UserNotificationTypeSubscription, Integer> {

}
