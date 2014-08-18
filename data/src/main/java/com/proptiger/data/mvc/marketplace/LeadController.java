/**
 * 
 */
package com.proptiger.data.mvc.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.marketplace.LeadService;
import com.proptiger.data.util.Constants;

/**
 * @author Anubhav
 *
 */
@Controller
public class LeadController extends BaseController {
    @Autowired
    private LeadService leadService;
    
    @RequestMapping(value = "data/v1/entity/lead", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse create(@RequestBody Lead lead) {
        
        //return new APIResponse(lead.getClient().getEmails().get(0).getEmail());
        return new APIResponse(leadService.createLead(lead));
    }
    
    @RequestMapping(value = "data/v1/entity/user/lead-offer")
    @ResponseBody
    public APIResponse get(@ModelAttribute FIQLSelector selector, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {        
        PaginatedResponse<List<LeadOffer>> leadOffers = leadService.getLeadOffers(activeUser.getUserIdentifier(),selector);
        return new APIResponse(super.filterFieldsFromSelector(leadOffers, selector), leadOffers.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/lead/exists")
    @ResponseBody
    public APIResponse get(@RequestParam String email, @RequestParam int cityId) {        
        return new APIResponse(leadService.exists(email, cityId));
    }
}
