package com.proptiger.data.mvc.notification;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.model.user.User;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.SubscriptionType;
import com.proptiger.data.notification.model.UserNotificationTypeSubscription;
import com.proptiger.data.notification.model.external.NotificationSubscriptionRequest;
import com.proptiger.data.notification.service.UserNotificationTypeSubscriptionService;
import com.proptiger.data.service.user.UserService;

/**
 * @author Sahil Garg
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity")
public class NotificationSubscriptionController extends BaseController {

    @Autowired
    private UserNotificationTypeSubscriptionService subscriptionService;

    @Autowired
    private UserService                             userService;

    /**
     * This API can be used to subscribe the logged in user to the given
     * notificationTypes
     * 
     * @param notificationTypes
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/notification/subscribe", method = RequestMethod.POST)
    public APIResponse subscribeToNotifications(
            @RequestBody List<NotificationTypeEnum> notificationTypes,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {

        User user = new User();
        user.setId(userInfo.getUserIdentifier());

        NotificationSubscriptionRequest request = new NotificationSubscriptionRequest(
                user,
                notificationTypes,
                SubscriptionType.Subscribed);
        List<UserNotificationTypeSubscription> result = subscriptionService.updateNotificationSubscription(request);

        if (result == null || result.isEmpty()) {
            return new APIResponse(
                    ResponseCodes.INTERNAL_SERVER_ERROR,
                    "Could not subscribe User with id: " + user.getId() + " to notificationTypes: " + notificationTypes);
        }
        return new APIResponse(result);
    }

    /**
     * This API can be used to unsubscribe the logged in user to the given
     * notificationTypes
     * 
     * @param notificationTypes
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/notification/unsubscribe", method = RequestMethod.POST)
    public APIResponse unsubscribeToNotifications(
            @RequestBody List<NotificationTypeEnum> notificationTypes,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {

        User user = new User();
        user.setId(userInfo.getUserIdentifier());

        NotificationSubscriptionRequest request = new NotificationSubscriptionRequest(
                user,
                notificationTypes,
                SubscriptionType.Unsubscribed);
        List<UserNotificationTypeSubscription> result = subscriptionService.updateNotificationSubscription(request);

        if (result == null || result.isEmpty()) {
            return new APIResponse(
                    ResponseCodes.INTERNAL_SERVER_ERROR,
                    "Could not unsubscribe User with id: " + user.getId()
                            + " to notificationTypes: "
                            + notificationTypes);
        }
        return new APIResponse(result);
    }

    /**
     * This API can be used to subscribe/unsubscribe the given users to the
     * given notificationTypes
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/notification/update-subscription", method = RequestMethod.POST)
    public APIResponse updateNotificationSubscription(@Valid @RequestBody NotificationSubscriptionRequest request) {

        List<User> registeredUsers = new ArrayList<User>();
        for (User user : request.getUsers()) {
            User registeredUser = userService.getUserById(user.getId());
            if (registeredUser == null) {
                return new APIResponse(ResponseCodes.BAD_REQUEST, "User " + user.getId() + " is not registered");
            }
            registeredUsers.add(registeredUser);
        }
        request.setUsers(registeredUsers);
        List<UserNotificationTypeSubscription> result = subscriptionService.updateNotificationSubscription(request);

        if (result == null || result.isEmpty()) {
            return new APIResponse(ResponseCodes.INTERNAL_SERVER_ERROR, "Could not update subscription.");
        }
        return new APIResponse(result);
    }

}
