package com.proptiger.data.notification.processor;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.payload.NewsEventTypePayload;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public class NewsNotificationTypeProcessor extends NotificationTypeProcessor {

    public NotificationTypePayload getNotificationTypePayload(
            EventGenerated eventGenerated,
            NotificationType notificationType) {

        NotificationTypePayload payload = notificationType.getNotificationTypeConfig()
                .getNotificationTypePayloadObject();
        EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
        payload.populatePayloadValues((NewsEventTypePayload) eventTypePayload);
        return payload;
    }
}
