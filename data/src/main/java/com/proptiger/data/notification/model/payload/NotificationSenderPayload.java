package com.proptiger.data.notification.model.payload;

import com.proptiger.data.notification.model.NotificationGenerated;

public interface NotificationSenderPayload {
    
    public NotificationSenderPayload populatePayload(NotificationGenerated nGenerated);
}
