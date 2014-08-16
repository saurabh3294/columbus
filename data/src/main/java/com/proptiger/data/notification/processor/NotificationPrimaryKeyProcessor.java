package com.proptiger.data.notification.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.model.legacy.NotificationPayloadOld;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

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
    
    public List<NotificationMessage> intraProcessorHandler(List<NotificationMessage> nMessages, List<NotificationGenerated> nGenerateds, NotificationOperation nOperation){
        Map<Object, List<NotificationMessage>> keyGroupMessages = groupingIntraNotificationByPrimaryKey(nMessages);
        Map<Object, List<NotificationGenerated>> keyGroupGenerated = groupingIntraNotificationGeneratedByPrimaryKey(nGenerateds);
        
        List<NotificationMessage> discardNMessage = new ArrayList<NotificationMessage>();
        for(Map.Entry<Object, List<NotificationMessage>> entry:keyGroupMessages.entrySet())
        {
            if( nOperation.equals(NotificationOperation.Merge) ){
                processIntraMerging( entry.getValue(), keyGroupGenerated.get(entry.getKey()));
            }
            else{
                processIntraSuppressing( entry.getValue(), keyGroupGenerated.get(entry.getKey()));
            }    
        }
        
        return null;
    }

    @Override
    public List<NotificationMessage> processIntraMerging(
            List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NotificationMessage> processIntraSuppressing(
            List<NotificationMessage> notificationMessages,
            List<NotificationGenerated> generatedNotifications) {
        // TODO Auto-generated method stub
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
    

}
