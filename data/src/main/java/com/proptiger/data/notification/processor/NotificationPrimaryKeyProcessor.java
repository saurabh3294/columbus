package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;

@Service
public class NotificationPrimaryKeyProcessor implements NotificationProcessor {

    @Autowired
    private NotificationMessageService   notificationMessageService;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

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

    @Override
    public List<NotificationMessage> processInterMerging(
            List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void processInterSuppressing(NotificationByKeyDto parent, NotificationByKeyDto child) {
        /**
         *  four cases. parent NotificationMessage or NotificationGenerated
         *  child NotificationMessage or NotificationGenerated.
         */
        // suppressing all the child notification Message 
        List<NotificationMessage> discardMessageList = child.getDiscardedMessage();
        Iterator<NotificationMessage> itM = child.getNotificationMessages().iterator();
        NotificationMessage nMessage = null;
        while(itM.hasNext()){
            nMessage = itM.next();
            notificationMessageService.addNotificationMessageUpdateHistory(nMessage, NotificationStatus.InterKeySuppressed);
            nMessage.setNotificationStatus(NotificationStatus.InterKeySuppressed);
            discardMessageList.add(nMessage);
            itM.remove();
        }
        
        // suppressing all the child Notification Generated.
        Iterator<NotificationGenerated> itG = child.getNotificationGenerateds().iterator();
        NotificationGenerated nGenerated = null;
        Map<NotificationStatus, List<NotificationGenerated>> discardGeneratedMap = child.getDiscardGeneratedMap();
        List<NotificationGenerated> discardGeneratedList = null;
        while(itG.hasNext()){
            nGenerated = itG.next();
            notificationGeneratedService.addNotificationGeneratedUpdateHistory(nGenerated, NotificationStatus.InterKeySuppressed);
            nGenerated.setNotificationStatus(NotificationStatus.InterKeySuppressed);
            
            discardGeneratedList = discardGeneratedMap.get(nGenerated.getNotificationStatus());
            if(discardGeneratedList == null){
                discardGeneratedList = new ArrayList<NotificationGenerated>();
            }
            discardGeneratedList.add(nGenerated);
            discardGeneratedMap.put(nGenerated.getNotificationStatus(), discardGeneratedList);
            itG.remove();
        }
        
    }

    @Override
    public void processIntraMerging(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByKey.getNotificationGenerateds();

        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(lastMessage);
        List<NotificationMessagePayload> mergePayload = lastMessage.getNotificationMessagePayload()
                .getNotificationMessagePayloads();

        // removing all notification Messages to be processed. Discarding them
        List<NotificationMessage> discardMessages = notificationByKey.getDiscardedMessage();
        for (NotificationMessage nMessage : nMessages) {
            nMessage.setNotificationStatus(NotificationStatus.IntraKeyMerged);
            discardMessages.add(nMessage);
            mergePayload.add(nMessage.getNotificationMessagePayload());
            notificationMessageService.addNotificationMessageUpdateHistory(nMessage, NotificationStatus.InterKeyMerged);
        }
        // As all the messages are being discarded. Hence, moved all of them to
        // discarded list.
        // As last message is being processed. Hence, inserting it in the list.
        nMessages.clear();
        nMessages.add(lastMessage);

        // discarding all the current Notification Generated .
        Map<NotificationStatus, List<NotificationGenerated>> discardMap = notificationByKey.getDiscardGeneratedMap();
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for (NotificationGenerated nGenerated : nGenerateds) {
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if (discardGenerated == null) {
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(NotificationStatus.IntraKeyMerged);
            discardGenerated.add(nGenerated);
            mergePayload.add(nGenerated.getNotificationMessagePayload());
            discardMap.put(currentStatus, discardGenerated);

            notificationGeneratedService.addNotificationGeneratedUpdateHistory(
                    nGenerated,
                    NotificationStatus.IntraKeyMerged);
        }
        nGenerateds.clear();

    }

    @Override
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByKey.getNotificationGenerateds();

        NotificationMessage lastMessage = nMessages.get(nMessages.size() - 1);
        nMessages.remove(lastMessage);

        // removing all notification Messages to be processed. Discarding them
        List<NotificationMessage> discardMessages = notificationByKey.getDiscardedMessage();
        for (NotificationMessage nMessage : nMessages) {
            nMessage.setNotificationStatus(NotificationStatus.IntrakeySuppressed);
            discardMessages.add(nMessage);
            notificationMessageService.addNotificationMessageUpdateHistory(
                    nMessage,
                    NotificationStatus.IntrakeySuppressed);
        }
        // As all the messages are being discarded. Hence, moved all of them to
        // discarded list.
        // As last message is being processed. Hence, inserting it in the list.
        nMessages.clear();
        nMessages.add(lastMessage);

        // discarding all the current Notification Generated .
        Map<NotificationStatus, List<NotificationGenerated>> discardMap = notificationByKey.getDiscardGeneratedMap();
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for (NotificationGenerated nGenerated : nGenerateds) {
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if (discardGenerated == null) {
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(NotificationStatus.IntrakeySuppressed);
            discardGenerated.add(nGenerated);

            discardMap.put(currentStatus, discardGenerated);

            notificationGeneratedService.addNotificationGeneratedUpdateHistory(
                    nGenerated,
                    NotificationStatus.IntrakeySuppressed);
        }
        nGenerateds.clear();
    }

}
