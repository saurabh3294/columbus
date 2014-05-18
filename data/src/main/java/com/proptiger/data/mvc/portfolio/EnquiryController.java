package com.proptiger.data.mvc.portfolio;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.Enquiry.EnquiryCustomDetails;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.portfolio.EnquiryService;
import com.proptiger.data.util.Constants;

/**
 * Providing API to get enquired property of user
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/enquired-property")
public class EnquiryController extends BaseController {

    @Autowired
    private EnquiryService enquiryService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getEnquiredProperties(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
         List<EnquiryCustomDetails> result = enquiryService.getEnquiries(userInfo.getUserIdentifier());
        return new APIResponse(result, result.size());
    }
}
