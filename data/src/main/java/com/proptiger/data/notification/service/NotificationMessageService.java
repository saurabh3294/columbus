package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.processor.NotificationMessageProcessor;
import com.proptiger.data.notification.repo.NotificationMessageDao;
import com.proptiger.data.service.ForumUserService;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationMessageService {
    private static Logger                           logger = LoggerFactory.getLogger(NotificationMessageService.class);

    @Autowired
    private NotificationMessageDao                  notificationMessageDao;

    @Autowired
    private NotificationTypeGeneratedService        ntGeneratedService;

    @Autowired
    private NotificationTypeService                 notiTypeService;

    @Autowired
    private ForumUserService                        forumUserService;

    @Autowired
    private UserNotificationTypeSubscriptionService userNTSubscriptionService;

    public Long getActiveNotificationMessageCount() {
        return notificationMessageDao
                .getNotificationMessageCountByNotificationStatus(NotificationStatus.MessageGenerated);
    }

    public List<NotificationMessage> getRawNotificationMessages(Pageable pageable) {
        List<NotificationMessage> notificationMessages = notificationMessageDao.findByNotificationStatus(
                NotificationStatus.MessageGenerated,
                pageable);

        logger.debug(Serializer.toJson(notificationMessages));

        if (notificationMessages == null) {
            return new ArrayList<NotificationMessage>();
        }

        for (NotificationMessage notificationMessage : notificationMessages) {
            populateDataOnPostLoad(notificationMessage);
        }

        return notificationMessages;
    }

    public void populateDataOnPostLoad(NotificationMessage nMessage) {
        nMessage.setNotificationMessagePayload(Serializer.fromJson(nMessage.getData(), NotificationMessagePayload.class));
        NotificationType notificationType = nMessage.getNotificationType();
        notiTypeService.populateNotificationTypeConfig(notificationType);
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
            userId = notificationMessage.getUserId();
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

    public NotificationMessage createNotificationMessage(Integer notificationTypeId, Integer userId, Object primaryKeyId) {

        NotificationType notiType = notiTypeService.findOne(notificationTypeId);

        NotificationMessage nMessage = createNewNotificationMessageObject(notiType, userId);
        NotificationMessagePayload nMessagePayload = new NotificationMessagePayload();
        NotificationTypePayload nTypePayload = notiType.getNotificationTypeConfig().getNotificationTypePayloadObject();
        nMessagePayload.setNotificationTypePayload(nTypePayload);
        nTypePayload.setPrimaryKeyValue(primaryKeyId);
        nMessage.setNotificationMessagePayload(nMessagePayload);

        return nMessage;
    }

    public NotificationMessage createNewNotificationMessageObject(NotificationType notiType, Integer userId) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setUserId(userId);
        notificationMessage.setNotificationType(notiType);
        return notificationMessage;
    }

    public List<NotificationMessage> getNotificationMessagesForNotificationTypeGenerated(
            NotificationTypeGenerated ntGenerated) {

        // Map of User and relevant user data
        Map<Integer, NotificationMessagePayload> userDataList = getUserDataMapByNotificationTypeGenerated(ntGenerated);
        logger.debug("Found " + userDataList.keySet().size()
                + " Users for NotificationTypeGenerated ID "
                + ntGenerated.getId());

        List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();
        for (Integer userId : userDataList.keySet()) {
            NotificationMessagePayload payload = userDataList.get(userId);
            NotificationMessage nMessage = createNewNotificationMessageObject(ntGenerated.getNotificationType(), userId);
            nMessage.setNotificationTypeGeneratedId(ntGenerated.getId());
            nMessage.setNotificationMessagePayload(payload);
            notificationMessages.add(nMessage);
        }

        return notificationMessages;
    }

    public Map<Integer, NotificationMessagePayload> getUserDataMapByNotificationTypeGenerated(
            NotificationTypeGenerated ntGenerated) {

        NotificationType notificationType = ntGenerated.getNotificationType();
        Map<Integer, NotificationMessagePayload> nmPayloadMap = new HashMap<Integer, NotificationMessagePayload>();

        NotificationMessageProcessor nmProcessor = notificationType.getNotificationTypeConfig()
                .getNotificationMessageProcessorObject();

        if (NotificationTypeUserStrategy.OnlySubscribed.equals(notificationType.getUserStrategy())) {
            List<ForumUser> userList = userNTSubscriptionService.getSubscribedUsersByNotificationType(notificationType);
            logger.debug("Found " + userList.size()
                    + " Subscribed users for NotificationType "
                    + notificationType.getName());
            nmPayloadMap = nmProcessor.getNotificationMessagePayloadBySubscribedUserList(userList, ntGenerated);
        }
        else if (NotificationTypeUserStrategy.MinusUnsubscribed.equals(notificationType.getUserStrategy())) {
            List<ForumUser> unsubscribedUserList = userNTSubscriptionService
                    .getUnsubscribedUsersByNotificationType(notificationType);
            logger.debug("Found " + unsubscribedUserList.size()
                    + " unsubscribed users for NotificationType "
                    + notificationType.getName());
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
        logger.debug("Saving " + notificationMessages.size() + " notificationMessages");
        saveOrUpdateMessages(notificationMessages);
        logger.debug("Setting MessageGenerated Status In TypeGenerated for NotificationTypeGenerated ID " + ntGenerated
                .getId());
        ntGeneratedService.setMessageGeneratedStatusInTypeGenerated(ntGenerated);
    }

    public List<NotificationMessage> saveOrUpdateMessages(List<NotificationMessage> notificationMessages) {
        for (NotificationMessage nMessage : notificationMessages) {
            populateNotificationMessageDataBeforeSave(nMessage);
        }
        notificationMessageDao.save(notificationMessages);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        return notificationMessages;
    }

    /**
     * Call this method for saving only when the auto increment id is need after
     * saving.
     * 
     * @param notificationMessages
     * @return
     */
    @Transactional
    public NotificationMessage saveOrFlush(NotificationMessage notificationMessage) {
        populateNotificationMessageDataBeforeSave(notificationMessage);

        NotificationMessage saveMessage = notificationMessageDao.saveAndFlush(notificationMessage);
        populateDataOnPostLoad(saveMessage);
        notificationMessageDao.flush();
        return saveMessage;
    }

    private void populateNotificationMessageDataBeforeSave(NotificationMessage notificationMessage) {
        NotificationMessagePayload payload = notificationMessage.getNotificationMessagePayload();
        notificationMessage.setData(Serializer.toJson(payload));
    }

    public void addNotificationMessageUpdateHistory(
            NotificationMessage notificationMessage,
            NotificationStatus notificationStatus) {
        NotificationMessageUpdateHistory nHistory = new NotificationMessageUpdateHistory(notificationStatus, new Date());

        notificationMessage.getNotificationMessagePayload().getNotificationMessageUpdateHistories().add(nHistory);
    }

    /**
     * inserting the new Notification Messages if it was generated previously.
     * 
     * @param nMessages
     */
    @Transactional
    public void checkAndGenerateNewMessages(List<NotificationMessage> nMessages) {
        NotificationMessage notificationMessage, savedNMessage;
        NotificationTypeGenerated nTypeGenerated = null;

        for (int i = 0; i < nMessages.size(); i++) {
            notificationMessage = nMessages.get(i);
            if (notificationMessage.getId() < 1) {
                nTypeGenerated = ntGeneratedService.createNotificationTypeGenerated(notificationMessage);
                logger.debug(" NEW TYPE GENERATED " + Serializer.toJson(nTypeGenerated));
                notificationMessage.setNotificationTypeGeneratedId(nTypeGenerated.getId());
                savedNMessage = saveOrFlush(notificationMessage);
                logger.debug(" NEW Message GENERATED " + Serializer.toJson(savedNMessage));
                logger.debug(" NEW Message GENERATED " + Serializer.toJson(savedNMessage));
                nMessages.set(i, savedNMessage);
            }
        }
    }
}
