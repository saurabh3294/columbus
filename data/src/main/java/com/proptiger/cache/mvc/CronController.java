package com.proptiger.cache.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.cron.CronService;

/**
 * Controller to execute crons for testing
 * 
 * @author azi
 * 
 *         XXX Should be disabled on production to avoid concurrancy issues
 * 
 */
@Controller
@DisableCaching
@RequestMapping("cron/")
public class CronController {
    @Autowired
    private CronService cronService;

    @RequestMapping("/v1/lead-assign")
    public @ResponseBody
    APIResponse leadAssignCron() throws Exception {
        cronService.manageLeadAssignment();
        return new APIResponse();
    }

    @RequestMapping("/v1/no-broker-claimed")
    public @ResponseBody
    APIResponse leadNotClaimed() throws Exception {
        cronService.manageNoBrokerClaimedNotification();
        return new APIResponse();
    }

    @RequestMapping("/v1/intimate-brokers")
    public @ResponseBody
    APIResponse intimateBrokersForClaim() throws Exception {
        cronService.manageLeadOfferedReminder();
        return new APIResponse();
    }

    @RequestMapping("/v1/populate-notifications")
    public @ResponseBody
    APIResponse populateNotifications() throws Exception {
        cronService.populateNotification();
        return new APIResponse();
    }

    @RequestMapping("/v1/send-notifications")
    public @ResponseBody
    APIResponse sendNotifications() throws Exception {
        cronService.populateNotification();
        cronService.sendTaskDueNotification();
        cronService.sendTaskOverDueNotification();
        return new APIResponse();
    }
}
