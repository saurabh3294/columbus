package com.proptiger.data.notification.generator.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
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
    public void handleNotificationMessage(Map<Integer, NotificationByTypeDto> nMap){
        /**
         * - intra primary key merging or suppressing
         *   -- grouping by primary key seperate for each Notification Type.
         *   -- merging and suppressing seperate for each Notification Type.
         **/
                        
        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;
        NotificationOperation nOperation = null;
        NotificationType nType = null;
        for(Map.Entry<Integer, NotificationByTypeDto> entry: nMap.entrySet()){
            nType = entry.getValue().getNotificationType();
            nOperation = nType.getIntraPrimaryKeyOperation();
            if(nOperation != null){
                nPrimaryKeyProcessor = nType.getNotificationTypeConfig().getPrimaryKeyProcessorObject();
                nPrimaryKeyProcessor.intraProcessorHandler(entry.getValue().getNotificationMessageByKeys()
                        , nOperation);
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
         *   \||||||||||||||||||||||||
         */
        
        
    }
}
