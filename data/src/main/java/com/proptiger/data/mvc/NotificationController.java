package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.service.marketplace.NotificationService;

/**
 * 
 * @author azi
 * 
 */
@DisableCaching
@Controller
public class NotificationController extends BaseController {
    @Autowired
    private NotificationService notificationService;

    @ResponseBody
    @RequestMapping({ "/data/v1/entity/user/notification", "/app/v1/entity/user/notification" })
    public APIResponse getNotificationForUser(@RequestParam(required = false) Integer notificationTypeId) {
        return new APIResponse(
                notificationService.getNotificationsForUser(SecurityContextUtils.getLoggedInUserId(),notificationTypeId));
    }

    @RequestMapping({"/data/v1/entity/user/notification/count", "/app/v1/entity/user/notification/count"})
    @ResponseBody
    public APIResponse getNotificationCountForUser() {
        return new APIResponse(null, notificationService.getNotificationsCountForUser(SecurityContextUtils
                .getLoggedInUserId()));
    }

    @RequestMapping(value = "/data/v1/entity/user/notification", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateNotificationForUser(@RequestBody List<Notification> notifications) {
        return new APIResponse(notificationService.updateNotificationsForUser(
                SecurityContextUtils.getLoggedInUserId(),
                notifications));
    }
}