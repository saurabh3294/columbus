package com.proptiger.data.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.service.user.UserEnquiredService.AlreadyEnquiredDetails;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
public class EnquiredProjectController {

    @Autowired
    private UserEnquiredService enquiredPropertyService;
    
    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/enquired")
    @ResponseBody
    @Deprecated
    public APIResponse hasEnquired(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestParam(value = "projectId") Integer projectId) {
        return hasEnquiredByUser(userInfo, projectId);
    }

    private APIResponse hasEnquiredByUser(ActiveUser userInfo, Integer projectId) {
        AlreadyEnquiredDetails enquiredDetails = enquiredPropertyService.hasEnquired(projectId, userInfo.getUserIdentifier());
        return new APIResponse(enquiredDetails);
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/project/{projectId}/enquired")
    @ResponseBody
    public APIResponse hasEnquired_(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @PathVariable Integer projectId) {
        return hasEnquiredByUser(userInfo, projectId);
    }

}
