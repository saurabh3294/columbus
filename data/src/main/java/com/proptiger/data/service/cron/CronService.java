package com.proptiger.data.service.cron;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.config.scheduling.QuartzScheduledClass;
import com.proptiger.core.config.scheduling.QuartzScheduledJob;
import com.proptiger.core.enums.LeadOfferStatus;
import com.proptiger.core.enums.NotificationType;
import com.proptiger.core.exception.ConstraintViolationException;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.service.marketplace.LeadOfferService;
import com.proptiger.data.service.marketplace.LeadService;
import com.proptiger.data.service.marketplace.LeadTaskService;
import com.proptiger.data.service.marketplace.NotificationService;

/**
 * 
 * @author azi
 * 
 */

@Component
@QuartzScheduledClass
public class CronService {
    @Autowired
    private LeadService         leadService;

    @Autowired
    private LeadTaskService     leadTaskService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LeadOfferDao        leadOfferDao;

    @Autowired
    private LeadOfferService    leadOfferService;

    @Autowired
    private LeadDao             leadDao;

    private static Logger       logger = LoggerFactory.getLogger(CronService.class);

    @QuartzScheduledJob(initialDelay = 10000, fixedDelay = 1800000)
    public void manageLeadAssignmentWithCycle() {
        Date createdSince = new Date(
                new Date().getTime() - PropertyReader.getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_CRON_BUFFER)
                        * 1000);
        List<Lead> leads = leadDao.getMergedLeadsWithoutOfferCreatedSince(createdSince);
        Date expireTime = notificationService.getNoBrokerClaimedCutoffTime();

        List<Lead> leadsWithLeadOfferExpired = leadDao.getMergedLeadsByOfferCreatedAtLessThanAndOfferStatusId(
                expireTime,
                LeadOfferStatus.Offered.getId());
        Set<Integer> leadIds = new HashSet<Integer>();

        for (Lead lead : leads) {
            leadIds.add(lead.getId());
        }

        Map<Integer, Integer> maxPhaseIdMapLeadId = new HashMap<Integer, Integer>();
        for (Lead lead : leadsWithLeadOfferExpired) {
            List<LeadOffer> offers = lead.getLeadOffers();
            int maxPhaseId = 0;
            for (LeadOffer offer : offers) {
                if (maxPhaseId < offer.getPhaseId()) {
                    maxPhaseId = offer.getPhaseId();
                }
            }
            maxPhaseIdMapLeadId.put(lead.getId(), maxPhaseId);
        }

        for (Lead lead : leadsWithLeadOfferExpired) {
            List<LeadOffer> offers = lead.getLeadOffers();
            int claimedCount = 0;
            for (LeadOffer offer : offers) {
                if (offer.getMasterLeadOfferStatus().isClaimed() && maxPhaseIdMapLeadId.get(offer.getLeadId()) == offer
                        .getPhaseId()) {
                    claimedCount++;
                }
            }

            if (!PropertyReader.getRequiredPropertyAsType(
                    PropertyKeys.MARKETPLACE_MAX_BROKER_COUNT_FOR_CLAIM,
                    Integer.class).equals(claimedCount)) {
                leadIds.add(lead.getId());
            }
        }

        List<Integer> leadIdList = new ArrayList<Integer>();
        for (Integer leadId : leadIds) {
            leadIdList.add(leadId);
        }

        manageNoBrokerClaimedNotification();
        if (!leadIds.isEmpty()) {
            notificationService
                    .deleteNotificationsOfLeadOffersExpired(leadIdList, NotificationType.LeadOffered.getId());
            leadOfferDao.updateStatusByLeadIdInAndStatus(
                    leadIdList,
                    LeadOfferStatus.Offered.getId(),
                    LeadOfferStatus.Expired.getId());
        }

        for (Integer leadId : leadIdList) {
            try {
                leadService.manageLeadAuctionWithCycle(
                        leadId,
                        maxPhaseIdMapLeadId,
                        maxPhaseIdMapLeadId.get(leadId) == null ? 0 : maxPhaseIdMapLeadId.get(leadId),
                        0);
            }

            catch (Exception e) {
                logger.error("Error in lead assignment: ", e);
            }

        }
    }

    @QuartzScheduledJob(
            initialDelayString = "${marketplace.notification.initial.delay}",
            fixedDelayString = "${marketplace.notification.fixed.delay}")
    public void populateNotification() {
        leadTaskService.manageCallDueNotification();
        leadTaskService.populateTaskDueNotification();
        leadTaskService.populateTaskOverDueNotification();
    }

    @QuartzScheduledJob(cron = "0 0 9 * * ?")
    public void sendTaskOverDueNotification() {
        populateNotification();
        notificationService.sendTaskOverDueNotification();
    }

    @QuartzScheduledJob(cron = "0 0 9,18 * * ?")
    public void sendTaskDueNotification() {
        populateNotification();
        notificationService.sendTaskDueNotification();
    }

    // @Scheduled(initialDelay = 4000, fixedDelay = 60000)
    public void manageNoBrokerClaimedNotification() {
        Date endDate = notificationService.getNoBrokerClaimedCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader.getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_CRON_BUFFER))
                        * 1000);
        List<Lead> leads = leadDao.getMergedLeadsByOfferredAtBetweenAndOfferStatusId(
                startDate,
                endDate,
                LeadOfferStatus.Offered.getId());
        for (Lead lead : leads) {
            try {
                leadOfferService.manageLeadOfferedNotificationDeletionForLead(lead.getId());
            }
            catch (ConstraintViolationException e) {
                logger.error("Error while deleting lead offer notification for lead id: " + lead.getId(), e);
            }
        }
    }

    @QuartzScheduledJob(initialDelay = 50000, fixedDelay = 1800000)
    public void manageLeadOfferedReminder() {
        Date endDate = notificationService.getAuctionOverCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader.getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_CRON_BUFFER))
                        * 1000);
        List<Lead> leads = leadDao.getMergedLeadsByOfferredAtBetweenAndOfferStatusId(
                startDate,
                endDate,
                LeadOfferStatus.Offered.getId());
        for (Lead lead : leads) {
            try {
                notificationService.manageLeadOfferedReminderForLead(lead.getId());
            }
            catch (ConstraintViolationException e) {
                logger.error("Error while sending lead offer reminder to rm for lead id: " + lead.getId(), e);
            }
        }
    }

    @QuartzScheduledJob(cron = "0 0 9 * * ?")
    public void sendLimitReachedGCMNotifications() {
        notificationService.sendLimitReachedGCMNotifications();
    }

    @QuartzScheduledJob(
            initialDelayString = "${marketplace.notification.initial.delay}",
            fixedDelayString = "${marketplace.notification.fixed.delay}")
    public void manageHighTaskOverdueNotificationForRM() {
        notificationService.manageHighTaskOverdueNotificationForRM();
    }
}
