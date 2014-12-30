package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.user.User;
import com.proptiger.core.util.Caching;
import com.proptiger.core.util.Constants;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.UserNotificationTypeSubscription;
import com.proptiger.data.notification.model.external.NotificationSubscriptionRequest;
import com.proptiger.data.notification.repo.UserNotificationTypeSubscriptionDao;

@Service
public class UserNotificationTypeSubscriptionService {

    private static Logger                       logger = LoggerFactory
                                                               .getLogger(UserNotificationTypeSubscriptionService.class);

    @Autowired
    private UserNotificationTypeSubscriptionDao userNTSubscriptionDao;

    @Autowired
    private NotificationTypeService             notificationTypeService;

    @Autowired
    private Caching                             caching;

    @Cacheable(
            value = Constants.CacheName.NOTIFICATION_SUBSCRIBED_USERS,
            key = "'notificationTypeId:'+#notificationTypeId+':primaryKey:'+#primaryKey+':'")
    public List<User> getSubscribedUsersByNotificationType(
            Integer notificationTypeId,
            Integer primaryKey,
            List<Integer> projectIds) {
        List<UserNotificationTypeSubscription> subscriptions = userNTSubscriptionDao
                .findSubscribedByNotificationTypeIdAndProjectIds(notificationTypeId, projectIds);
        return getUsersFromSubscriptions(subscriptions);
    }

    @Cacheable(
            value = Constants.CacheName.NOTIFICATION_UNSUBSCRIBED_USERS,
            key = "'notificationTypeId:'+#notificationTypeId+':'")
    public List<User> getUnsubscribedUsersByNotificationType(Integer notificationTypeId) {
        List<UserNotificationTypeSubscription> subscriptions = userNTSubscriptionDao
                .findUnsubscribedByNotificationTypeId(notificationTypeId);
        return getUsersFromSubscriptions(subscriptions);
    }

    /**
     * It will update the NotificationSubscriptions for given notificationTypes
     * and users in the request.
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<UserNotificationTypeSubscription> updateNotificationSubscription(NotificationSubscriptionRequest request) {
        List<Integer> notificationTypeIds = new ArrayList<Integer>();
        List<Integer> userIds = new ArrayList<Integer>();

        List<NotificationType> notificationTypes = notificationTypeService.findByNotificationTypeEnums(request
                .getNotificationTypes());
        for (NotificationType notificationType : notificationTypes) {
            notificationTypeIds.add(notificationType.getId());
        }

        for (User user : request.getUsers()) {
            userIds.add(user.getId());
        }

        logger.debug("Updating Notification Subscription to " + request.getSubscriptionType()
                + " for UserIds: "
                + userIds
                + " and notificationTypeIds: "
                + notificationTypeIds);

        List<UserNotificationTypeSubscription> subscriptions = userNTSubscriptionDao
                .findByNotificationTypeIdsAndUserIds(notificationTypeIds, userIds);

        logger.debug("Got " + subscriptions.size() + " subscriptions from DB");

        Map<String, UserNotificationTypeSubscription> subscriptionMap = getSubscriptionMap(subscriptions);

        for (User user : request.getUsers()) {
            for (Integer notificationTypeId : notificationTypeIds) {
                String subscriptionKey = getSubscriptionMapKey(user.getId(), notificationTypeId);
                UserNotificationTypeSubscription subscription = subscriptionMap.get(subscriptionKey);
                if (subscription == null) {
                    subscription = new UserNotificationTypeSubscription();
                    subscription.setNotificationTypeId(notificationTypeId);
                    subscription.setUser(user);
                    subscriptions.add(subscription);
                }
                subscription.setSubscriptionType(request.getSubscriptionType());
            }
        }

        logger.debug("Updated " + subscriptions.size() + " subscriptions");

        // Deleting entries from cache
        for (Integer notificationTypeId : notificationTypeIds) {
            String keyPattern = "notificationTypeId:" + notificationTypeId + ":";

            logger.debug("Deleting keyPattern: " + keyPattern + " from Cache");
            caching.deleteMultipleResponseFromCacheOnRegex(
                    keyPattern,
                    Constants.CacheName.NOTIFICATION_SUBSCRIBED_USERS);

            caching.deleteMultipleResponseFromCacheOnRegex(
                    keyPattern,
                    Constants.CacheName.NOTIFICATION_UNSUBSCRIBED_USERS);
        }

        logger.debug("Saving " + subscriptions.size() + " updated subscriptions");
        return (List<UserNotificationTypeSubscription>) userNTSubscriptionDao.save(subscriptions);
    }

    private List<User> getUsersFromSubscriptions(List<UserNotificationTypeSubscription> subscriptions) {
        List<User> users = new ArrayList<User>();
        Set<Integer> userIds = new HashSet<Integer>();
        if (subscriptions == null) {
            logger.debug("No subscriptions found in DB");
            return users;
        }
        for (UserNotificationTypeSubscription subscription : subscriptions) {
            User user = subscription.getUser();
            if (!userIds.contains(user.getId())) {
                users.add(user);
                userIds.add(user.getId());
            }
        }
        return users;
    }

    private Map<String, UserNotificationTypeSubscription> getSubscriptionMap(
            List<UserNotificationTypeSubscription> subscriptions) {
        Map<String, UserNotificationTypeSubscription> subscriptionMap = new HashMap<String, UserNotificationTypeSubscription>();
        for (UserNotificationTypeSubscription subscription : subscriptions) {
            subscriptionMap.put(
                    getSubscriptionMapKey(subscription.getUser().getId(), subscription.getNotificationTypeId()),
                    subscription);
        }
        return subscriptionMap;
    }

    private String getSubscriptionMapKey(Integer userId, Integer notificationTypeId) {
        return String.valueOf(userId) + "." + String.valueOf(notificationTypeId);
    }
}
