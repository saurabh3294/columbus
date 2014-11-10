package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.model.user.User;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.external.NotificationCreatorServiceRequest;
import com.proptiger.data.notification.service.external.NotificationCreatorService;
import com.proptiger.data.service.user.UserService;

/**
 * @author Sahil Garg
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/notification/sender")
public class SendNotificationController {

    @Autowired
    private NotificationCreatorService notificationCreatorService;

    @Autowired
    private UserService                userService;

    /**
     * This API is used to send notification of given notification type to a
     * particular user
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public APIResponse sendNotification(@Valid @RequestBody NotificationCreatorServiceRequest request) {

        List<User> registeredUsers = new ArrayList<User>();
        for (User user : request.getUsers()) {
            registeredUsers.add(userService.findOrCreateUser(user));
        }

        request.setUsers(registeredUsers);
        List<NotificationGenerated> notificationGeneratedList = notificationCreatorService
                .createNotificationGenerated(request);
        if (notificationGeneratedList == null) {
            return new APIResponse(ResponseCodes.BAD_REQUEST, "Not able to generate Notification.");
        }
        return new APIResponse(notificationGeneratedList);
        
    }

}
