package com.proptiger.data.service.cron;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.service.marketplace.LeadService;
import com.proptiger.data.service.marketplace.NotificationService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ConstraintViolationException;

/**
 * 
 * @author azi
 * 
 */

@Component
public class CronService {
    @Autowired
    private LeadService         leadService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LeadOfferDao        leadOfferDao;

    @Autowired
    private LeadDao             leadDao;

    private static Logger       logger = LoggerFactory.getLogger(CronService.class);

    @Scheduled(initialDelay = 10000, fixedDelay = 1800000)
    public void manageLeadAssignment() {
        Date createdSince = new Date(new Date().getTime() - 7200 * 1000);
        List<Lead> leads = leadDao.getMergedLeadsWithoutOfferCreatedSince(createdSince);
        for (Lead lead : leads) {
            try {
                leadService.manageLeadAuction(lead.getId());
            }
            catch (Exception e) {
                logger.error("Error in lead assignment: " + e);
            }
        }
    }

    @Scheduled(initialDelay = 20000, fixedDelay = 300000)
    public void manageCallDueNotification() {
        notificationService.manageCallDueNotification();
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 1800000)
    public void populateTaskDueNotification() {
        notificationService.populateTaskDueNotification();
    }

    @Scheduled(initialDelay = 40000, fixedDelay = 1800000)
    public void populateTaskOverDueNotification() {
        notificationService.populateTaskOverDueNotification();
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendTaskOverDueNotification() {
        notificationService.populateTaskOverDueNotification();
        notificationService.sendTaskOverDueNotification();
    }

    @Scheduled(cron = "0 0 9,18 * * ?")
    public void sendTaskDueNotification() {
        notificationService.populateTaskDueNotification();
        notificationService.sendTaskDueNotification();
    }

    @Scheduled(initialDelay = 40000, fixedDelay = 1800000)
    public void manageNoBrokerClaimedNotification() {
        Date endDate = notificationService.getNoBrokerClaimedCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader
                        .getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_NO_BROKER_CLAIMED_CRON_BUFFER))*1000);
        List<Lead> leads = leadDao.getMergedLeadsByOfferredAtBetweenAndOfferStatusId(
                startDate,
                endDate,
                LeadOfferStatus.Offered.getLeadOfferStatusId());
        for (Lead lead : leads) {
            try {
                notificationService.manageLeadOfferedNotificationDeletionForLead(lead.getId());
            }
            catch (ConstraintViolationException e) {
                logger.error("Error while deleting lead offer notification for lead id: " + lead.getId() + e);
            }
        }
    }
}