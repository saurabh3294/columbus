package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.util.Serializer;

@Service
@Primary
public class NotificationPrimaryKeyProcessor extends NotificationProcessor {
    private static Logger logger = LoggerFactory.getLogger(NotificationPrimaryKeyProcessor.class);

    public Object getPrimaryKeyOfNotificationMessage(NotificationMessagePayload notificationMessagePayload) {
        return notificationMessagePayload.getNotificationTypePayload().getPrimaryKeyValue();
    }

    public List<NotificationMessage> intraKeyProcessorHandler(
            Map<Object, NotificationByKeyDto> mapByObject,
            NotificationOperation nOperation) {

        for (Map.Entry<Object, NotificationByKeyDto> entry : mapByObject.entrySet()) {
            if (nOperation.equals(NotificationOperation.Merge)) {
                processIntraMerging(entry.getValue());
            }
            else {
                processIntraSuppressing(entry.getValue());

            }
        }

        return null;
    }

    public void processInterMerging(
            NotificationByKeyDto parentNotification,
            List<NotificationByKeyDto> childNotification) {
        NotificationMessage notificationMessage = parentNotification.getNotificationMessages().get(0);
        logger.debug("PARENT " + Serializer.toJson(parentNotification));
        logger.debug("CHILD " + Serializer.toJson(childNotification));
        for (NotificationByKeyDto notificationByKeyDto : childNotification) {
            merging(
                    notificationByKeyDto.getNotificationMessages(),
                    notificationByKeyDto.getNotificationGenerateds(),
                    notificationByKeyDto.getDiscardedMessage(),
                    notificationByKeyDto.getDiscardGeneratedMap(),
                    NotificationStatus.InterKeyMerged,
                    notificationMessage);
        }
    }

    public void processInterSuppressing(NotificationByKeyDto parent, NotificationByKeyDto child) {
        /**
         * four cases. parent NotificationMessage or NotificationGenerated child
         * NotificationMessage or NotificationGenerated.
         */
        suppressing(
                child.getNotificationMessages(),
                child.getNotificationGenerateds(),
                child.getDiscardedMessage(),
                child.getDiscardGeneratedMap(),
                NotificationStatus.InterKeySuppressed);
    }
    // TODO code needed as if notification message not present.
    public void processIntraMerging(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();

        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(lastMessage);

        merging(
                notificationByKey.getNotificationMessages(),
                notificationByKey.getNotificationGenerateds(),
                notificationByKey.getDiscardedMessage(),
                notificationByKey.getDiscardGeneratedMap(),
                NotificationStatus.IntraKeyMerged,
                lastMessage);

        // As last message is being processed. Hence, inserting it in the list.
        nMessages.add(lastMessage);

    }
    
    // TODO code needed as if NM not present.
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByKey.getNotificationGenerateds();
        // removing all notification Messages to be processed. Discarding them
        List<NotificationMessage> discardMessages = notificationByKey.getDiscardedMessage();
        // discarding all the current Notification Generated .
        Map<NotificationStatus, List<NotificationGenerated>> discardMap = notificationByKey.getDiscardGeneratedMap();

        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(lastMessage);

        suppressing(nMessages, nGenerateds, discardMessages, discardMap, NotificationStatus.IntrakeySuppressed);
        nMessages.add(lastMessage);
    }

}
