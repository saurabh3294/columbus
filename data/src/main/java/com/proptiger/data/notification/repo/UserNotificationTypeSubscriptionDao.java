package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.UserNotificationTypeSubscription;

public interface UserNotificationTypeSubscriptionDao extends PagingAndSortingRepository<UserNotificationTypeSubscription, Integer> {

    public List<UserNotificationTypeSubscription> findAll();
}