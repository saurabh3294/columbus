package com.proptiger.data.notification.sender.data;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Service
public class DefaultTemplateDataFetcher extends TemplateDataFetcher {

    public Map<String, Object> fetchTemplateData(NotificationGenerated nGenerated) {
        NotificationMessagePayload payload = nGenerated.getNotificationMessagePayload();
        return payload.getExtraAttributes();
    }

}
