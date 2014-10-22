package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.model.GCMUser;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.GCMUserService;

/**
 * @author Sahil Garg
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/gcm-user")
public class GCMUserController {

    @Autowired
    private GCMUserService gcmUserService;

    /**
     * This API is used to add a new GCM used if a user installs an Android App.
     * It will also update the logged in used info corresponding to a GCMRegId.
     * 
     * @param gcmUser
     * @param userInfo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public APIResponse postGCMUser(@RequestBody GCMUser gcmUser) {
        ActiveUser activeUser = SecurityContextUtils.getActiveUser();
        Integer userId = null;
        if (activeUser != null) {
            userId = activeUser.getUserIdentifier();
        }
        GCMUser result = gcmUserService.postGCMUser(gcmUser, userId);

        if (result == null) {
            return new APIResponse(ResponseCodes.REQUEST_PARAM_INVALID, "GCM Request ID or App Identifier not found.");
        }
        return new APIResponse(result);
    }

    /**
     * This API is used to delete a GCM User if the user uninstalls an Android
     * App
     * 
     * @param gcmRegId
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "/{gcmRegId}", method = RequestMethod.DELETE)
    @ResponseBody
    public APIResponse deleteGCMUser(@PathVariable String gcmRegId) {
        gcmUserService.deleteGCMUser(gcmRegId);
        return new APIResponse();
    }

}
