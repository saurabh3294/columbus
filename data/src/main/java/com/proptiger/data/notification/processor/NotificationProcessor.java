package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;

public interface NotificationProcessor {
    public void processIntraMerging(
            NotificationByKeyDto notificationByKey);
    
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey);

    public void processInterMerging(NotificationByKeyDto parentNotification,
            List<NotificationByKeyDto> childNotification);

    public void processInterSuppressing(NotificationByKeyDto parent, NotificationByKeyDto child);

}
