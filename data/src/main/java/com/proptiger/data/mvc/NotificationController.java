package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.service.marketplace.NotificationService;

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
        return new APIResponse(notificationService.getNotificationsForUser(SecurityContextUtils.getLoggedInUserId()));
    }

    @RequestMapping("count")
    @ResponseBody
    public APIResponse getNotificationCountForUser() {
        return new APIResponse(null, notificationService.getNotificationsCountForUser(SecurityContextUtils
                .getLoggedInUserId()));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateNotificationForUser(@RequestBody List<Notification> notifications) {
        return new APIResponse(notificationService.updateNotificationsForUser(
                SecurityContextUtils.getLoggedInUserId(),
                notifications));
    }
}