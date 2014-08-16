package com.proptiger.data.notification.generator.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;

@Service
public class NotificationProcessorHandler {
    
    @Autowired
    private NotificationMessageService nMessageService;
    
    @Autowired
    private NotificationGeneratedService nGeneratedService;
    
    /**
     * 
     * @param notificationMessages
     * @return
     */
    public List<NotificationMessage> handleNotificationMessage(List<NotificationMessage> notificationMessages, List<NotificationGenerated> notificationGenerateds){
        /**
         * - intra primary key merging or suppressing
         *   -- grouping by primary key seperate for each Notification Type.
         *   -- merging and suppressing seperate for each Notification Type.
         **/
        Map<String, List<NotificationMessage>> groupMessageByNotificationTypeMap = nMessageService.groupNotificationsByNotificationType(notificationMessages);
        Map<String, List<NotificationGenerated>> groupGeneratedByNotificationMap = nGeneratedService.groupNotificationsByNotificationType(notificationGenerateds);
                
        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;
        NotificationOperation nOperation = null;
        NotificationType nType = null;
        for(Map.Entry<String, List<NotificationMessage>> entry: groupMessageByNotificationTypeMap.entrySet()){
            nType = entry.getValue().get(0).getNotificationType();
            nOperation = nType.getIntraPrimaryKeyOperation();
            if(nOperation != null){
                nPrimaryKeyProcessor = entry.getValue().get(0).getNotificationType().getNotificationTypeConfig().getPrimaryKeyProcessorObject();
                nPrimaryKeyProcessor.intraProcessorHandler(entry.getValue(), groupGeneratedByNotificationMap.get(entry.getKey()), nOperation);
            }
            
        }
        
        /**
         * 
         * - inter primary key suppressing and merging
         *   -- Suppressing
         *      --- group by suppressing Group.
         *      --- seperate them by primary key.
         *      --- then process them and suppress them.
         *   -- merging
         *      --- group by merge group.
         *      --- seperate them by primary key.
         *      --- then process them and merge them.
         *      
         * - intra non primary key suppressing or merging.
         *   -- either of them will be processed.
         *   -- suppress
         *      --- group them by type.
         *      --- then process them.
         *   -- merging
         *      --- group them by type.
         *      --- process them 
         * - inter non primary key suppressing and merging.
         *   -- suppress
         *       --- group by suppressing Group.
         *       --- then process them and suppress them.
         *   -- merging
         *      --- group by merge group.
         *      --- then process them and merge them.
         *   
         */
        
        
        return null;
    }
}
