package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.SubscriptionType;
import com.proptiger.data.notification.model.UserNotificationTypeSubscription;

public interface UserNotificationTypeSubscriptionDao extends
        PagingAndSortingRepository<UserNotificationTypeSubscription, Integer> {

    @Query("SELECT S FROM UserNotificationTypeSubscription S LEFT JOIN FETCH S.user U WHERE S.notificationTypeId = ?1 AND S.subscriptionType = ?2")
    public List<UserNotificationTypeSubscription> findByNotificationTypeIdAndSubscriptionType(
            Integer notificationTypeId,
            SubscriptionType subscriptionType);

    @Query("SELECT S FROM UserNotificationTypeSubscription S LEFT JOIN FETCH S.user U WHERE S.notificationTypeId IN ?1 AND U.id IN ?2")
    public List<UserNotificationTypeSubscription> findByNotificationTypeIdsAndUserIds(
            List<Integer> notificationTypeIds,
            List<Integer> userIds);

}
