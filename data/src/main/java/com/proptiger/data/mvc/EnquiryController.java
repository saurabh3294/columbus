package com.proptiger.data.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.EnquiryService;

@Controller
@RequestMapping(value = "data/v1/entity/enquiry")
public class EnquiryController extends BaseController {
    
    @Autowired
    private EnquiryService enquiryService;
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object createLead(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody Enquiry enquiry) {
        
       return new APIResponse(enquiryService.createLeadEnquiry(enquiry, request, response));
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/{enquiryId}")
    @ResponseBody
    public Object updateLead(
            HttpServletRequest request,
            @PathVariable Long enquiryId,
            @RequestBody Enquiry enquiry) {
        return new APIResponse(enquiryService.updateLeadEnquiry(enquiry, enquiryId, request));
    }
}