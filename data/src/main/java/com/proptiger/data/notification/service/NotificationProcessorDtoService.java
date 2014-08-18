package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.processor.dto.NotificationProcessorDto;

@Service
public class NotificationProcessorDtoService {

    public List<NotificationProcessorDto> buildPrimaryKeyDto(
            List<NotificationMessage> nMessages,
            List<NotificationGenerated> nGenerateds) {
        List<NotificationProcessorDto> nIntraProcessorDtos = new ArrayList<NotificationProcessorDto>();
        Map<Integer, NotificationProcessorDto> map = buildDtoWithNMessage(nMessages, nIntraProcessorDtos);

        buildDtoWithNGenerated(map, nGenerateds);

        return nIntraProcessorDtos;
    }

    public void buildNonPrimaryKeyDto(NotificationProcessorDto processorDto) {
        List<NotificationMessage> typeMessages, keyMessages;
        List<NotificationGenerated> typeGenerateds, keyGenerateds;
        List<NotificationMessage> typeDiscardMessages, keyDiscardMessages;
        Map<NotificationStatus, List<NotificationGenerated>> typeDiscardGeneratedMap, keyDiscardGeneratedMap;

        NotificationByTypeDto byTypeDto;
        NotificationByKeyDto byKeyDto;

        for (Map.Entry<Integer, NotificationByTypeDto> entry : processorDto.getNotificationByTypeDtos().entrySet()) {
            byTypeDto = entry.getValue();
            typeMessages = byTypeDto.getNotificationMessages();
            typeGenerateds = byTypeDto.getNotificationGenerateds();
            typeDiscardMessages = byTypeDto.getDiscardedMessage();
            typeDiscardGeneratedMap = byTypeDto.getDiscardGeneratedMap();

            for (Map.Entry<Object, NotificationByKeyDto> keyEntry : entry.getValue().getNotificationMessageByKeys()
                    .entrySet()) {
                byKeyDto = keyEntry.getValue();
                keyMessages = byKeyDto.getNotificationMessages();
                keyGenerateds = byKeyDto.getNotificationGenerateds();
                keyDiscardMessages = byKeyDto.getDiscardedMessage();
                keyDiscardGeneratedMap = byKeyDto.getDiscardGeneratedMap();

                typeMessages.addAll(keyMessages);
                typeGenerateds.addAll(keyGenerateds);
                typeDiscardMessages.addAll(keyDiscardMessages);
                typeDiscardGeneratedMap.putAll(keyDiscardGeneratedMap);

                keyMessages.clear();
                keyMessages.clear();
                keyDiscardMessages.clear();
                keyDiscardGeneratedMap.clear();
            }
        }

    }

    private void buildDtoWithNGenerated(
            Map<Integer, NotificationProcessorDto> map,
            List<NotificationGenerated> nGeneratedList) {

        NotificationType nType = null;
        NotificationPrimaryKeyProcessor nKeyProcessor = null;
        Object primaryKeyValue = null;

        for (NotificationGenerated nGenerated : nGeneratedList) {
            nType = nGenerated.getNotificationType();
            nKeyProcessor = nType.getNotificationTypeConfig().getPrimaryKeyProcessorObject();
            primaryKeyValue = nKeyProcessor.getPrimaryKeyOfNotificationMessage(nGenerated
                    .getNotificationMessagePayload());

            try {
                map.get(nGenerated.getForumUser().getUserId()).getNotificationByTypeDtos().get(nType.getName())
                        .getNotificationMessageByKeys().get(primaryKeyValue).getNotificationGenerateds()
                        .add(nGenerated);
            }
            catch (NullPointerException e) {
                continue;
            }

        }
    }

    private Map<Integer, NotificationProcessorDto> buildDtoWithNMessage(
            List<NotificationMessage> nMessages,
            List<NotificationProcessorDto> nIntraProcessorDtoList) {
        Map<Integer, NotificationProcessorDto> map = new LinkedHashMap<Integer, NotificationProcessorDto>();

        Map<Integer, NotificationByTypeDto> typeMap = null;
        Map<Object, NotificationByKeyDto> keyMap = null;
        NotificationProcessorDto notificationIntraProcessorDto = null;

        NotificationByTypeDto nByTypeDto = null;
        NotificationByKeyDto nByKey = null;
        NotificationType nType = null;
        NotificationPrimaryKeyProcessor nKeyProcessor = null;
        Object primaryKeyValue = null;

        for (NotificationMessage nMessage : nMessages) {
            nType = nMessage.getNotificationType();
            nKeyProcessor = nType.getNotificationTypeConfig().getPrimaryKeyProcessorObject();
            primaryKeyValue = nKeyProcessor
                    .getPrimaryKeyOfNotificationMessage(nMessage.getNotificationMessagePayload());

            notificationIntraProcessorDto = map.get(nMessage.getForumUser().getUserId());
            if (notificationIntraProcessorDto == null) {
                notificationIntraProcessorDto = new NotificationProcessorDto();
                notificationIntraProcessorDto.setUserId(nMessage.getForumUser().getUserId());
                nIntraProcessorDtoList.add(notificationIntraProcessorDto);
                map.put(nMessage.getForumUser().getUserId(), notificationIntraProcessorDto);
            }
            typeMap = notificationIntraProcessorDto.getNotificationByTypeDtos();

            nByTypeDto = typeMap.get(nType.getName());
            if (nByTypeDto == null) {
                nByTypeDto = new NotificationByTypeDto();
                nByTypeDto.setNotificationType(nType);
                ;
                typeMap.put(nType.getId(), nByTypeDto);
            }

            keyMap = nByTypeDto.getNotificationMessageByKeys();

            nByKey = keyMap.get(primaryKeyValue);
            if (nByKey == null) {
                nByKey = new NotificationByKeyDto();
                nByKey.setObjectId(primaryKeyValue);
                keyMap.put(primaryKeyValue, nByKey);
            }
            nByKey.getNotificationMessages().add(nMessage);
        }

        return map;
    }

}