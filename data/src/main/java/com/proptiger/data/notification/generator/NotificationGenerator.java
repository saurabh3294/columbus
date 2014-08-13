package com.proptiger.data.notification.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.pojo.LimitOffsetPageRequest;

@Service
public class NotificationGenerator {
    @Autowired
    private NotificationMessageService notificationMessageService;
    
    public Integer generateNotifications(){
        // TODO to handle the pageable condition.
        List<NotificationMessage> notificationMessages = notificationMessageService.getRawNotificationMessages(new LimitOffsetPageRequest(0,1)); 
        
        return null;
    }
}