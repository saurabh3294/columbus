package com.proptiger.data.service.marketplace;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.model.marketplace.NotificationType;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.proptiger.data.repo.marketplace.NotificationTypeDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.SerializationUtils;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.UnauthorizedException;
import com.rits.cloning.Cloner;

/**
 * 
 * @author azi
 * 
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationDao     notificationDao;

    @Autowired
    private NotificationTypeDao notificationTypeDao;

    @Autowired
    private LeadTaskDao         taskDao;

    @Autowired
    private LeadOfferDao        leadOfferDao;

    @Autowired
    private LeadTaskService     leadTaskService;

    /**
     * 
     * @param userId
     * @return {@link List} of {@link Notification} grouped on the basis of
     *         {@link NotificationType} in default order
     */
    public List<NotificationType> getNotificationsForUser(int userId) {
        List<NotificationType> notificationTypes = notificationDao.getNotificationTypesForUser(userId);

        List<NotificationType> finalNotificationTypes = new ArrayList<>();
        for (NotificationType notificationType : notificationTypes) {
            for (Notification notification : notificationType.getNotifications()) {
                notification.setNotificationType(null);
            }

            if (notificationType.isGroupable()) {
                finalNotificationTypes.add(notificationType);
            }
            else {
                for (Notification notification : notificationType.getNotifications()) {
                    Cloner cloner = new Cloner();
                    NotificationType newNotificationType = cloner.deepClone(notificationType);

                    newNotificationType.setNotifications(Arrays.asList(notification));
                    finalNotificationTypes.add(newNotificationType);
                }
            }
        }

        finalNotificationTypes = filterReadNotifications(finalNotificationTypes);
        Collections.sort(finalNotificationTypes, NotificationType.getNotificationtypereversecomparator());
        return finalNotificationTypes;
    }

    /**
     * filters out read notifications
     * 
     * @param notificationTypes
     * @return
     */
    private List<NotificationType> filterReadNotifications(List<NotificationType> notificationTypes) {
        List<NotificationType> finalNotificationTypes = new ArrayList<>();
        for (NotificationType notificationType : notificationTypes) {
            if (notificationType.isIgnorable()) {
                boolean read = true;
                for (Notification notification : notificationType.getNotifications()) {
                    read = read && notification.isRead();
                }
                if (!read) {
                    finalNotificationTypes.add(notificationType);
                }
            }
            else {
                finalNotificationTypes.add(notificationType);
            }
        }
        return finalNotificationTypes;
    }

    /**
     * manages task overdue notifications
     */
    public void populateTaskOverDueNotification() {
        Date validStartTime = new Date(0);
        Date validEndTime = new Date();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskOverDue.getId();

        List<LeadOffer> leadOffers = leadOfferDao.getOffersWithTaskScheduledBetweenAndWithoutNotification(
                validStartTime,
                validEndTime,
                notificationTypeId);
        for (LeadOffer leadOffer : leadOffers) {
            populateTaskOverDueNotificationForLeadOffer(leadOffer.getId());
        }
    }

    /**
     * manages task due notification for one single lead offer... gets lock on
     * lead offer to avoid race conditions
     * 
     * @param leadOfferId
     */
    @Transactional
    private void populateTaskOverDueNotificationForLeadOffer(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.getLock(leadOfferId);

        Date validStartTime = new Date(0);
        Date validEndTime = new Date();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskOverDue.getId();

        int validTaskIdForNotification = 0;

        LeadTask nextTask = leadOffer.getNextTask();
        if (nextTask != null) {
            Date scheduledTime = nextTask.getScheduledFor();
            if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)) {
                Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                        nextTask.getId(),
                        notificationTypeId);
                if (notification == null) {
                    createTaskNotification(nextTask, notificationTypeId);
                }
                validTaskIdForNotification = nextTask.getId();
            }
        }
        deleteInvalidNotificationForLeadOffer(leadOfferId, validTaskIdForNotification, notificationTypeId);
    }

    /**
     * manages task due notifications
     */
    public void manageTaskDueNotification() {
        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        List<LeadOffer> leadOffers = leadOfferDao.getOffersWithTaskScheduledBetweenAndWithoutNotification(
                validStartTime,
                validEndTime,
                notificationTypeId);
        for (LeadOffer leadOffer : leadOffers) {
            manageTaskDueNotificationForLeadOffer(leadOffer.getId());
        }
        notificationDao.deleteTaskNotificationNotScheduledBetween(validStartTime, validEndTime, notificationTypeId);
    }

    /**
     * manages task due notification for one single lead offer... gets lock on
     * lead offer to avoid race conditions
     * 
     * @param leadOfferId
     */
    @Transactional
    private void manageTaskDueNotificationForLeadOffer(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.getLock(leadOfferId);

        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        int validTaskIdForNotification = 0;

        LeadTask nextTask = leadOffer.getNextTask();
        if (nextTask != null) {
            Date scheduledTime = nextTask.getScheduledFor();
            if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)
                    && !(leadOffer.getLastTask() == null && nextTask.getTaskStatusId() == LeadTaskService
                            .getOfferdefaultleadtaskstatusmappingid())) {
                Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                        nextTask.getId(),
                        notificationTypeId);
                if (notification == null) {
                    createTaskNotification(nextTask, notificationTypeId);
                    // XXX NOTIFICATION TO BE SENT HERE
                }
                validTaskIdForNotification = nextTask.getId();
            }
        }

        deleteInvalidNotificationForLeadOffer(leadOfferId, validTaskIdForNotification, notificationTypeId);
    }

    /**
     * creates notification for a task
     * 
     * @param leadTask
     * @param notificationTypeId
     * @return
     */
    @Transactional
    private Notification createTaskNotification(LeadTask leadTask, int notificationTypeId) {
        leadTask = leadTaskService.getTaskDetails(leadTask.getId());
        leadTask.unlinkCircularLoop();
        return createNotification(
                leadTask.getLeadOffer().getAgentId(),
                notificationTypeId,
                leadTask.getId(),
                SerializationUtils.objectToJson(leadTask));
    }

    /**
     * creates notification object in database
     * 
     * @param userId
     * @param notificationTypeId
     * @param objectId
     * @param details
     * @return
     */
    private Notification createNotification(int userId, int notificationTypeId, int objectId, JsonNode details) {
        Notification notification = new Notification();
        notification.setNotificationTypeId(notificationTypeId);
        notification.setObjectId(objectId);
        notification.setUserId(userId);
        notification.setDetails(details);

        notification = notificationDao.save(notification);
        return notification;
    }

    /**
     * gets the time upto which task must be scheduled in order for the client
     * to get notified
     * 
     * @return
     */
    private Date getTaskDueEndScheduledTime() {
        return new Date(new Date().getTime() + 1000
                * (PropertyReader.getRequiredPropertyAsType(
                        PropertyKeys.MARKETPLACE_DUE_TASK_NOTIFICATION_DURATION,
                        Integer.class)));
    }

    public void manageTaskNotificationForLeadOffer(int leadOfferId) {
        manageTaskDueNotificationForLeadOffer(leadOfferId);
        populateTaskOverDueNotificationForLeadOffer(leadOfferId);
    }

    /**
     * deletes invalid notification of a notification type for a lead offer
     * 
     * @param leadOfferId
     * @param validTaskIdForNotification
     * @param notificationTypeId
     */
    private void deleteInvalidNotificationForLeadOffer(
            int leadOfferId,
            int validTaskIdForNotification,
            int notificationTypeId) {
        List<Notification> notifications = notificationDao.getInvalidTaskNotificationForLeadOffer(
                leadOfferId,
                validTaskIdForNotification,
                notificationTypeId);
        notificationDao.delete(notifications);
    }

    /**
     * updates notification for a user
     * 
     * @param userId
     * @param notifications
     * @return
     */
    @Transactional
    public List<Notification> updateNotificationsForUser(int userId, List<Notification> notifications) {
        List<Notification> savedNotifications = notificationDao
                .findByIdIn(getNotificationIdsFromNotifications(notifications));
        Map<Integer, Notification> mappedNotification = getMappedNotifications(notifications);

        for (Notification notification : savedNotifications) {
            if (notification.getUserId() != userId) {
                throw new UnauthorizedException();
            }
            else if (!notification.getNotificationType().isIgnorable()) {
                throw new BadRequestException("Notification id " + notification.getId() + " can't be dismissed.");
            }
            else {
                ExclusionAwareBeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
                try {
                    beanUtilsBean.copyProperties(notification, mappedNotification.get(notification.getId()));
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ProAPIException("Error in copying notification", e);
                }
                notification = notificationDao.save(notification);
            }
        }
        return savedNotifications;
    }

    private List<Integer> getNotificationIdsFromNotifications(List<Notification> notifications) {
        List<Integer> notificationIds = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationIds.add(notification.getId());
        }
        return notificationIds;
    }

    private Map<Integer, Notification> getMappedNotifications(List<Notification> notifications) {
        Map<Integer, Notification> map = new HashMap<>();
        for (Notification notification : notifications) {
            map.put(notification.getId(), notification);
        }
        return map;
    }

    /**
     * method to send task overdue notification to user
     */
    public void sendTaskOverDueNotification() {
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskOverDue.getId();
        List<Notification> notifications = notificationDao.findByNotificationTypeId(notificationTypeId);
        Map<Integer, List<Notification>> map = groupNotificationsByUser(notifications);
        NotificationType notificationType = notificationTypeDao.findOne(notificationTypeId);
        for (Integer userId : map.keySet()) {
            notificationType.setNotifications(map.get(userId));
            JsonNode message = SerializationUtils.objectToJson(notificationType);

            System.out.println("SENDING NOTIFICATION TO USERID: " + userId + " MESSAGE: " + message);
            // Send Notification To User
        }
    }

    /**
     * 
     * @param notifications
     * @return
     */
    private Map<Integer, List<Notification>> groupNotificationsByUser(List<Notification> notifications) {
        Map<Integer, List<Notification>> map = new HashMap<>();
        for (Notification notification : notifications) {
            int userId = notification.getUserId();
            if (!map.containsKey(userId)) {
                map.put(userId, new ArrayList<Notification>());
            }
            map.get(userId).add(notification);
        }
        return map;
    }
}