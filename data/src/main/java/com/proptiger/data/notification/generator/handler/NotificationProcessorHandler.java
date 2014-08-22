package com.proptiger.data.notification.generator.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.generator.NotificationGenerator;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationType.NotificationOperation;
import com.proptiger.data.notification.processor.NotificationNonPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.processor.dto.NotificationProcessorDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationProcessorDtoService;
import com.proptiger.data.notification.service.NotificationTypeService;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationProcessorHandler {
    private static Logger                   logger = LoggerFactory.getLogger(NotificationProcessorHandler.class);

    @Autowired
    private NotificationMessageService      nMessageService;

    @Autowired
    private NotificationGeneratedService    nGeneratedService;

    @Autowired
    private NotificationTypeService         notificationTypeService;

    @Autowired
    private NotificationProcessorDtoService processorDtoService;

    /**
     * 
     * @param notificationMessages
     * @return
     */
    public void handleNotificationMessage(NotificationProcessorDto processorDto) {
        Map<Integer, NotificationByTypeDto> nMap = processorDto.getNotificationByTypeDtos();
        Integer userId = processorDto.getUserId();

        // Intra Primary Key Processing
        handleIntraPrimaryKeyProcessing(nMap);
        
        logger.info(" AFTER INTRA KEY PROCESSING");
        logger.debug(Serializer.toJson(nMap));
        // Inter Primary Key Suppressing
        handleInterPrimaryKeySuppressing(nMap);
        
        logger.info(" AFTER INTER KEY SUPPRESSING");
        logger.debug(Serializer.toJson(nMap));
        
        // Inter Primary Key Merging
        handleInterPrimaryKeyMerging(nMap, userId);
        
        logger.info(" AFTER INTER KEY MERGING");
        logger.debug(Serializer.toJson(nMap));

        // converting Processor DTO from Primary Key related data to non Primary
        // key related data.
        processorDtoService.buildNonPrimaryKeyDto(processorDto);
        logger.info(" BUILDING NEW DTO ");
        logger.debug(Serializer.toJson(nMap));

        // Intra Non Primary Key Processing
        handleIntraNonPrimaryKeyProcessing(nMap);

        logger.info(" AFTER INTRA NONKEY PROCESSING");
        logger.debug(Serializer.toJson(nMap));
        
        // Inter Non Primary Key Suppressing
        handleInterNonPrimaryKeySuppressing(nMap);
        
        logger.info(" AFTER INTER NONKEY SUPPRESSING");
        logger.debug(Serializer.toJson(nMap));

        // Inter Non Primary Key Merging
        handleInterNonPrimaryKeyMerging(nMap, userId);
        
        logger.info(" AFTER INTER NONKEY MERGING");
        logger.debug(Serializer.toJson(nMap));

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

    public void handleInterNonPrimaryKeyMerging(Map<Integer, NotificationByTypeDto> nMap, Integer userId) {
        Map<Integer, List<Integer>> mergeGroup = notificationTypeService.notificationInterNonKeyMergeGroupingMap();

        NotificationByTypeDto parentNotificationByTypeDto;
        NotificationNonPrimaryKeyProcessor nNonPrimaryKeyProcessor = null;
        NotificationMessage notificationMessage = null;
        List<Integer> childNotificationTypeIds = null;
        List<NotificationByTypeDto> foundNTypeDtos = new ArrayList<NotificationByTypeDto>();

        for (Map.Entry<Integer, List<Integer>> parentChildentry : mergeGroup.entrySet()) {
            parentNotificationByTypeDto = nMap.get(parentChildentry.getKey());
            childNotificationTypeIds = parentChildentry.getValue();

            for (Integer notificationTypeId : childNotificationTypeIds) {
                if (nMap.get(notificationTypeId) != null) {
                    foundNTypeDtos.add(nMap.get(notificationTypeId));
                }
            }
            if (foundNTypeDtos.size() < 1) {
                continue;
            }

            if (parentNotificationByTypeDto == null) {
                parentNotificationByTypeDto = new NotificationByTypeDto();
                parentNotificationByTypeDto.setNotificationType(notificationTypeService.findOne(parentChildentry
                        .getKey()));
                notificationMessage = nMessageService.createNotificationMessage(parentChildentry.getKey(), userId, null);
                parentNotificationByTypeDto.getNotificationMessages().add(notificationMessage);
                nMap.put(parentChildentry.getKey(), parentNotificationByTypeDto);
            }

            nNonPrimaryKeyProcessor = parentNotificationByTypeDto.getNotificationType().getNotificationTypeConfig()
                    .getNonPrimaryKeyProcessorObject();
            nNonPrimaryKeyProcessor.processInterMerging(parentNotificationByTypeDto, foundNTypeDtos);

        }
    }

    public void handleInterNonPrimaryKeySuppressing(Map<Integer, NotificationByTypeDto> nMap) {

        /**
         * Getting the Map of Suppressing relationship between two notification
         * types based on their primary key. In Map key is of parent
         * Notification Type and value is Child Notification Type.
         */
        Map<Integer, Integer> suppressGroup = notificationTypeService
                .getNotificationInterNonPrimaryKeySupressGroupingMap();

        NotificationByTypeDto parentNotificationByTypeDto, childNotificationByTypeDto;
        NotificationType parentNotificationType;
        NotificationNonPrimaryKeyProcessor nNonPrimaryKeyProcessor = null;

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
                parentNotificationType = parentNotificationByTypeDto.getNotificationType();
                nNonPrimaryKeyProcessor = parentNotificationType.getNotificationTypeConfig()
                        .getNonPrimaryKeyProcessorObject();
                nNonPrimaryKeyProcessor
                        .processInterSuppressing(parentNotificationByTypeDto, childNotificationByTypeDto);

            }
        }

    }

    public void handleIntraNonPrimaryKeyProcessing(Map<Integer, NotificationByTypeDto> nMap) {

        NotificationNonPrimaryKeyProcessor nNonPrimaryKeyProcessor = null;
        NotificationOperation nOperation = null;
        NotificationType nType = null;
        for (Map.Entry<Integer, NotificationByTypeDto> entry : nMap.entrySet()) {
            nType = entry.getValue().getNotificationType();
            nOperation = nType.getIntraNonPrimaryKeyOperation();
            if (nOperation != null) {
                nNonPrimaryKeyProcessor = nType.getNotificationTypeConfig().getNonPrimaryKeyProcessorObject();
                nNonPrimaryKeyProcessor.intraKeyProcessorHandler(entry.getValue(), nOperation);
            }

        }
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
        Map<Integer, Integer> suppressGroup = notificationTypeService
                .getNotificationInterPrimaryKeySupressGroupingMap();

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

    public void handleInterPrimaryKeyMerging(Map<Integer, NotificationByTypeDto> nMap, Integer userId) {
        logger.info(" Handling Inter Primary Key Merging");
        Map<Integer, List<Integer>> mergeGroup = notificationTypeService.notificationInterKeyMergeGroupingMap();
        logger.debug(" INTER PRIMARY KEY GROUP "+Serializer.toJson(mergeGroup));
        
        NotificationByTypeDto parentNotificationByTypeDto;
        Map<Object, NotificationByKeyDto> parentNotificationByKeyMap;
        NotificationByKeyDto parentNotificationByKeyDto;
        NotificationPrimaryKeyProcessor nPrimaryKeyProcessor = null;
        NotificationMessage notificationMessage = null;
        List<Integer> childNotificationTypeIds = null;
        List<NotificationByTypeDto> foundNTypeDtos = new ArrayList<NotificationByTypeDto>();
        Map<Object, List<NotificationByKeyDto>> groupNotificationByKey = new LinkedHashMap<Object, List<NotificationByKeyDto>>();

        for (Map.Entry<Integer, List<Integer>> parentChildentry : mergeGroup.entrySet()) {
            parentNotificationByTypeDto = nMap.get(parentChildentry.getKey());
            childNotificationTypeIds = parentChildentry.getValue();

            for (Integer notificationTypeId : childNotificationTypeIds) {
                if (nMap.get(notificationTypeId) != null) {
                    foundNTypeDtos.add(nMap.get(notificationTypeId));
                }
            }
            logger.debug(" FOUND PARENT "+parentChildentry.getKey()+" GROUP "+ Serializer.toJson(foundNTypeDtos));
            if (foundNTypeDtos.size() < 1) {
                continue;
            }
            groupNotificationsByKey(foundNTypeDtos, groupNotificationByKey);
            logger.debug(" GROPED NOTIFICATION BY KEY "+Serializer.toJson(groupNotificationByKey));
            if (parentNotificationByTypeDto == null) {
                parentNotificationByTypeDto = new NotificationByTypeDto();
                parentNotificationByTypeDto
                        .setNotificationMessageByKeys(new LinkedHashMap<Object, NotificationByKeyDto>());
                parentNotificationByTypeDto.setNotificationType(notificationTypeService.findOne(parentChildentry
                        .getKey()));
                nMap.put(parentChildentry.getKey(), parentNotificationByTypeDto);
            }

            parentNotificationByKeyMap = parentNotificationByTypeDto.getNotificationMessageByKeys();
            nPrimaryKeyProcessor = parentNotificationByTypeDto.getNotificationType().getNotificationTypeConfig()
                    .getPrimaryKeyProcessorObject();
            for (Map.Entry<Object, List<NotificationByKeyDto>> entry : groupNotificationByKey.entrySet()) {
                parentNotificationByKeyDto = parentNotificationByKeyMap.get(entry.getKey());
                if (parentNotificationByKeyDto == null) {
                    parentNotificationByKeyDto = new NotificationByKeyDto();

                    notificationMessage = nMessageService.createNotificationMessage(parentChildentry.getKey(), userId, entry.getKey());
                    parentNotificationByKeyDto.getNotificationMessages().add(notificationMessage);
                    parentNotificationByKeyMap.put(entry.getKey(), parentNotificationByKeyDto);
                }

                nPrimaryKeyProcessor.processInterMerging(parentNotificationByKeyDto, entry.getValue());
            }

        }
    }

    private void groupNotificationsByKey(
            List<NotificationByTypeDto> notificationByTypeDtos,
            Map<Object, List<NotificationByKeyDto>> group) {

        List<NotificationByKeyDto> notificationByKeyDtos = null;
        for (NotificationByTypeDto notificationByTypeDto : notificationByTypeDtos) {
            for (Map.Entry<Object, NotificationByKeyDto> entry : notificationByTypeDto.getNotificationMessageByKeys()
                    .entrySet()) {
                notificationByKeyDtos = group.get(entry.getKey());
                if (notificationByKeyDtos == null) {
                    notificationByKeyDtos = new ArrayList<NotificationByKeyDto>();
                }
                notificationByKeyDtos.add(entry.getValue());
                group.put(entry.getKey(), notificationByKeyDtos);
            }
        }

    }
}
