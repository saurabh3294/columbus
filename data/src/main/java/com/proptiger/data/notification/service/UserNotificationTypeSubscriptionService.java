package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.SubscriptionType;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.UserNotificationTypeSubscription;
import com.proptiger.data.notification.repo.UserNotificationTypeSubscriptionDao;

@Service
public class UserNotificationTypeSubscriptionService {

    @Autowired
    private UserNotificationTypeSubscriptionDao  userNTSubscriptionDao;

    // Map of NotificationTypeId to subscribed users
    private static Map<Integer, List<ForumUser>> subscribedUserMap;

    // Map of NotificationTypeId to unsubscribed users
    private static Map<Integer, List<ForumUser>> unsubscribedUserMap;

    @PostConstruct
    private void constuctMappingFromDB() {
        Iterable<UserNotificationTypeSubscription> userNTSubscriptionList = userNTSubscriptionDao.findAll();
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

    private Map<Integer, List<ForumUser>> addToNotificationTypeUserMap(
            UserNotificationTypeSubscription userNTSubscription,
            Map<Integer, List<ForumUser>> ntUserMap) {

        Integer notificationTypeId = userNTSubscription.getNotificationTypeId();
        ForumUser forumUser = userNTSubscription.getForumUser();

        List<ForumUser> userList = ntUserMap.get(notificationTypeId);

        if (userList == null) {
            userList = new ArrayList<ForumUser>();
            userList.add(forumUser);
            ntUserMap.put(notificationTypeId, userList);
        }
        else {
            userList.add(forumUser);
        }
        return ntUserMap;
    }

    public List<ForumUser> getSubscribedUsersByNotificationType(NotificationType notificationType) {
        List<ForumUser> userList = subscribedUserMap.get(notificationType.getId());
        if (userList == null) {
            return new ArrayList<ForumUser>();
        }
        return userList;
    }

    public List<ForumUser> getUnsubscribedUsersByNotificationType(NotificationType notificationType) {
        List<ForumUser> userList = unsubscribedUserMap.get(notificationType.getId());
        if (userList == null) {
            return new ArrayList<ForumUser>();
        }
        return userList;
    }

}
