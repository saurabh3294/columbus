package com.proptiger.data.mvc.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.model.proptiger.Enquiry.EnquiryCustomDetails;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.service.user.UserEnquiredService;

/**
 * Providing API to get enquired property of user
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/enquired-property")
public class EnquiredPropertyController extends BaseController {

    @Autowired
    private UserEnquiredService enquiredPropertyService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getEnquiredProperties(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
         List<EnquiryCustomDetails> result = enquiredPropertyService.getEnquiries(activeUser);
        return new APIResponse(result, result.size());
    }
}
