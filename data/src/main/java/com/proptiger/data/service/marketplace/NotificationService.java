package com.proptiger.data.service.marketplace;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.enums.LeadTaskName;
import com.proptiger.data.enums.NotificationType;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.MarketplaceNotificationType;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.MarketplaceNotificationTypeDao;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.util.DateUtil;
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
    private NotificationDao                notificationDao;

    @Autowired
    private MarketplaceNotificationTypeDao notificationTypeDao;

    @Autowired
    private LeadTaskDao                    taskDao;

    @Autowired
    private LeadOfferDao                   leadOfferDao;

    @Autowired
    private LeadOfferService               leadOfferService;

    @Autowired
    private LeadDao                        leadDao;

    @Autowired
    private LeadTaskService                leadTaskService;

    @Autowired
    NotificationGeneratedService           generatedService;

    private static final List<Integer>     allMasterTaskIdsButCall = new ArrayList<>();

    @Autowired
    private static Logger                  logger;

    static {
        for (LeadTaskName leadTask : LeadTaskName.values()) {
            if (!leadTask.equals(LeadTaskName.Call)) {
                allMasterTaskIdsButCall.add(leadTask.getId());
            }
        }
    }

    /**
     * 
     * @param userId
     * @return {@link List} of {@link Notification} grouped on the basis of
     *         {@link MarketplaceNotificationType} in default order
     */
    public List<MarketplaceNotificationType> getNotificationsForUser(int userId) {
        List<MarketplaceNotificationType> notificationTypes = notificationDao.getNotificationTypesForUser(userId);

        List<MarketplaceNotificationType> finalNotificationTypes = new ArrayList<>();
        for (MarketplaceNotificationType notificationType : notificationTypes) {
            for (Notification notification : notificationType.getNotifications()) {
                notification.setNotificationType(null);
            }

            if (notificationType.isGroupable()) {
                finalNotificationTypes.add(notificationType);
            }
            else {
                for (Notification notification : notificationType.getNotifications()) {
                    Cloner cloner = new Cloner();
                    MarketplaceNotificationType newNotificationType = cloner.deepClone(notificationType);

                    newNotificationType.setNotifications(Arrays.asList(notification));
                    finalNotificationTypes.add(newNotificationType);
                }
            }
        }

        finalNotificationTypes = filterReadNotifications(finalNotificationTypes);
        Collections.sort(finalNotificationTypes, MarketplaceNotificationType.getNotificationtypereversecomparator());
        return finalNotificationTypes;
    }

    /**
     * returns notification count for a user
     * 
     * @param userId
     * @return
     */
    public int getNotificationsCountForUser(int userId) {
        List<Notification> notificationTypes = notificationDao.getNotificationWithTypeForUser(userId);

        int count = 0;
        for (Notification notification : notificationTypes) {

            if (!(notification.getNotificationType().isIgnorable() && notification.isRead())) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     * filters out read notifications
     * 
     * @param notificationTypes
     * @return
     */
    private List<MarketplaceNotificationType> filterReadNotifications(
            List<MarketplaceNotificationType> notificationTypes) {
        List<MarketplaceNotificationType> finalNotificationTypes = new ArrayList<>();
        for (MarketplaceNotificationType notificationType : notificationTypes) {
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

        Integer nextTaskId = leadOffer.getNextTaskId();
        if (nextTaskId != null) {
            LeadTask nextTask = leadTaskService.getLeadTask(nextTaskId);
            Date scheduledTime = nextTask.getScheduledFor();
            if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)) {
                createNotificationForTask(notificationTypeId, nextTask, false);
                validTaskIdForNotification = nextTask.getId();
            }
        }
        deleteInvalidNotificationForLeadOffer(leadOfferId, validTaskIdForNotification, notificationTypeId);
    }

    /**
     * manages task due notifications
     */
    public void manageCallDueNotification() {
        Date validStartTime = new Date();
        Date validEndTime = getCallDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        List<LeadOffer> leadOffers = leadOfferDao.getOffersWithTaskScheduledBetweenAndWithoutNotification(
                validStartTime,
                validEndTime,
                notificationTypeId,
                Arrays.asList(LeadTaskName.Call.getId()));
        for (LeadOffer leadOffer : leadOffers) {
            manageCallDueNotificationForLeadOffer(leadOffer.getId());
        }
        notificationDao.deleteTaskNotificationNotScheduledBetween(
                validStartTime,
                validEndTime,
                notificationTypeId,
                Arrays.asList(LeadTaskName.Call.getId()));
    }

    /**
     * manages task due notification for one single lead offer... gets lock on
     * lead offer to avoid race conditions
     * 
     * @param leadOfferId
     */
    @Transactional
    private void manageCallDueNotificationForLeadOffer(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.getLock(leadOfferId);

        Date validStartTime = new Date();
        Date validEndTime = getCallDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        int validTaskIdForNotification = 0;

        LeadTask nextTask = leadOffer.getNextTask();
        if (nextTask != null) {
            Date scheduledTime = nextTask.getScheduledFor();
            if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)
                    && !(leadOffer.getLastTask() == null && nextTask.getTaskStatusId() == LeadTaskService
                            .getOfferdefaultleadtaskstatusmappingid())) {
                createNotificationForTask(notificationTypeId, nextTask, true);
                validTaskIdForNotification = nextTask.getId();
            }
        }
        deleteInvalidNotificationForLeadOffer(leadOfferId, validTaskIdForNotification, notificationTypeId);
    }

    /**
     * 
     * 
     * @param notificationTypeId
     * @param nextTask
     * @param sendNotification
     * @return
     */
    private Notification createNotificationForTask(int notificationTypeId, LeadTask nextTask, boolean sendNotification) {
        Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                nextTask.getId(),
                notificationTypeId);
        if (notification == null) {
            createTaskNotification(nextTask, notificationTypeId);
            if (sendNotification) {
                // XXX NOTIFICATION TO BE SENT HERE
            }
        }

        LeadTask toBePersisted = leadTaskService.getTaskDetails(nextTask.getId());
        toBePersisted.unlinkCircularLoop();
        return createNotification(
                toBePersisted.getLeadOffer().getAgentId(),
                notificationTypeId,
                toBePersisted.getId(),
                SerializationUtils.objectToJson(toBePersisted));
    }

    /**
     * manages task overdue notifications
     */
    public void populateTaskDueNotification() {
        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        List<LeadOffer> leadOffers = leadOfferDao.getOffersWithTaskScheduledBetweenAndWithoutNotification(
                validStartTime,
                validEndTime,
                notificationTypeId,
                allMasterTaskIdsButCall);
        for (LeadOffer leadOffer : leadOffers) {
            populateTaskDueNotificationForLeadOffer(leadOffer.getId());
        }
        notificationDao.deleteTaskNotificationNotScheduledBetween(
                validStartTime,
                validEndTime,
                notificationTypeId,
                allMasterTaskIdsButCall);
    }

    /**
     * manages task due notification for one single lead offer... gets lock on
     * lead offer to avoid race conditions
     * 
     * @param leadOfferId
     */
    @Transactional
    private void populateTaskDueNotificationForLeadOffer(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.getLock(leadOfferId);

        Date validStartTime = new Date();
        Date validEndTime = getTaskDueEndScheduledTime();
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();

        int validTaskIdForNotification = 0;

        LeadTask nextTask = leadOffer.getNextTask();
        if (nextTask != null) {
            Date scheduledTime = nextTask.getScheduledFor();
            if (scheduledTime.after(validStartTime) && scheduledTime.before(validEndTime)) {
                createNotificationForTask(notificationTypeId, nextTask, false);
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
    private Notification createTaskNotification(LeadTask leadTask, int notificationTypeId) {
        // XXX should be removed from here... need to figure out why offer
        // object is not being set automatically
        leadTask.setLeadOffer(leadOfferDao.findOne(leadTask.getLeadOfferId()));

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
     * 
     * @param lead
     * @param notificationTypeId
     * @return
     */

    public Notification createLeadNotification(Lead lead, int notificationTypeId) {
        Notification notification = new Notification();
        notification.setNotificationTypeId(notificationTypeId);
        notification.setObjectId(lead.getId());

        Gson gson = new Gson();
        notification.setStringDetails(gson.toJson(lead).toString());
        Notification notificationPreMature = notification;
        List<LeadOffer> leadOffers = leadOfferDao.findByLeadId(lead.getId());

        for (LeadOffer leadOffer : leadOffers) {
            Cloner cloner = new Cloner();
            Notification notificationOriginal = cloner.deepClone(notificationPreMature);
            notificationOriginal.setUserId(leadOffer.getAgentId());
            notificationDao.save(notificationOriginal);

        }
        return notification;
    }

    /**
     * gets the time upto which task must be scheduled in order for the client
     * to get notified
     * 
     * @return
     */
    private Date getCallDueEndScheduledTime() {
        return new Date(new Date().getTime() + 1000
                * (PropertyReader.getRequiredPropertyAsType(
                        PropertyKeys.MARKETPLACE_CALL_DUE_NOTIFICATION_DURATION,
                        Integer.class)));
    }

    /**
     * gets the time upto which task must be scheduled in order for the client
     * to get notified
     * 
     * @return
     */
    private Date getTaskDueEndScheduledTime() {
        DateTime finalDateTime;
        DateTime dateTime = new DateTime();

        DateTime beginningOfDay = dateTime.withTimeAtStartOfDay();
        DateTime mark1 = beginningOfDay.plusSeconds(PropertyReader.getRequiredPropertyAsType(
                PropertyKeys.MARKETPLACE_TASK_DUE_NOTIFICATION_TIME1,
                Integer.class));
        DateTime mark2 = beginningOfDay.plusSeconds(PropertyReader.getRequiredPropertyAsType(
                PropertyKeys.MARKETPLACE_TASK_DUE_NOTIFICATION_TIME2,
                Integer.class));

        if (dateTime.isBefore(mark1)) {
            finalDateTime = mark2.minusDays(1).plusSeconds(
                    PropertyReader.getRequiredPropertyAsType(
                            PropertyKeys.MARKETPLACE_TASK_DUE_NOTIFICATION_DURATION2,
                            Integer.class));
        }
        else if (dateTime.isBefore(mark2)) {
            finalDateTime = mark1.plusSeconds(PropertyReader.getRequiredPropertyAsType(
                    PropertyKeys.MARKETPLACE_TASK_DUE_NOTIFICATION_DURATION1,
                    Integer.class));
        }
        else {
            finalDateTime = mark2.plusSeconds(PropertyReader.getRequiredPropertyAsType(
                    PropertyKeys.MARKETPLACE_TASK_DUE_NOTIFICATION_DURATION2,
                    Integer.class));
        }

        return new Date(finalDateTime.getMillis());
    }

    public void manageTaskNotificationForLeadOffer(int leadOfferId) {
        manageCallDueNotificationForLeadOffer(leadOfferId);
        populateTaskDueNotificationForLeadOffer(leadOfferId);
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
        sendTaskNotificationToUsers(notificationTypeId, notifications);
    }

    /**
     * method to send task overdue notification to user
     */
    public void sendTaskDueNotification() {
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();
        List<Notification> notifications = notificationDao.findByNotificationTypeIdAndMasterTaskIdIn(
                notificationTypeId,
                allMasterTaskIdsButCall);
        sendTaskNotificationToUsers(notificationTypeId, notifications);
    }

    private void sendTaskNotificationToUsers(int notificationTypeId, List<Notification> notifications) {
        Map<Integer, List<Notification>> map = groupNotificationsByUser(notifications);
        MarketplaceNotificationType notificationType = notificationTypeDao.findOne(notificationTypeId);
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

    /**
     * creates and sends lead offer notification to brokers
     * 
     * @param offer
     * @return
     */
    public Notification sendLeadOfferNotification(int offerId) {
        LeadOffer offer = leadOfferDao.getLeadOfferWithRequirements(offerId);
        Notification notification = createNotification(
                offer.getAgentId(),
                NotificationType.LeadOffered.getId(),
                offer.getId(),
                SerializationUtils.objectToJson(offer));
        // XXX send notification to broker
        return notification;
    }

    @Transactional
    public void manageLeadOfferedNotificationDeletionForLead(int leadId) {
        Lead lead = leadDao.getLock(leadId);
        List<LeadOffer> offers = lead.getLeadOffers();

        Date endDate = getNoBrokerClaimedCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader
                        .getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_NO_BROKER_CLAIMED_CRON_BUFFER)) * 1000);

        int offeredStatusId = LeadOfferStatus.Offered.getLeadOfferStatusId();
        Date maxOfferDate = new Date(0);
        boolean claimed = false;
        for (LeadOffer offer : offers) {
            if (offer.getStatusId() != offeredStatusId) {
                claimed = true;
            }
            maxOfferDate = DateUtil.max(maxOfferDate, offer.getCreatedAt());
        }

        if (claimed || (maxOfferDate.after(startDate) && maxOfferDate.before(endDate))) {
            if (!claimed) {
                leadOfferService.expireLeadOffers(offers);
                generatedService
                        .createNotificationGenerated(
                                Arrays.asList(new NotificationMessage(
                                        getRelationshipManagerUserId(),
                                        NotificationType.NoBrokerClaimed.getEmailSubject(),
                                        "Lead ID: " + leadId
                                                + " of resale marketplace was not claimed by any broker. Marking all offers as expired.")),
                                Arrays.asList(MediumType.Email));
                createNotification(
                        getRelationshipManagerUserId(),
                        NotificationType.NoBrokerClaimed.getId(),
                        leadId,
                        null);
            }
            deleteLeadOfferNotificationForLead(offers);
        }
    }

    /**
     * deletes offerred notification for a list of offers
     * 
     * @param offers
     */
    private void deleteLeadOfferNotificationForLead(List<LeadOffer> offers) {
        for (LeadOffer offer : offers) {
            Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                    offer.getId(),
                    NotificationType.LeadOffered.getId());
            if (notification != null) {
                notificationDao.delete(notification);
            }
        }
    }

    private int getRelationshipManagerUserId() {
        return PropertyReader.getRequiredPropertyAsType(
                PropertyKeys.MARKETPLACE_RELATIONSHIP_MANAGER_USER_ID,
                Integer.class);
    }

    public Date getNoBrokerClaimedCutoffTime() {
        return DateUtil
                .getWorkingTimeSubtractedFromDate(
                        new Date(),
                        PropertyReader.getRequiredPropertyAsType(
                                PropertyKeys.MARKETPLACE_BIDDING_CYCLE_DURATION,
                                Integer.class) + PropertyReader.getRequiredPropertyAsType(
                                PropertyKeys.MARKETPLACE_POST_BIDDING_OFFER_DURATION,
                                Integer.class));
    }

    public Notification manageDealClosedNotification(int leadOfferId) {
        generatedService.createNotificationGenerated(Arrays.asList(new NotificationMessage(
                getRelationshipManagerUserId(),
                NotificationType.SaleSuccessful.getEmailSubject(),
                "Lead OfferID: " + leadOfferId + " of resale marketplace is marked as closed won.")), Arrays
                .asList(MediumType.Email));
        return createNotification(
                getRelationshipManagerUserId(),
                NotificationType.SaleSuccessful.getId(),
                leadOfferId,
                null);
    }
}