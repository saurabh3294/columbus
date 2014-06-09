package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.UserPreferenceService;
import com.proptiger.data.util.Constants;

/**
 * B2b User Detail Controller
 * 
 * @author Azitabh Ajit
 * 
 */

@Controller
@RequestMapping
public class UserPreferenceController extends BaseController {
    @Autowired
    UserPreferenceService b2bUserPreferenceService;

    @RequestMapping(value = "/data/v1/entity/user/preference", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateUserPreference(
            @RequestBody UserPreference b2bUserDetail,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {

        return new APIResponse(b2bUserPreferenceService.updateUserPreference(b2bUserDetail, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/b2b/appDetails", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getUserPreference(@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo)
            throws Exception {
        return new APIResponse(b2bUserPreferenceService.getUserPreferences(userInfo.getUserIdentifier()));
    }
}