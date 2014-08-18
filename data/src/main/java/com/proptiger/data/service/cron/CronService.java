package com.proptiger.data.service.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.service.marketplace.LeadService;

/**
 * 
 * @author azi
 * 
 */

@Component
public class CronService {
    @Autowired
    private LeadService   leadService;

    private static Logger logger = LoggerFactory.getLogger(CronService.class);

    @Scheduled(cron = "0 * * * * *")
    private void manageLeadAssignment() {
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
}
