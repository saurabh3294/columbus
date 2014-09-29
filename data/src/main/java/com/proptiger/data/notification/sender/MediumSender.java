package com.proptiger.data.notification.sender;

import com.proptiger.data.notification.model.payload.NotificationSenderPayload;

public interface MediumSender {
    public boolean send(String template, Integer userId, String notificationTypeName, NotificationSenderPayload payload);
}
