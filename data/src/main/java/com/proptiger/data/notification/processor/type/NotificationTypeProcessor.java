package com.proptiger.data.notification.processor.type;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public abstract class NotificationTypeProcessor {

    public NotificationTypePayload getNotificationTypePayload(
            EventGenerated eventGenerated,
            NotificationType notificationType) {

        EventTypePayload eventTypePayload = eventGenerated.getEventTypePayload();
        return getNotificationTypePayload(eventTypePayload);
    }

    private NotificationTypePayload getNotificationTypePayload(EventTypePayload eventTypePayload) {

        NotificationTypePayload payload = new NotificationTypePayload();
        payload.populatePayloadValues((DefaultEventTypePayload) eventTypePayload);

        if (eventTypePayload.getChildEventTypePayloads() != null) {
            List<NotificationTypePayload> childPayloads = new ArrayList<NotificationTypePayload>();
            for (EventTypePayload childEventTypePayload : eventTypePayload.getChildEventTypePayloads()) {
                childPayloads.add(getNotificationTypePayload(childEventTypePayload));
            }
            payload.setChildNotificationTypePayloads(childPayloads);
        }

        return payload;
    }

}
