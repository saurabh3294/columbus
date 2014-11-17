package com.proptiger.data.notification.processor;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public abstract class NotificationTypeProcessor {

    public NotificationTypePayload getNotificationTypePayload(
            EventGenerated eventGenerated,
            NotificationType notificationType) {

        NotificationTypePayload payload = notificationType.getNotificationTypeConfig()
                .getNotificationTypePayloadObject();
        EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
        payload.setPrimaryKeyName(eventTypePayload.getPrimaryKeyName());
        payload.setPrimaryKeyValue(eventTypePayload.getPrimaryKeyValue());
        payload.populatePayloadValues(eventTypePayload);
        return payload;
    }
}
