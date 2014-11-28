package com.proptiger.data.service.marketplace;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.exception.UnauthorizedException;
import com.proptiger.core.model.user.User;
import com.proptiger.core.util.DateUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.dto.external.marketplace.GcmMessage;
import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.enums.LeadTaskName;
import com.proptiger.data.enums.NotificationType;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.LeadTask.AgentOverDueTaskCount;
import com.proptiger.data.model.marketplace.MarketplaceNotificationType;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.model.external.NotificationCreatorServiceRequest;
import com.proptiger.data.notification.service.external.NotificationCreatorService;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadTaskDao;
import com.proptiger.data.repo.marketplace.MarketplaceNotificationTypeDao;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.proptiger.data.service.companyuser.CompanyUserService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.SerializationUtils;
import com.rits.cloning.Cloner;

/**
 * 
 * @author azi
 * 
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationDao                   notificationDao;

    @Autowired
    private MarketplaceNotificationTypeDao    notificationTypeDao;

    @Autowired
    private LeadTaskDao                       taskDao;

    @Autowired
    private LeadOfferDao                      leadOfferDao;

    @Autowired
    private LeadDao                           leadDao;

    @Autowired
    NotificationCreatorService                notificationCreatorService;

    public static final List<Integer>         allMasterTaskIdsButCall = new ArrayList<>();

    private static final NotificationTypeEnum defaultNotificationType = NotificationTypeEnum.MarketplaceDefault;

    @Autowired
    UserService                               userService;

    @Autowired
    LeadTaskStatusDao                         leadTaskStatusDao;

    @Autowired
    MailSender                                mailSender;

    private static Logger                     logger                  = LoggerFactory
                                                                              .getLogger(NotificationService.class);
    @Autowired
    CompanyUserService                        companyUserService;

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
        return getFilteredAndOrderedNotificationTypes(notificationTypes);
    }

    /**
     * 
     * @param userId
     *            notificationTypeId
     * @return {@link List} of {@link Notification} grouped on the basis of
     *         {@link MarketplaceNotificationType} in default order
     */
    public List<MarketplaceNotificationType> getNotificationsForUser(int userId, Integer notificationTypeId) {
        List<MarketplaceNotificationType> notificationTypes;
        if (notificationTypeId == null) {
            notificationTypes = notificationDao.getNotificationTypesForUser(userId);
        }
        else {
            notificationTypes = notificationDao.getNotificationTypesForUser(userId, notificationTypeId);
        }
        return getFilteredAndOrderedNotificationTypes(notificationTypes);
    }

    private List<MarketplaceNotificationType> getFilteredAndOrderedNotificationTypes(
            List<MarketplaceNotificationType> notificationTypes) {
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
        List<MarketplaceNotificationType> notificationTypes = getNotificationsForUser(userId);

        int count = 0;
        for (MarketplaceNotificationType notificationType : notificationTypes) {
            count = count + notificationType.getNotifications().size();
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
     * 
     * 
     * @param notificationTypeId
     * @param nextTask
     * @param sendNotification
     * @return
     */
    public Notification createNotificationForTask(int notificationTypeId, LeadTask nextTask) {
        Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                nextTask.getId(),
                notificationTypeId);
        if (notification == null) {
            notification = createTaskNotification(nextTask, notificationTypeId);
        }
        return notification;
    }

    public void sendCallDueNotification(Notification notification) {
        sendTaskDueNotificationToUser(Arrays.asList(notification));
    }

    /**
     * creates notification for a task
     * 
     * @param leadTask
     * @param notificationTypeId
     * @return
     */
    private Notification createTaskNotification(LeadTask leadTask, int notificationTypeId) {
        LeadOffer offer = leadOfferDao.findOne(leadTask.getLeadOfferId());

        return createNotification(
                offer.getAgentId(),
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
    public Notification createNotification(int userId, int notificationTypeId, int objectId, JsonNode details) {
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
    public void createLeadNotification(Lead lead, int notificationTypeId) {
        List<LeadOffer> leadOffers = leadOfferDao.getByLeadIdAndOpenFlagAndClaimedFlag(lead.getId(), true, true);
        if (leadOffers != null && !leadOffers.isEmpty()) {
            List<Integer> leadOfferIds = new ArrayList<>();
            for (LeadOffer leadOffer : leadOffers) {
                leadOfferIds.add(leadOffer.getId());
            }

            List<Notification> notifications = notificationDao.findByObjectIdInAndNotificationTypeIdAndReadFalse(
                    leadOfferIds,
                    notificationTypeId);

            Map<Integer, Notification> unreadNotifications = new HashMap<>();

            if (notifications != null) {
                for (Notification unreadNotification : notifications) {
                    unreadNotifications.put(unreadNotification.getUserId(), unreadNotification);
                }
            }

            for (LeadOffer leadOffer : leadOffers) {
                if (!unreadNotifications.containsKey(leadOffer.getAgentId())) {
                    Notification notification = new Notification();
                    notification.setNotificationTypeId(notificationTypeId);
                    notification.setUserId(leadOffer.getAgentId());
                    notification.setObjectId(leadOffer.getId());
                    notification.setDetails(SerializationUtils.objectToJson(lead));
                    notificationDao.save(notification);
                }
                else {
                    Notification notification = unreadNotifications.get(leadOffer.getAgentId());
                    notification.setDetails(SerializationUtils.objectToJson(lead));
                    notificationDao.save(notification);
                }
                sendDuplicateLeadNotification(leadOffer.getId());
            }
        }
    }

    private void sendDuplicateLeadNotification(int leadOfferId) {
        LeadOffer offer = leadOfferDao.getById(leadOfferId);
        User user = userService.getUserById(offer.getLead().getClientId());
        String message = user.getFullName() + ", "
                + offer.getId()
                + " has submitted another request. The new information is updated in current lead.";
        String titleMessage = "New Enquiry from Client";

        GcmMessage gcmMessage = new GcmMessage();
        gcmMessage.setData(Arrays.asList(leadOfferId));
        gcmMessage.setMessage(message);
        gcmMessage.setTitleMessage(titleMessage);
        gcmMessage.setNotificationTypeId(NotificationType.DuplicateLead.getId());
        sendGcmMessageUsingService(gcmMessage, offer.getAgentId());
    }

    public void sendLimitReachedGCMNotifications() {
        List<Notification> notifications = notificationDao.findByUserIdAndObjectId(
                NotificationType.MaxLeadCountForBrokerReached.getId(),
                0);

        for (Notification notification : notifications) {
            sendLimitReachedGCMNotification(notification.getUserId());
        }
    }

    private void sendLimitReachedGCMNotification(int userId) {
        String message = "Claim Lead suspended. Please update your existing New leads to claim new leads";
        String titleMessage = "Claim Lead suspended";

        GcmMessage gcmMessage = new GcmMessage();
        gcmMessage.setData(null);
        gcmMessage.setMessage(message);
        gcmMessage.setTitleMessage(titleMessage);
        gcmMessage.setNotificationTypeId(NotificationType.MaxLeadCountForBrokerReached.getId());
        sendGcmMessageUsingService(gcmMessage, userId);
    }

    /**
     * gets the time upto which task must be scheduled in order for the client
     * to get notified
     * 
     * @return
     */
    public Date getCallDueEndScheduledTime() {
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
    public Date getTaskDueEndScheduledTime() {
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

    /**
     * deletes invalid notification of a notification type for a lead offer
     * 
     * @param leadOfferId
     * @param validTaskIdForNotification
     * @param notificationTypeId
     */
    public void deleteInvalidNotificationForLeadOffer(
            int leadOfferId,
            int validTaskIdForNotification,
            int notificationTypeId,
            List<Integer> masterTaskIds) {
        List<Notification> notifications = notificationDao.getInvalidTaskNotificationForLeadOffer(
                leadOfferId,
                validTaskIdForNotification,
                notificationTypeId,
                masterTaskIds);
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
        for (Integer userId : map.keySet()) {
            List<Notification> userNotifications = map.get(userId);
            String message = getTaskOverDueNotificationMessage(userNotifications);

            GcmMessage gcmMessage = new GcmMessage();
            gcmMessage.setNotificationTypeId(notificationTypeId);
            gcmMessage.setMessage(message);
            gcmMessage.setData(getLeadOfferIdsFromTaskIds(getObjectIdsFromNotifications(userNotifications)));
            int size = userNotifications.size();
            if (size == 1) {
                gcmMessage.setTitleMessage("Task Overdue");
            }
            else {
                gcmMessage.setTitleMessage(size + " Tasks Overdue");
            }

            sendGcmMessageUsingService(gcmMessage, userId);
        }
    }

    /**
     * method to send task overdue notification to user
     */
    public void sendTaskDueNotification() {
        int notificationTypeId = com.proptiger.data.enums.NotificationType.TaskDue.getId();
        List<Notification> notifications = notificationDao.findByNotificationTypeIdAndMasterTaskIdIn(
                notificationTypeId,
                allMasterTaskIdsButCall);
        Map<Integer, List<Notification>> map = groupNotificationsByUser(notifications);
        for (Integer userId : map.keySet()) {
            sendTaskDueNotificationToUser(map.get(userId));
            ;
        }
    }

    private void sendTaskDueNotificationToUser(List<Notification> userNotifications) {
        int size = userNotifications.size();
        if (size > 0) {
            String message = getTaskDueNotificationMessage(userNotifications);

            GcmMessage gcmMessage = new GcmMessage();
            gcmMessage.setNotificationTypeId(com.proptiger.data.enums.NotificationType.TaskDue.getId());
            gcmMessage.setMessage(message);
            gcmMessage.setData(getLeadOfferIdsFromTaskIds(getObjectIdsFromNotifications(userNotifications)));
            if (size == 1) {
                gcmMessage.setTitleMessage("Task Due");
            }
            else {
                gcmMessage.setTitleMessage(size + " Tasks Due");
            }
            sendGcmMessageUsingService(gcmMessage, userNotifications.get(0).getUserId());
        }
    }

    private List<Integer> getLeadOfferIdsFromTaskIds(List<Integer> taskIds) {
        List<LeadTask> tasks = taskDao.findByIdInWithResultingStatusAndMasterLeadTaskAndMasterLeadTaskStatusAndStatusReasonOrderByPerformedAtDesc(taskIds);
        List<Integer> leadOfferIds = new ArrayList<>();
        for (LeadTask leadTask : tasks) {
            leadOfferIds.add(leadTask.getLeadOfferId());
        }
        return leadOfferIds;
    }

    private String getTaskDueNotificationMessage(List<Notification> notifications) {
        List<Integer> taskIds = new ArrayList<>();
        for (Notification notification : notifications) {
            taskIds.add(notification.getObjectId());
        }
        List<LeadTask> tasks = taskDao.findByIdInWithLead(taskIds);
        String message = "";

        if (tasks.size() == 1) {
            LeadTask task = tasks.get(0);
            int userId = leadOfferDao.getById(task.getLeadOfferId()).getLead().getClientId();
            User user = userService.getUserById(userId);
            LeadTaskStatus leadTaskStatus = leadTaskStatusDao.getLeadTaskStatusDetail(task.getTaskStatusId());
            message = "Your " + leadTaskStatus.getMasterLeadTask().getSingularDisplayName()
                    + " with "
                    + user.getFullName()
                    + ", "
                    + task.getLeadOfferId()
                    + " is due at ";
            if (!DateUtil.getNextDayStartTime(new Date()).after(task.getScheduledFor())) {
                message += DateUtil.getReadableDateFromDate(task.getScheduledFor()) + ", ";
            }
            message += DateUtil.getHHMMTimeFromDate(task.getScheduledFor()) + ". Please update.";
        }
        else {
            Map<Integer, List<LeadTask>> mappedTasks = mapTaskOnType(tasks);

            message = "You have ";

            List<String> midContents = new ArrayList<>();
            for (Integer typeId : mappedTasks.keySet()) {
                int size = mappedTasks.get(typeId).size();
                LeadTask task = mappedTasks.get(typeId).get(0);
                String content = Integer.toString(size);
                if (size == 1) {
                    content = "1 " + task.getTaskStatus().getMasterLeadTask().getSingularDisplayName();
                }
                else {
                    content = size + " " + task.getTaskStatus().getMasterLeadTask().getPluralDisplayName();
                }
                midContents.add(content);
            }
            message += com.proptiger.core.util.StringUtils.toSentence(midContents);
            message += " due. Please update your tasks.";
        }
        return message;
    }

    private String getTaskOverDueNotificationMessage(List<Notification> notifications) {
        List<Integer> taskIds = getObjectIdsFromNotifications(notifications);
        List<LeadTask> tasks = taskDao.findByIdInWithLead(taskIds);

        String message = "";
        if (tasks.size() == 1) {
            LeadTask task = tasks.get(0);
            User user = userService.getUserById(task.getLeadOffer().getLead().getClientId());
            message = "Your " + task.getTaskStatus().getMasterLeadTask().getSingularDisplayName()
                    + " with "
                    + user.getFullName()
                    + ", "
                    + task.getLeadOfferId()
                    + " is overdue by "
                    + DateUtil.getHumanReadableTimeDifference(new Date().getTime() - task.getScheduledFor().getTime())
                    + ". Please update.";
        }
        else {
            Map<Integer, List<LeadTask>> mappedTasks = mapTaskOnType(tasks);

            message = "You have ";

            List<String> midContents = new ArrayList<>();
            for (Integer typeId : mappedTasks.keySet()) {
                int size = mappedTasks.get(typeId).size();
                LeadTask task = mappedTasks.get(typeId).get(0);
                String content = Integer.toString(size);
                if (size == 1) {
                    content = "1 " + task.getTaskStatus().getMasterLeadTask().getSingularDisplayName();
                }
                else {
                    content = size + " " + task.getTaskStatus().getMasterLeadTask().getPluralDisplayName();
                }
                midContents.add(content);
            }
            message += com.proptiger.core.util.StringUtils.toSentence(midContents);
            message += " overdue. Please update your tasks.";
        }
        return message;
    }

    private Map<Integer, List<LeadTask>> mapTaskOnType(List<LeadTask> tasks) {
        Map<Integer, List<LeadTask>> mappedTasks = new HashMap<>();
        for (LeadTask task : tasks) {
            int typeId = task.getTaskStatus().getMasterLeadTask().getId();
            if (!mappedTasks.containsKey(typeId)) {
                mappedTasks.put(typeId, new ArrayList<LeadTask>());
            }
            mappedTasks.get(typeId).add(task);
        }
        return mappedTasks;
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

    public Notification createAndSendLeadOfferNotification(int offerId) {
        LeadOffer offer = leadOfferDao.getByIdWithRequirements(offerId);
        for (LeadRequirement leadRequirement : offer.getLead().getRequirements()) {
            leadRequirement.setLead(null);
        }

        int notificationTypeId = NotificationType.LeadOffered.getId();

        Notification notification = createNotification(
                offer.getAgentId(),
                notificationTypeId,
                offer.getId(),
                SerializationUtils.objectToJson(offer));

        sendLeadOfferNotification(offer.getAgentId());
        return notification;
    }

    /**
     * sends lead offer notification
     * 
     * @param userId
     */
    private void sendLeadOfferNotification(int userId) {
        List<Notification> notifications = notificationDao.findByUserIdAndNotificationTypeId(
                userId,
                NotificationType.LeadOffered.getId());
        List<Integer> offerIds = getObjectIdsFromNotifications(notifications);

        String message;
        String titleMessage;
        if (offerIds.size() == 0) {
            throw new ProAPIException();
        }
        if (offerIds.size() == 1) {
            titleMessage = "Claim New Lead";
            message = "1 lead is waiting to be claimed.";
        }
        else {
            titleMessage = "Claim New Leads";
            message = offerIds.size() + " leads are waiting to be claimed.";
        }

        GcmMessage gcmMessage = new GcmMessage();
        gcmMessage.setNotificationTypeId(NotificationType.LeadOffered.getId());
        gcmMessage.setData(offerIds);
        gcmMessage.setMessage(message);
        gcmMessage.setTitleMessage(titleMessage);

        sendGcmMessageUsingService(gcmMessage, userId);
    }

    private List<Integer> getObjectIdsFromNotifications(List<Notification> notifications) {
        List<Integer> objectIds = new ArrayList<>();
        for (Notification notification : notifications) {
            objectIds.add(notification.getObjectId());
        }
        return objectIds;
    }

    private void sendGcmMessageUsingService(GcmMessage gcmMessage, int userId) {
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                defaultNotificationType,
                userId,
                new DefaultMediumDetails(MediumType.MarketplaceApp, SerializationUtils.objectToJson(gcmMessage)
                        .toString()));
        notificationCreatorService.createNotificationGenerated(request);
    }

    /**
     * method to get gcm message content on lead offer
     * 
     * @param userId
     * @param notificationTypeId
     * @return
     */
    private String getGcmMessageContentForGroupableNotification(int userId, int notificationTypeId) {
        MarketplaceNotificationType notificationType = notificationTypeDao.findOne(notificationTypeId);
        // List<Notification> notifications = notificationDao
        // .findByUserIdAndNotificationTypeId(userId, notificationTypeId);
        // for (Notification notification : notifications) {
        // notification.setNotificationType(null);
        // }
        notificationType.setNotifications(new ArrayList<Notification>());

        return SerializationUtils.objectToJson(notificationType).toString();
    }

    /**
     * deletes offerred notification for a list of offers
     * 
     * @param offers
     */
    public void deleteLeadOfferNotificationForLead(List<LeadOffer> offers) {
        for (LeadOffer offer : offers) {
            Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                    offer.getId(),
                    NotificationType.LeadOffered.getId());
            if (notification != null) {
                notificationDao.delete(notification);
            }
        }
    }

    public int getRelationshipManagerUserId() {
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
        sendEmail(
                getRelationshipManagerUserId(),
                NotificationType.SaleSuccessful.getEmailSubject(),
                "Lead OfferID: " + leadOfferId + " of resale marketplace is marked as closed won.");
        return createNotification(
                getRelationshipManagerUserId(),
                NotificationType.SaleSuccessful.getId(),
                leadOfferId,
                null);
    }

    public Date getAuctionOverCutoffTime() {
        return DateUtil.getWorkingTimeSubtractedFromDate(
                new Date(),
                PropertyReader.getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_BIDDING_CYCLE_DURATION));
    }

    /**
     * send offer not yet claimed notification to rm
     * 
     * @param leadId
     */
    @Transactional
    public void manageLeadOfferedReminderForLead(int leadId) {
        Date endDate = getAuctionOverCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader.getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_CRON_BUFFER))
                        * 1000);

        Lead lead = leadDao.getLock(leadId);
        List<LeadOffer> offers = lead.getLeadOffers();

        Date maxOfferDate = new Date(0);
        boolean claimed = false;
        for (LeadOffer offer : offers) {
            claimed = claimed || offer.getMasterLeadOfferStatus().isClaimed();
            maxOfferDate = DateUtil.max(maxOfferDate, offer.getCreatedAt());
        }

        if (!claimed && maxOfferDate.after(startDate) && maxOfferDate.before(endDate)) {
            int notificationTypeId = NotificationType.AuctionOverWithoutClaim.getId();
            Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                    lead.getId(),
                    notificationTypeId);
            if (notification == null) {
                sendEmail(
                        getRelationshipManagerUserId(),
                        NotificationType.AuctionOverWithoutClaim.getEmailSubject(),
                        "Lead ID: " + lead.getId()
                                + " of resale marketplace is not yet claimed. Please intimate the brokers. Offer will get expired after "
                                + PropertyReader
                                        .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_POST_BIDDING_OFFER_DURATION)
                                + " working seconds.");
                createNotification(getRelationshipManagerUserId(), notificationTypeId, lead.getId(), null);
            }
        }
    }

    public void sendEmail(int userId, String subject, String content) {
        if (PropertyReader.getRequiredPropertyAsBoolean(PropertyKeys.MARKETPLACE_SENDEMAIL_USING_SERVICE)) {
            NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(userId, new MailDetails(
                    subject,
                    content));
            notificationCreatorService.createNotificationGenerated(request);
        }
        else {
            MailBody mailBody = new MailBody();
            mailBody.setSubject(subject);
            mailBody.setBody(content);

            MailDetails mailDetails = new MailDetails(mailBody);
            mailDetails.setMailTo(userService.getUserById(userId).getEmail());
            mailSender.sendMailUsingAws(mailDetails);
        }
    }

    public void moveToPrimary(int LeadId) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri;
        String stringUrl = "";
        try {
            stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.CRM_URL) + PropertyReader
                    .getRequiredPropertyAsString(PropertyKeys.CRM_MOVE_RESALE_LEAD_TO_PRIMARY) + LeadId;
            uri = new URI(stringUrl);
            restTemplate.getForObject(uri, Object.class);
        }
        catch (Exception e) {
            logger.error("Error in CRM API CALL: ", e);
        }
    }

    @Async
    public void moveToPrimaryAsync(int LeadId) {
        moveToPrimary(LeadId);
    }

    public void removeNotification(LeadOffer leadOfferInDB) {

        Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                leadOfferInDB.getId(),
                NotificationType.LeadOffered.getId());
        if (notification != null) {
            notificationDao.delete(notification);
        }
    }

    public void deleteNotificationsOfLeadOffersExpired(List<Integer> leadIdList, int notificationTypeId) {
        String string = StringUtils.join(leadIdList, ",");
        if (string != "") {
            notificationDao.deleteUsingNotificationTypeAndObjectId(
                    string,
                    notificationTypeId,
                    LeadOfferStatus.Offered.getId());
        }
    }

    public void manageHighTaskOverdueNotificationForRM() {
        int notificationTypeId = NotificationType.TooManyTasksOverDue.getId();
        int minOverDueTaskForNotification = PropertyReader
                .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_TASK_OVERDUE_COUNT_FOR_RM_NOTIFICATION);
        Date scheduledForBefore = DateUtil
                .addDays(
                        new Date(),
                        -1 * PropertyReader
                                .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_TASK_OVERDUE_DURATION_DAY_FOR_RM_NOTIFICATION));
        List<AgentOverDueTaskCount> overDueTaskCountForUsers = taskDao.findOverDueTasksForAgents(
                scheduledForBefore,
                minOverDueTaskForNotification);
        for (AgentOverDueTaskCount overDueTaskCountForUser : overDueTaskCountForUsers) {
            int userId = overDueTaskCountForUser.getAgentId();
            if (notificationDao.findByObjectIdAndNotificationTypeId(userId, notificationTypeId) == null) {
                int rmUserId = PropertyReader
                        .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_RELATIONSHIP_MANAGER_USER_ID);
                createNotification(rmUserId, notificationTypeId, userId, null);

                String companyname = companyUserService.getCompanyUsers(userId).get(0).getCompany().getName();
                String emailSubject = "Too Many Overdue Tasks for " + companyname;
                String emailContent = companyname + " has not updated "
                        + overDueTaskCountForUser.getOverDueTaskCount()
                        + " overdue tasks in last "
                        + PropertyReader
                                .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_TASK_OVERDUE_DURATION_DAY_FOR_RM_NOTIFICATION)
                        + " days.";
                sendEmail(rmUserId, emailSubject, emailContent);
            }
        }
        notificationDao.deleteTooManyTaskNotification(
                scheduledForBefore,
                minOverDueTaskForNotification,
                notificationTypeId);
    }

    public Notification findByUserIdAndNotificationId(int userId, int notificationTypeId, int objectId) {
        return notificationDao.findByUserIdAndNotificationTypeIdAndObjectId(userId, notificationTypeId, objectId);
    }

    public void deleteNotification(int userId, int notificationTypeId) {
        notificationDao.deleteNotification(userId, notificationTypeId);
    }

    public void deleteNotification(int userId, int notificationTypeId, int objectId) {
        notificationDao.deleteRMNotification(userId, notificationTypeId, objectId);
    }
}
