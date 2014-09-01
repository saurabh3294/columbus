package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.marketplace.NotificationService;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * 
 * @author azi
 * 
 */
@DisableCaching
@Controller
@RequestMapping("/data/v1/entity/user/notification")
public class NotificationController extends BaseController {
    @Autowired
    private NotificationService notificationService;

    @RequestMapping
    @ResponseBody
    public APIResponse getNotificationForUser() {
        return new APIResponse(notificationService.getNotificationsForUser(Integer.parseInt(SecurityContextUtils
                .getLoggedInUser().getUserId())));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateNotificationForUser(@RequestBody List<Notification> notifications) {
        return new APIResponse(notificationService.updateNotificationsForUser(
                SecurityContextUtils.getLoggedInUserId(),
                notifications));
    }
}