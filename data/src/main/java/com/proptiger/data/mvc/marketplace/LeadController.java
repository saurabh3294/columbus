/**
 * 
 */
package com.proptiger.data.mvc.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.service.marketplace.LeadService;

/**
 * @author Anubhav
 * 
 */
@Controller
@DisableCaching
public class LeadController extends BaseController {
    @Autowired
    private LeadService leadService;

    @RequestMapping(value = "data/v1/entity/lead", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse create(@RequestBody Lead lead) {
        Lead createdLead = leadService.createLead(lead);
        try {
            leadService.manageLeadAuctionWithBeforeCycle(createdLead.getId());
        }
        catch (Exception e) {
        }
        return new APIResponse(createdLead);
    }

    @RequestMapping(value = "data/v1/entity/lead/{leadId}/request-more-brokers", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse requestMoreBrokers(@PathVariable int leadId) {
        try {
            leadService.manageLeadAuctionWithBeforeCycleForRequestBrokers(leadId);
        }
        catch (Exception e) {
        }
        return new APIResponse();
    }

    @RequestMapping(value = "data/v1/entity/lead/exists")
    @ResponseBody
    public APIResponse get(@RequestParam String email, @RequestParam int cityId) {
        return new APIResponse(leadService.exists(email, cityId));
    }
}