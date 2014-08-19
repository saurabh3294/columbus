package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.processor.NotificationMessageProcessor;
import com.proptiger.data.notification.repo.NotificationMessageDao;

@Service
public class NotificationMessageService {

    @Autowired
    private NotificationMessageDao                  notificationMessageDao;

    @Autowired
    private NotificationTypeGeneratedService        ntGeneratedService;

    @Autowired
    private NotificationTypeService                 notificationTypeService;

    @Autowired
    private UserNotificationTypeSubscriptionService userNTSubscriptionService;

    private Gson                                    serializer = new Gson();

    public Integer getActiveNotificationMessageCount() {
        return notificationMessageDao
                .getNotificationMessageCountByNotificationStatus(NotificationStatus.MessageGenerated);
    }

    public List<NotificationMessage> getRawNotificationMessages(Pageable pageable) {
        List<NotificationMessage> notificationMessages = notificationMessageDao.findByStatus(
                NotificationStatus.MessageGenerated,
                pageable);
        if (notificationMessages == null) {
            return new ArrayList<NotificationMessage>();
        }

        return notificationMessages;
    }

    public Map<Integer, List<NotificationMessage>> groupNotificationMessageByuser(
            List<NotificationMessage> notificationMessageList) {
        if (notificationMessageList == null) {
            return new HashMap<Integer, List<NotificationMessage>>();
        }

        Map<Integer, List<NotificationMessage>> groupNotificationMessageMap = new HashMap<Integer, List<NotificationMessage>>();
        Integer userId = null;
        List<NotificationMessage> groupNotifcationMessage = null;
        for (NotificationMessage notificationMessage : notificationMessageList) {
            userId = notificationMessage.getForumUser().getUserId();
            groupNotifcationMessage = groupNotificationMessageMap.get(userId);

            if (groupNotificationMessageMap.get(userId) == null) {
                groupNotifcationMessage = new ArrayList<NotificationMessage>();
            }
            groupNotifcationMessage.add(notificationMessage);
            groupNotificationMessageMap.put(userId, groupNotifcationMessage);
        }

        return groupNotificationMessageMap;
    }

    public Map<String, List<NotificationMessage>> groupNotificationsByNotificationType(
            List<NotificationMessage> notificationMessageList) {
        if (notificationMessageList == null) {
            return new HashMap<String, List<NotificationMessage>>();
        }

        Map<String, List<NotificationMessage>> groupNotificationMessageMap = new HashMap<String, List<NotificationMessage>>();
        NotificationType notificationType = null;
        String notificationName = null;
        List<NotificationMessage> groupNotifcationMessage = null;
        for (NotificationMessage notificationMessage : notificationMessageList) {

            notificationType = notificationMessage.getNotificationType();
            notificationName = notificationType.getName();
            groupNotifcationMessage = groupNotificationMessageMap.get(notificationName);

            if (groupNotificationMessageMap.get(notificationName) == null) {
                groupNotifcationMessage = new ArrayList<NotificationMessage>();
            }
            groupNotifcationMessage.add(notificationMessage);
            groupNotificationMessageMap.put(notificationName, groupNotifcationMessage);
        }

        return groupNotificationMessageMap;
    }

    public NotificationMessage createNotificationMessage() {
        // TODO:
        return new NotificationMessage();
    }

    public List<NotificationMessage> getNotificationMessagesForNotificationTypeGenerated(
            NotificationTypeGenerated ntGenerated) {

        // Map of User and relevant user data
        Map<ForumUser, NotificationMessagePayload> userDataList = getUserDataMapByNotificationTypeGenerated(ntGenerated);

        List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();
        for (ForumUser forumUser : userDataList.keySet()) {
            NotificationMessagePayload payload = userDataList.get(forumUser);
            NotificationMessage nMessage = new NotificationMessage();
            nMessage.setNotificationTypeGenerated(ntGenerated);
            nMessage.setNotificationType(ntGenerated.getNotificationType());
            nMessage.setForumUser(forumUser);
            nMessage.setNotificationMessagePayload(payload);
            notificationMessages.add(nMessage);
        }

        return notificationMessages;
    }

    public Map<ForumUser, NotificationMessagePayload> getUserDataMapByNotificationTypeGenerated(
            NotificationTypeGenerated ntGenerated) {

        NotificationType notificationType = ntGenerated.getNotificationType();
        Map<ForumUser, NotificationMessagePayload> nmPayloadMap = new HashMap<ForumUser, NotificationMessagePayload>();

        NotificationMessageProcessor nmProcessor = notificationType.getNotificationTypeConfig()
                .getNotificationMessageProcessorObject();

        if (NotificationTypeUserStrategy.OnlySubscribed.equals(notificationType.getUserStrategy())) {
            List<ForumUser> userList = userNTSubscriptionService.getSubscribedUsersByNotificationType(notificationType);
            nmPayloadMap = nmProcessor.getNotificationMessagePayloadBySubscribedUserList(userList, ntGenerated);
        }
        else if (NotificationTypeUserStrategy.DefaultMinusUnsubscribed.equals(notificationType.getUserStrategy())) {
            List<ForumUser> unsubscribedUserList = userNTSubscriptionService
                    .getUnsubscribedUsersByNotificationType(notificationType);
            nmPayloadMap = nmProcessor.getNotificationMessagePayloadByUnsubscribedUserList(
                    unsubscribedUserList,
                    ntGenerated);
        }
        return nmPayloadMap;
    }

    @Transactional
    public void persistNotificationMessages(
            List<NotificationMessage> notificationMessages,
            NotificationTypeGenerated ntGenerated) {
        saveOrUpdateMessages(notificationMessages);
        ntGeneratedService.setMessageGeneratedStatusInTypeGenerated(ntGenerated);
    }

    public Iterable<NotificationMessage> saveOrUpdateMessages(Iterable<NotificationMessage> notificationMessages) {
        Iterator<NotificationMessage> iterator = notificationMessages.iterator();
        while (iterator.hasNext()) {
            populateNotificationMessageDataBeforeSave(iterator.next());
        }
        notificationMessageDao.save(notificationMessages);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        return notificationMessages;
    }

    private void populateNotificationMessageDataBeforeSave(NotificationMessage notificationMessage) {
        NotificationMessagePayload payload = notificationMessage.getNotificationMessagePayload();
        notificationMessage.setData(serializer.toJson(payload));
    }

    public void addNotificationMessageUpdateHistory(
            NotificationMessage notificationMessage,
            NotificationStatus notificationStatus) {
        NotificationMessageUpdateHistory nHistory = new NotificationMessageUpdateHistory(notificationStatus, new Date());

        notificationMessage.getNotificationMessagePayload().getNotificationMessageUpdateHistories().add(nHistory);
    }
}
