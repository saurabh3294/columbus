package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.processor.dto.NotificationIntraProcessorDto;

@Service
public class NotificationIntraProcessorDtoService {

    public List<NotificationIntraProcessorDto> buildDto(List<NotificationMessage> nMessages, List<NotificationGenerated> nGenerateds) {
        List<NotificationIntraProcessorDto> nIntraProcessorDtos = new ArrayList<NotificationIntraProcessorDto>();
        Map<Integer, NotificationIntraProcessorDto> map = buildDtoWithNMessage(nMessages, nIntraProcessorDtos);
        
        buildDtoWithNGenerated(map, nGenerateds);
        
        return nIntraProcessorDtos;
    }

    private void buildDtoWithNGenerated(
            Map<Integer, NotificationIntraProcessorDto> map,
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

    private Map<Integer, NotificationIntraProcessorDto> buildDtoWithNMessage(
            List<NotificationMessage> nMessages,
            List<NotificationIntraProcessorDto> nIntraProcessorDtoList) {
        Map<Integer, NotificationIntraProcessorDto> map = new LinkedHashMap<Integer, NotificationIntraProcessorDto>();

        Map<Integer, NotificationByTypeDto> typeMap = null;
        Map<Object, NotificationByKeyDto> keyMap = null;
        NotificationIntraProcessorDto notificationIntraProcessorDto = null;

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
                notificationIntraProcessorDto = new NotificationIntraProcessorDto();
                notificationIntraProcessorDto.setUserId(nMessage.getForumUser().getUserId());
                nIntraProcessorDtoList.add(notificationIntraProcessorDto);
                map.put(nMessage.getForumUser().getUserId(), notificationIntraProcessorDto);
            }
            typeMap = notificationIntraProcessorDto.getNotificationByTypeDtos();

            nByTypeDto = typeMap.get(nType.getName());
            if (nByTypeDto == null) {
                nByTypeDto = new NotificationByTypeDto();
                nByTypeDto.setNotificationType(nType);;
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
