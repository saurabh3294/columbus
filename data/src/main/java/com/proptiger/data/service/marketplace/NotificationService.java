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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.data.enums.LeadTaskName;
import com.proptiger.data.enums.NotificationType;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.MarketplaceNotificationType;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.MarketplaceNotificationTypeDao;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.user.UserService;
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
    private LeadDao                        leadDao;

    @Autowired
    NotificationGeneratedService           generatedService;

    @Autowired
    NotificationMessageService             notificationMessageService;

    public static final List<Integer>     allMasterTaskIdsButCall = new ArrayList<>();

    private static final String            defaultNotificationType = "marketplace_default";

    @Autowired
    UserService                            userService;

    @Autowired
    MailSender                             mailSender;

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
     * 
     * 
     * @param notificationTypeId
     * @param nextTask
     * @param sendNotification
     * @return
     */
    public Notification createNotificationForTask(int notificationTypeId, LeadTask nextTask, boolean sendNotification) {
        Notification notification = notificationDao.findByObjectIdAndNotificationTypeId(
                nextTask.getId(),
                notificationTypeId);
        if (notification == null) {
            notification = createTaskNotification(nextTask, notificationTypeId);
            if (sendNotification) {
                // XXX GCM notification being sent
                if (PropertyReader.getRequiredPropertyAsBoolean(PropertyKeys.MARKETPLACE_GCM_SEND_ALL)) {
                    generatedService.createNotificationGenerated(Arrays.asList(notificationMessageService
                            .createNotificationMessage(
                                    defaultNotificationType,
                                    notification.getUserId(),
                                    notification.getStringDetails())), Arrays.asList(MediumType.MarketplaceApp));
                }
            }
        }
        return notification;
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
        List<LeadOffer> leadOffers = leadOfferDao.getLegitimateLeadOffersForDuplicateLeadNotifications(lead.getId());
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
            }
        }
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
            if (PropertyReader.getRequiredPropertyAsBoolean(PropertyKeys.MARKETPLACE_GCM_SEND_ALL)) {
                generatedService.createNotificationGenerated(Arrays.asList(notificationMessageService
                        .createNotificationMessage(defaultNotificationType, userId, message.toString())), Arrays
                        .asList(MediumType.MarketplaceApp));
            }
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
        if (PropertyReader.getRequiredPropertyAsBoolean(PropertyKeys.MARKETPLACE_GCM_SEND_NEW_OFFER)) {
            generatedService.createNotificationGenerated(Arrays.asList(notificationMessageService
                    .createNotificationMessage(
                            defaultNotificationType,
                            notification.getUserId(),
                            notification.getStringDetails())), Arrays.asList(MediumType.MarketplaceApp));
        }
        return notification;
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
            generatedService.createNotificationGenerated(
                    Arrays.asList(new NotificationMessage(userId, subject, content)),
                    Arrays.asList(MediumType.Email));
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
}