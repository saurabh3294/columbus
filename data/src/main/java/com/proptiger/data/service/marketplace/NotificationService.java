package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.model.marketplace.NotificationType;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.rits.cloning.Cloner;

/**
 * 
 * @author azi
 * 
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private LeadTaskDao     taskDao;

    @Autowired
    private LeadOfferDao    leadOfferDao;

    @Autowired
    private LeadTaskService leadTaskService;

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

        Collections.sort(finalNotificationTypes, NotificationType.getNotificationtypereversecomparator());
        return finalNotificationTypes;
    }

    /**
     * manages task due notifications
     */
    public void manageTaskDueNotification() {
        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int taskTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        List<LeadOffer> leadOffers = leadOfferDao.getOffersWithDueLeadTasks(validStartTime, validEndTime, taskTypeId);
        for (LeadOffer leadOffer : leadOffers) {
            manageTaskDueNotificationForLeadOffer(leadOffer.getId());
        }

        notificationDao.deleteTaskNotificationNotScheduledBetween(validStartTime, validEndTime, taskTypeId);
    }

    /**
     * manages task due notification for one single lead offer... gets lock on
     * lead offer to avoid race conditions
     * 
     * @param leadOfferId
     */
    @Transactional
    public void manageTaskDueNotificationForLeadOffer(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.getLock(leadOfferId);

        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int taskTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();
        notificationDao.deleteTaskNotificationNotScheduledBetween(validStartTime, validEndTime, taskTypeId);

        LeadTask nextTask = leadOffer.getNextTask();
        if (nextTask != null) {
            Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                    nextTask.getId(),
                    com.proptiger.data.enums.NotificationType.TaskDue.getId());
            if (notification == null) {
                Date scheduledTime = nextTask.getScheduledFor();
                if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)
                        && !(leadOffer.getLastTask() == null && nextTask.getTaskStatusId() == LeadTaskService
                                .getOfferdefaultleadtaskstatusmappingid())) {
                    createTaskDueNotification(nextTask);

                    // XXX NOTIFICATION TO BE SENT HERE
                }
            }
        }
    }

    /**
     * creates notification for a task
     * 
     * @param leadTask
     * @return
     */
    private Notification createTaskDueNotification(LeadTask leadTask) {
        Notification notification = new Notification();
        notification.setNotificationTypeId(com.proptiger.data.enums.NotificationType.TaskDue.getId());
        notification.setObjectId(leadTask.getId());
        notification.setUserId(leadTask.getLeadOffer().getAgentId());

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
}