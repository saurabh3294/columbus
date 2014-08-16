package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;

@Service
public class NotificationPrimaryKeyProcessor implements NotificationProcessor{
    
    public Object getPrimaryKeyOfNotificationMessage(NotificationMessagePayload notificationMessagePayload){
        return notificationMessagePayload.getNotificationTypePayload().getPrimaryKeyValue();
    }
    
    public Map<Object, List<NotificationMessage>> groupingIntraNotificationByPrimaryKey(List<NotificationMessage> notificationMessageList) {
        Map<Object, List<NotificationMessage>> groupMap = new HashMap<Object, List<NotificationMessage>>();
        Object objectId = null;
        List<NotificationMessage> groupedMessage = null;
        
        for(NotificationMessage nMessage : notificationMessageList){
            objectId = getPrimaryKeyOfNotificationMessage(nMessage.getNotificationMessagePayload());
            groupedMessage = groupMap.get(objectId);
            
            if(groupedMessage == null){
                groupedMessage = new ArrayList<NotificationMessage>();
            }
            groupedMessage.add(nMessage);
            groupMap.put(objectId, groupedMessage);
        }
        
        return groupMap;
    }
    
    public Map<Object, List<NotificationGenerated>> groupingIntraNotificationGeneratedByPrimaryKey(List<NotificationGenerated> nGeneratedList) {
        Map<Object, List<NotificationGenerated>> groupMap = new HashMap<Object, List<NotificationGenerated>>();
        Object objectId = null;
        List<NotificationGenerated> groupedGenerated = null;
        
        for(NotificationGenerated nGenerated : nGeneratedList){
            objectId = getPrimaryKeyOfNotificationMessage(nGenerated.getNotificationMessagePayload());
            groupedGenerated = groupMap.get(objectId);
            
            if(groupedGenerated == null){
                groupedGenerated = new ArrayList<NotificationGenerated>();
            }
            groupedGenerated.add(nGenerated);
            groupMap.put(objectId, groupedGenerated);
        }
        
        return groupMap;
    }
    
    public List<NotificationMessage> intraProcessorHandler(Map<Object, NotificationByKeyDto> mapByObject, NotificationOperation nOperation){
                
        List<NotificationMessage> discardNMessage = new ArrayList<NotificationMessage>();
        for(Map.Entry<Object, NotificationByKeyDto> entry:mapByObject.entrySet())
        {
            if( nOperation.equals(NotificationOperation.Merge) ){
                processIntraMerging(entry.getValue());
            }
            else{
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
    public List<NotificationMessage> processInterSuppressing(
            List<NotificationMessage> notificationMessages,
            Map<NotificationType, List<NotificationGenerated>> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void processIntraMerging(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByKey.getNotificationGenerateds();
        
        NotificationMessage  lastMessage = nMessages.get(nMessages.size() -1 );
        nMessages.remove(lastMessage);
        List<NotificationMessagePayload> mergePayload = lastMessage.getNotificationMessagePayload().getNotificationMessagePayloads();
        
        // removing all notification Messages to be processed. Discarding them
        List<NotificationMessage> discardMessages = notificationByKey.getDiscardedMessage();
        for(NotificationMessage nMessage:nMessages){
            nMessage.setNotificationStatus(NotificationStatus.Merged);
            discardMessages.add(nMessage);
            mergePayload.add(nMessage.getNotificationMessagePayload());
        }
        
        // discarding all the current Notification Generated .
        Map<NotificationStatus, List<NotificationGenerated>> discardMap = notificationByKey.getDiscardGeneratedMap();
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for(NotificationGenerated nGenerated: nGenerateds){
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if(discardGenerated == null){
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(NotificationStatus.Merged);
            discardGenerated.add(nGenerated);
            mergePayload.add(nGenerated.getNotificationMessagePayload());
            discardMap.put(currentStatus, discardGenerated);
        }
        
    }

    @Override
    public void processIntraSuppressing(NotificationByKeyDto notificationByKey) {
        List<NotificationMessage> nMessages = notificationByKey.getNotificationMessages();
        List<NotificationGenerated> nGenerateds = notificationByKey.getNotificationGenerateds();
        
        NotificationMessage  lastMessage = nMessages.get(nMessages.size() -1 );
        nMessages.remove(lastMessage);
        
        // removing all notification Messages to be processed. Discarding them
        List<NotificationMessage> discardMessages = notificationByKey.getDiscardedMessage();
        for(NotificationMessage nMessage:nMessages){
            nMessage.setNotificationStatus(NotificationStatus.Suppressed);
            discardMessages.add(nMessage);
        }
                
        // discarding all the current Notification Generated .
        Map<NotificationStatus, List<NotificationGenerated>> discardMap = notificationByKey.getDiscardGeneratedMap();
        NotificationStatus currentStatus = null;
        List<NotificationGenerated> discardGenerated = null;
        for(NotificationGenerated nGenerated: nGenerateds){
            currentStatus = nGenerated.getNotificationStatus();
            discardGenerated = discardMap.get(currentStatus);
            if(discardGenerated == null){
                discardGenerated = new ArrayList<NotificationGenerated>();
            }
            nGenerated.setNotificationStatus(NotificationStatus.Suppressed);
            discardGenerated.add(nGenerated);
            
            discardMap.put(currentStatus, discardGenerated);
        }
        
    }
    

}
