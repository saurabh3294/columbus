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

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.repo.NotificationMessageDao;
import com.proptiger.data.service.ForumUserService;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationMessageService {

    @Autowired
    private NotificationMessageDao           notificationMessageDao;

    @Autowired
    private NotificationTypeGeneratedService ntGeneratedService;
    
    @Autowired
    private NotificationTypeService notiTypeService;
    
    @Autowired
    private ForumUserService forumUserService;

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
        
        for(NotificationMessage notificationMessage: notificationMessages){
            populateDataOnPostLoad(notificationMessage);
        }
        
        return notificationMessages;
    }

    public void populateDataOnPostLoad(NotificationMessage nMessage){
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

    public NotificationMessage createNotificationMessage(Integer notificationTypeId, Integer userId) {
        NotificationType notiType = notiTypeService.findOne(notificationTypeId);
        ForumUser forumUser = forumUserService.findOne(userId);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setForumUser(forumUser);
        notificationMessage.setNotificationType(notiType);
        
        return notificationMessage;
    }

    public List<NotificationMessage> getNotificationMessagesForNotificationType(NotificationType notificationType) {
        // TODO:
        // get user list based on notification type
        // get msg by type gen from msgService
        // add data to msg by notification type
        return null;
    }

    /*
     * TODO: Make it Transactional
     */
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
    
    /**
     * Call this method for saving only when the auto increment id is need after saving.
     * @param notificationMessages
     * @return
     */
    public NotificationMessage saveOrFlush(NotificationMessage notificationMessage){
        populateNotificationMessageDataBeforeSave(notificationMessage);
        
        return notificationMessageDao.saveAndFlush(notificationMessage);
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
     * @param nMessages
     */
    public void checkAndGenerateNewMessages(List<NotificationMessage> nMessages){
        NotificationMessage notificationMessage, savedNMessage;
        
        for(int i=0; i<nMessages.size(); i++){
           notificationMessage = nMessages.get(i);
           if(notificationMessage.getId() < 1){
               savedNMessage = saveOrFlush(notificationMessage);
               nMessages.set(i, savedNMessage);
           }
        }
    }
}
