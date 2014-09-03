package com.proptiger.data.service.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.service.marketplace.LeadService;
import com.proptiger.data.service.marketplace.NotificationService;

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

    private static Logger       logger = LoggerFactory.getLogger(CronService.class);

    @Scheduled(initialDelay = 10000, fixedDelay = 10000000)
    public void manageLeadAssignment() {
        List<Lead> leads = leadService.getLeadsPendingAction();
        for (Lead lead : leads) {
            try {
                leadService.manageLeadAuction(lead.getId());
            }
            catch (Exception e) {
                logger.debug("Error in lead assignment: " + e);
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
}