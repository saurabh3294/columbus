package com.proptiger.data.mvc.b2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.B2bUserDetail;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.b2b.B2bUserDetailService;
import com.proptiger.data.util.Constants;

/**
 * B2b User Detail Controller
 * 
 * @author Azitabh Ajit
 * 
 */

@Controller
@RequestMapping
public class B2bUserDetailController extends BaseController {
    @Autowired
    B2bUserDetailService b2bUserDetailService;

    @RequestMapping(value = "/data/v1/entity/user/b2b/user-details", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateUserPreference(
            @RequestBody B2bUserDetail b2bUserDetail,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(b2bUserDetailService.updateUserDetails(b2bUserDetail, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/b2b/user-details", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getUserPreference(@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo)
            throws Exception {
        return new APIResponse(b2bUserDetailService.getUserDetails(userInfo));
    }
}