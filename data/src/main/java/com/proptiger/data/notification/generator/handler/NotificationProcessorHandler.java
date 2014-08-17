package com.proptiger.data.notification.generator.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeService;

@Service
public class NotificationProcessorHandler {

    @Autowired
    private NotificationMessageService   nMessageService;

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    @Autowired
    private NotificationTypeService      notificationTypeService;

    /**
     * 
     * @param notificationMessages
     * @return
     */
    public void handleNotificationMessage(Map<Integer, NotificationByTypeDto> nMap) {

        // Intra Primary Key Processing
        handleIntraPrimaryKeyProcessing(nMap);

        // Inter Primary Key Suppressing
        handleInterPrimaryKeySuppressing(nMap);
        /**
         * 
         * - inter primary key suppressing and merging -- Suppressing --- group
         * by suppressing Group. --- seperate them by primary key. --- then
         * process them and suppress them. -- merging --- group by merge group.
         * --- seperate them by primary key. --- then process them and merge
         * them.
         * 
         * - intra non primary key suppressing or merging. -- either of them
         * will be processed. -- suppress --- group them by type. --- then
         * process them. -- merging --- group them by type. --- process them -
         * inter non primary key suppressing and merging. -- suppress --- group
         * by suppressing Group. --- then process them and suppress them. --
         * merging --- group by merge group. --- then process them and merge
         * them. \||||||||||||||||||||||||
         */

    }

    

    public void handleIntraPrimaryKeyProcessing(Map<Integer, NotificationByTypeDto> nMap) {
        /**
         * - intra primary key merging or suppressing -- grouping by primary key
         * seperate for each Notification Type. -- merging and suppressing
         * seperate for each Notification Type.
         **/

        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;
        NotificationOperation nOperation = null;
        NotificationType nType = null;
        for (Map.Entry<Integer, NotificationByTypeDto> entry : nMap.entrySet()) {
            nType = entry.getValue().getNotificationType();
            nOperation = nType.getIntraPrimaryKeyOperation();
            if (nOperation != null) {
                nPrimaryKeyProcessor = nType.getNotificationTypeConfig().getPrimaryKeyProcessorObject();
                nPrimaryKeyProcessor.intraKeyProcessorHandler(
                        entry.getValue().getNotificationMessageByKeys(),
                        nOperation);
            }

        }
    }

    /**
     * Getting the Map of Suppressing relationship between two notification
     * types based on their primary key.
     * 
     * @param nMap
     */
    public void handleInterPrimaryKeySuppressing(Map<Integer, NotificationByTypeDto> nMap) {
        /**
         * Getting the Map of Suppressing relationship between two notification
         * types based on their primary key. In Map key is of parent
         * Notification Type and value is Child Notification Type.
         */
        Map<Integer, Integer> suppressGroup = notificationTypeService.NotificationInterPrimaryKeySupressGroupingMap();

        NotificationByTypeDto parentNotificationByTypeDto, childNotificationByTypeDto;
        Map<Object, NotificationByKeyDto> parentNotificationByKeyMap, childNotificationByKeyMap;
        NotificationByKeyDto childNotificationByKeyDto;
        NotificationType parentNotificationType;
        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;

        /**
         * Iterating over all possible suppress groups.
         */
        for (Map.Entry<Integer, Integer> parentChildentry : suppressGroup.entrySet()) {
            /**
             * Getting the parent and child Notification Types From the existing
             * notification from the map.
             */
            parentNotificationByTypeDto = nMap.get(parentChildentry.getKey());
            childNotificationByTypeDto = nMap.get(parentChildentry.getValue());

            /**
             * Only if parent and child exists, then suppressing can be done.
             */
            if (parentNotificationByTypeDto != null && childNotificationByTypeDto != null) {
                parentNotificationByKeyMap = parentNotificationByTypeDto.getNotificationMessageByKeys();
                childNotificationByKeyMap = childNotificationByTypeDto.getNotificationMessageByKeys();
                parentNotificationType = parentNotificationByTypeDto.getNotificationType();
                nPrimaryKeyProcessor = parentNotificationType.getNotificationTypeConfig()
                        .getPrimaryKeyProcessorObject();

                /**
                 * For each primary key present in the parent Notification Type,
                 * check if that primary key is present for the child also. If
                 * present then suppress the child notifications.
                 */
                for (Map.Entry<Object, NotificationByKeyDto> entry : parentNotificationByKeyMap.entrySet()) {
                    childNotificationByKeyDto = childNotificationByKeyMap.get(entry.getKey());
                    if (childNotificationByKeyDto != null) {
                        nPrimaryKeyProcessor.processInterSuppressing(entry.getValue(), childNotificationByKeyDto);
                    }
                }
            }
        }

    }
    
    public void handleInterPrimaryKeyMerging(Map<Integer, NotificationByTypeDto> nMap) {
        Map<Integer, List<Integer>> mergeGroup = notificationTypeService.notificationInterMergeGroupingMap();
        
        NotificationByTypeDto parentNotificationByTypeDto, childNotificationByTypeDto;
        Map<Object, NotificationByKeyDto> parentNotificationByKeyMap, childNotificationByKeyMap;
        NotificationByKeyDto childNotificationByKeyDto;
        NotificationType parentNotificationType;
        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;
        
        for(Map.Entry<Integer, List<Integer>> parentChildentry:mergeGroup.entrySet()){
            parentNotificationByTypeDto = nMap.get(parentChildentry.getKey());
            if(parentNotificationByTypeDto == null){
                
            }
        }
    }
}
