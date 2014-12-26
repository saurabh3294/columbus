package com.proptiger.data.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.Application;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApplicationNameService;
import com.proptiger.core.util.Constants;
import com.proptiger.data.external.dto.CustomUser;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.data.internal.dto.RegisterUser;
import com.proptiger.data.model.user.UserDetails;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.service.user.UserService.AlreadyEnquiredDetails;
import com.proptiger.data.service.user.UserService.UserCommunicationType;

/**
 * User APIs to get/register/update/delete a user entity
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
@Controller
@DisableCaching
public class UserController extends BaseController {

    @Value("${proptiger.url}")
    private String      proptigerUrl;

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
    public APIResponse getUserDetails(@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        return new APIResponse(userService.getUserDetails(
                activeUser.getUserIdentifier(),
                activeUser.getApplicationType(), true));
    }

    @RequestMapping(value = "data/v1/entity/user/who-am-i", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse whoAmI() {
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
    public APIResponse register(@RequestBody RegisterUser register) {
        Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
        Integer userId = userService.register(register, applicationType);
        return new APIResponse(userService.getUserDetails(userId, applicationType, true));

    }

    @RequestMapping(value = "app/v1/reset-password", method = RequestMethod.POST, params = {"email"})
    @ResponseBody
    public APIResponse resetPassword(
            @RequestParam String email) {
        Object message = userService.processResetPasswordRequest(email);
        return new APIResponse(message);
    }
    
    @RequestMapping(value = "app/v1/reset-password", method = RequestMethod.POST, params = {"token"})
    @ResponseBody
    public APIResponse resetPasswordUsingToken(
            @RequestParam String token,
            @RequestBody ChangePassword changePassword) {
        Object message = userService.resetPasswordUsingToken(token, changePassword);
        return new APIResponse(message);
    }

    /**
     * Accesible by users having role UserRole.ADMIN_BACKEND
     * 
     * @param email
     * @return
     */
    @RequestMapping(value = "app/v1/user/details-by-email", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getUserDetailsByEmailId(@RequestParam String email) {
        CustomUser customUser = userService.getUserDetailsByEmail(email);
        return new APIResponse(customUser);
    }

    @RequestMapping(value = Constants.Security.USER_VALIDATE_API, method = RequestMethod.GET)
    @ResponseBody
    public void validateUserCommunicationDetails(
            @RequestParam UserCommunicationType type,
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        userService.validateUserCommunicationDetails(type, token);
        response.sendRedirect(proptigerUrl + "?flag=email_valid");
    }

    @RequestMapping(value = "app/v1/entity/user/details", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateUserDetails(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestBody UserDetails user) throws IOException {
        return new APIResponse(userService.updateUserDetails(user, userInfo));
    }
    
    @RequestMapping(value = "app/v1/entity/user/child", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getChild(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) throws IOException {
        return new APIResponse(userService.getChildHeirarchy(activeUser));
    }
    
}