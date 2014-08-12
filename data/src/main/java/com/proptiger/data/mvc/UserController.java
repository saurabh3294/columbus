package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.data.internal.dto.Register;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.service.user.UserService.AlreadyEnquiredDetails;
import com.proptiger.data.util.Constants;

/**
 * APIs to find whether a user have already enquired about a entity
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
@Controller
@DisableCaching
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/enquired")
    @ResponseBody
    @Deprecated
    public APIResponse hasEnquired(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestParam(value = "projectId") Integer projectId) {
        return hasEnquiredByUser(userInfo, projectId);
    }

    private APIResponse hasEnquiredByUser(ActiveUser userInfo, Integer projectId) {
        AlreadyEnquiredDetails enquiredDetails = userService.hasEnquired(projectId, userInfo.getUserIdentifier());
        return new APIResponse(enquiredDetails);
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/project/{projectId}/enquired")
    @ResponseBody
    public APIResponse hasEnquired_(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @PathVariable Integer projectId) {
        return hasEnquiredByUser(userInfo, projectId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/registered")
    @ResponseBody
    public APIResponse isRegistered(String email) {
        return new APIResponse(userService.isRegistered(email));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/app/v1/user/details")
    @ResponseBody
    public APIResponse getUserDetails(@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(userService.getUserDetails(userInfo.getUserIdentifier()));
    }
    
    @RequestMapping(value = "data/v1/entity/user/who-am-i", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse whoAmI(){
        return new APIResponse(userService.getWhoAmIDetail());
    }
    
    @RequestMapping(value = "data/v1/entity/user/change-password", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse changePassword(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestBody ChangePassword changePassword) {
        userService.changePassword(userInfo, changePassword);
        return new APIResponse();
    }
    @RequestMapping(value = Constants.Security.REGISTER_URL, method = RequestMethod.POST)
    @ResponseBody
    public APIResponse register(@RequestBody Register register){
        ForumUser forumUser = userService.register(register);
        return new APIResponse(userService.getUserDetails(forumUser.getUserId()));
    }
    
    @RequestMapping(value = "app/v1/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse resetPassword(@RequestParam String email){
        String message = userService.resetPassword(email);
        return new APIResponse(message);
    }
    
}