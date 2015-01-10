package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;

public class NotificationProcessor {
    @Autowired
    protected NotificationMessageService   notificationMessageService;

    @Autowired
    protected NotificationGeneratedService notificationGeneratedService;

    // TODO to handle the code when NM is null.
    protected void merging(
            List<NotificationMessage> nMessages,
            List<NotificationGenerated> nGenerateds,
            List<NotificationMessage> discardMessages,
            Map<NotificationStatus, List<NotificationGenerated>> discardMap,
            NotificationStatus mergeNotificationStatus,
            NotificationMessage parentMessage) {

        List<NotificationMessagePayload> mergePayload = parentMessage.getNotificationMessagePayload()
                .getNotificationMessagePayloads();

        // removing all notification Messages to be processed. Discarding them
        for (NotificationMessage nMessage : nMessages) {
            nMessage.setNotificationStatus(mergeNotificationStatus);
            discardMessages.add(nMessage);
            mergePayload.add(nMessage.getNotificationMessagePayload());
            notificationMessageService.addNotificationMessageUpdateHistory(nMessage, mergeNotificationStatus);
        }
        // As all the messages are being discarded. Hence, moved all of them to
        // discarded list.
        // As last message is being processed. Hence, inserting it in the list.
        nMessages.clear();

        // discarding all the current Notification Generated .
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for (NotificationGenerated nGenerated : nGenerateds) {
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if (discardGenerated == null) {
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(mergeNotificationStatus);
            discardGenerated.add(nGenerated);
            mergePayload.add(nGenerated.getNotificationMessagePayload());
            discardMap.put(currentStatus, discardGenerated);

            notificationGeneratedService.addNotificationGeneratedUpdateHistory(
                    nGenerated,
                    NotificationStatus.IntraKeyMerged);
        }
        nGenerateds.clear();

    }

    // TODO to handle the code when NM is null
    protected void suppressing(
            List<NotificationMessage> nMessages,
            List<NotificationGenerated> nGenerateds,
            List<NotificationMessage> discardMessages,
            Map<NotificationStatus, List<NotificationGenerated>> discardMap,
            NotificationStatus suppressNotificationStatus) {
        
        // removing all notification Messages to be processed. Discarding them
        for (NotificationMessage nMessage : nMessages) {
            nMessage.setNotificationStatus(suppressNotificationStatus);
            discardMessages.add(nMessage);
            notificationMessageService.addNotificationMessageUpdateHistory(
                    nMessage,
                    suppressNotificationStatus);
        }
        // As all the messages are being discarded. Hence, moved all of them to
        // discarded list.
        // As last message is being processed. Hence, inserting it in the list.
        nMessages.clear();

        // discarding all the current Notification Generated .
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for (NotificationGenerated nGenerated : nGenerateds) {
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if (discardGenerated == null) {
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(suppressNotificationStatus);
            discardGenerated.add(nGenerated);

            discardMap.put(currentStatus, discardGenerated);

            notificationGeneratedService.addNotificationGeneratedUpdateHistory(
                    nGenerated,
                    suppressNotificationStatus);
        }
        nGenerateds.clear();
    }
}
