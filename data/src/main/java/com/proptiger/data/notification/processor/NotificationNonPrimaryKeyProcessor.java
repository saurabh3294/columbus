package com.proptiger.data.notification.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.generator.handler.NotificationProcessorHandler;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.service.NotificationMessageService;

@Service
@Primary
public class NotificationNonPrimaryKeyProcessor extends NotificationProcessor {
    private static Logger              logger = LoggerFactory.getLogger(NotificationNonPrimaryKeyProcessor.class);

    @Autowired
    private NotificationMessageService nMessageService;

    public void intraKeyProcessorHandler(NotificationByTypeDto notificationByTypeDto, NotificationOperation nOperation) {

        if (nOperation.equals(NotificationOperation.Merge)) {
            processIntraMerging(notificationByTypeDto);
        }
        else {
            processIntraSuppressing(notificationByTypeDto);
        }

    }

    public void processIntraMerging(NotificationByTypeDto notificationByType) {
        List<NotificationMessage> nMessages = notificationByType.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByType.getNotificationGenerateds();

        NotificationMessage lastMessage = null;
        if(nMessages != null && !nMessages.isEmpty()){
            lastMessage = nMessages.get(nMessages.size() - 1);
            nMessages.remove(nMessages.size() - 1);
        }
        /*
         * Only if number of NGenerated is > 1 then merging is required.
         */
        else if (nGenerateds.size() > 1) {
            NotificationGenerated nGenerated = nGenerateds.get(0);
            lastMessage = nMessageService.createNotificationMessage(
                    nGenerated.getNotificationType().getId(),
                    nGenerated.getUserId(),
                    NotificationProcessorHandler.nonPrimaryKeyMergingValue);
        }
        else{
            return;
        }
        

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
        suppressing(
                child.getNotificationMessages(),
                child.getNotificationGenerateds(),
                child.getDiscardedMessage(),
                child.getDiscardGeneratedMap(),
                NotificationStatus.InterNonKeySuppressed);

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
