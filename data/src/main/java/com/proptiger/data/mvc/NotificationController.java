package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
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
public class NotificationController extends BaseController {
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/data/v1/entity/user/notification")
    @ResponseBody
    public APIResponse getNotificationForUser() {
        return new APIResponse(notificationService.getNotificationsForUser(Integer.parseInt(SecurityContextUtils
                .getLoggedInUser().getUserId())));
    }
}