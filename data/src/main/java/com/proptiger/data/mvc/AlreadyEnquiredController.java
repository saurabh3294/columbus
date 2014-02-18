package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.AlreadyEnquiredService;
import com.proptiger.data.service.AlreadyEnquiredService.AlreadyEnquiredDetails;
import com.proptiger.data.util.Constants;

/**
 * APIs to find whether a user have already enquired about a entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@DisableCaching
public class AlreadyEnquiredController extends BaseController {

    @Autowired
    private AlreadyEnquiredService alreadyEnquiredService;

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/enquired")
    @ResponseBody
    @Deprecated
    public ProAPIResponse hasEnquired(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @RequestParam(value = "projectId") Integer projectId) {
        AlreadyEnquiredDetails enquiredDetails = alreadyEnquiredService.hasEnquired(
                projectId,
                userInfo.getUserIdentifier());
        return new ProAPISuccessResponse(enquiredDetails);
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/project/{projectId}/enquired")
    @ResponseBody
    public ProAPIResponse hasEnquired_(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @PathVariable Integer projectId) {
        AlreadyEnquiredDetails enquiredDetails = alreadyEnquiredService.hasEnquired(
                projectId,
                userInfo.getUserIdentifier());
        return new ProAPISuccessResponse(enquiredDetails);
    }
}
