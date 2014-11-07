package com.proptiger.data.notification.sender;

import com.proptiger.data.notification.model.NotificationGenerated;

public interface MediumSender {
    public boolean send(String template, NotificationGenerated nGenerated);
}
