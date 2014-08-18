package com.proptiger.data.notification.processor;

import java.util.List;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

public interface NotificationMessageProcessor {
    
    public List<ForumUser> getDefaultUserList(NotificationTypePayload payload);
    
}
