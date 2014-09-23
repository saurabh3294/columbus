package com.proptiger.data.mvc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.security.OTPService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 *
 */
@Controller

public class OTPController extends BaseController{

    @Autowired
    private OTPService otpService;

    @RequestMapping(value = "app/v1/otp/validate", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse validateOTP(
            @RequestBody Integer otp,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {

        boolean valid = otpService.validate(otp, activeUser);
        return new APIResponse(valid);
    }
    
    @RequestMapping(value = "app/v1/otp", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getOTP(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        otpService.respondWithOTP(activeUser);;
        return new APIResponse();
    }

}
