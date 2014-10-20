package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.user.User;
import com.proptiger.data.notification.enums.SubscriptionType;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.UserNotificationTypeSubscription;
import com.proptiger.data.notification.repo.UserNotificationTypeSubscriptionDao;

@Service
public class UserNotificationTypeSubscriptionService {

    private static Logger                       logger              = LoggerFactory
                                                                            .getLogger(UserNotificationTypeSubscriptionService.class);

    @Autowired
    private UserNotificationTypeSubscriptionDao userNTSubscriptionDao;

    // Map of NotificationTypeId to subscribed users
    private static Map<Integer, List<User>>     subscribedUserMap   = new HashMap<Integer, List<User>>();

    // Map of NotificationTypeId to unsubscribed users
    private static Map<Integer, List<User>>     unsubscribedUserMap = new HashMap<Integer, List<User>>();

    @PostConstruct
    private void constuctMappingFromDB() {
        Iterable<UserNotificationTypeSubscription> userNTSubscriptionList = findAll();
        Iterator<UserNotificationTypeSubscription> iterator = userNTSubscriptionList.iterator();

        while (iterator.hasNext()) {
            UserNotificationTypeSubscription userNTSubscription = iterator.next();

            SubscriptionType subscriptionType = userNTSubscription.getSubscriptionType();

            if (SubscriptionType.Subscribed.equals(subscriptionType)) {
                subscribedUserMap = addToNotificationTypeUserMap(userNTSubscription, subscribedUserMap);
            }
            else if (SubscriptionType.Unsubscribed.equals(subscriptionType)) {
                unsubscribedUserMap = addToNotificationTypeUserMap(userNTSubscription, unsubscribedUserMap);
            }
        }
    }

    public Iterable<UserNotificationTypeSubscription> findAll() {
        return userNTSubscriptionDao.findAll();
    }

    private Map<Integer, List<User>> addToNotificationTypeUserMap(
            UserNotificationTypeSubscription userNTSubscription,
            Map<Integer, List<User>> ntUserMap) {

        Integer notificationTypeId = userNTSubscription.getNotificationTypeId();
        User forumUser = userNTSubscription.getUser();

        List<User> userList = ntUserMap.get(notificationTypeId);

        if (userList == null) {
            userList = new ArrayList<User>();
            userList.add(forumUser);
            ntUserMap.put(notificationTypeId, userList);
        }
        else {
            userList.add(forumUser);
        }
        return ntUserMap;
    }

    public List<User> getSubscribedUsersByNotificationType(NotificationType notificationType) {
        logger.debug(subscribedUserMap.toString());
        List<User> userList = subscribedUserMap.get(notificationType.getId());
        if (userList == null) {
            return new ArrayList<User>();
        }
        return userList;
    }

    public List<User> getUnsubscribedUsersByNotificationType(NotificationType notificationType) {
        List<User> userList = unsubscribedUserMap.get(notificationType.getId());
        if (userList == null) {
            return new ArrayList<User>();
        }
        return userList;
    }

}
