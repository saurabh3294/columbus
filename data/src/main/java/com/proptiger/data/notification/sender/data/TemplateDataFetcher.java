package com.proptiger.data.notification.sender.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;

@Service
public abstract class TemplateDataFetcher {

    public abstract Map<String, Object> fetchTemplateData(NotificationGenerated nGenerated);

    protected Map<String, List<NotificationMessagePayload>> getMessagePayloadsMappedByType(
            Map<String, List<NotificationMessagePayload>> payloadMap,
            NotificationMessagePayload payload) {
        if (payload == null) {
            return payloadMap;
        }
        List<NotificationMessagePayload> childPayloads = payload.getNotificationMessagePayloads();
        if (childPayloads != null) {
            for (NotificationMessagePayload childPayload : childPayloads) {
                payloadMap = getMessagePayloadsMappedByType(payloadMap, childPayload);
            }
        }
        payload.setNotificationMessagePayloads(null);
        String type = payload.getNotificationTypeName();
        List<NotificationMessagePayload> mappedPayloads = payloadMap.get(type);
        if (mappedPayloads == null) {
            mappedPayloads = new ArrayList<NotificationMessagePayload>();
        }
        mappedPayloads.add(payload);
        payloadMap.put(type, mappedPayloads);
        return payloadMap;
    }

    protected List<NotificationTypePayload> getAllChildNotificationTypePayloads(
            List<NotificationTypePayload> typePayloadList,
            NotificationTypePayload payload) {
        if (payload == null) {
            return typePayloadList;
        }
        List<NotificationTypePayload> childPayloads = payload.getChildNotificationTypePayloads();
        if (childPayloads != null) {
            for (NotificationTypePayload childPayload : childPayloads) {
                typePayloadList = getAllChildNotificationTypePayloads(typePayloadList, childPayload);
            }
        }
        payload.setChildNotificationTypePayloads(null);
        typePayloadList.add(payload);
        return typePayloadList;
    }
}
