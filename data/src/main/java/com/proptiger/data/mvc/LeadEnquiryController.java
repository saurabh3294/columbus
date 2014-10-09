package com.proptiger.data.mvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.LeadEnquiryService;

@Controller
@RequestMapping(value = "data/v1/entity/lead/")
public class LeadEnquiryController extends BaseController {
    @Autowired
    private LeadEnquiryService leadService;
    
    

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object createLead(
        HttpServletRequest request,
        @RequestBody Enquiry enquiry) {
        
       return new APIResponse(leadService.createLeadEnquiry(enquiry, request));
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Object updateLead(
            HttpServletRequest request,
            @RequestBody Enquiry enquiry) {
        return new APIResponse(leadService.updateLeadEnquiry(enquiry, request).getResults());
    }
}
