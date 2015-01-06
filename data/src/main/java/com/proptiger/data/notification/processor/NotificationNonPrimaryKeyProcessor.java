package com.proptiger.data.notification.processor;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;

@Service
@Primary
public class NotificationNonPrimaryKeyProcessor extends NotificationProcessor {

    public void intraKeyProcessorHandler(NotificationByTypeDto notificationByTypeDto, NotificationOperation nOperation) {

        if (nOperation.equals(NotificationOperation.Merge)) {
            processIntraMerging(notificationByTypeDto);
        }
        else {
            processIntraSuppressing(notificationByTypeDto);
        }

    }
    
    // Handle If NM not present.
    public void processIntraMerging(NotificationByTypeDto notificationByType) {
        List<NotificationMessage> nMessages = notificationByType.getNotificationMessages();

        if (nMessages.size() < 1) {
            return;
        }
        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(nMessages.size() - 1);

        merging(
                nMessages,
                notificationByType.getNotificationGenerateds(),
                notificationByType.getDiscardedMessage(),
                notificationByType.getDiscardGeneratedMap(),
                NotificationStatus.IntraNonKeyMerged,
                lastMessage);
        
        nMessages.add(lastMessage);
    }
    
    // TODO to handle IF NM not present.
    public void processIntraSuppressing(NotificationByTypeDto notificationByType) {
        List<NotificationMessage> nMessages = notificationByType.getNotificationMessages();

        if (nMessages.size() < 1) {
            return;
        }
        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(nMessages.size() - 1);

        suppressing(
                nMessages,
                notificationByType.getNotificationGenerateds(),
                notificationByType.getDiscardedMessage(),
                notificationByType.getDiscardGeneratedMap(),
                NotificationStatus.IntraNonKeySuppressed);
        nMessages.add(lastMessage);
    }

    public void processInterSuppressing(NotificationByTypeDto parent, NotificationByTypeDto child) {
        suppressing(child.getNotificationMessages(), child.getNotificationGenerateds(), child.getDiscardedMessage(), child.getDiscardGeneratedMap(), NotificationStatus.InterNonKeySuppressed);

    }

    public void processInterMerging(
            NotificationByTypeDto parentNotification,
            List<NotificationByTypeDto> childNotification) {
        NotificationMessage notificationMessage = parentNotification.getNotificationMessages().get(0);

        for (NotificationByTypeDto notificationByKeyDto : childNotification) {
            merging(
                    notificationByKeyDto.getNotificationMessages(),
                    notificationByKeyDto.getNotificationGenerateds(),
                    notificationByKeyDto.getDiscardedMessage(),
                    notificationByKeyDto.getDiscardGeneratedMap(),
                    NotificationStatus.InterNonKeyMerged,
                    notificationMessage);
        }

    }

}
