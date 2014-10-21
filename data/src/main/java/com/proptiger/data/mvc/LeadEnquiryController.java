package com.proptiger.data.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.LeadEnquiryService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping(value = "data/v1/entity/lead-enquiry")
public class LeadEnquiryController extends BaseController {
    
    @Autowired
    private LeadEnquiryService leadEnquiryService;
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object createLead(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody Enquiry enquiry) {
        
       return new APIResponse(leadEnquiryService.createLeadEnquiry(enquiry, request, response));
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/enquiryId/{enquiryId}")
    @ResponseBody
    public Object updateLead(
            HttpServletRequest request,
            @PathVariable Long enquiryId,
            @RequestBody Enquiry enquiry) {
        return new APIResponse(leadEnquiryService.updateLeadEnquiry(enquiry, enquiryId, request));
    }
}