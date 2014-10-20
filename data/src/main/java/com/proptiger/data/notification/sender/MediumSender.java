package com.proptiger.data.notification.sender;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;

public interface MediumSender {
    public boolean send(String template, Integer userId, NotificationGenerated nGenerated, NotificationSenderPayload payload);
}
