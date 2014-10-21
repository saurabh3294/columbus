package com.proptiger.data.notification.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;

@Service
public class NotificationCreatorService {

    @Autowired
    private NotificationMessageService   notificationMessageService;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

    public List<NotificationGenerated> createNotificationGenerated(NotificationCreatorServiceRequest request) {

        List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();

        Map<String, Object> payloadMap = request.getPayloadMap();
        payloadMap.put(Tokens.Default.Subject.name(), request.getSubject());
        payloadMap.put(Tokens.Default.Body.name(), request.getBody());
        payloadMap.put(Tokens.Default.Template.name(), request.getTemplate());

        for (Integer userId : request.getUserIds()) {
            NotificationMessage message = notificationMessageService.createNotificationMessage(
                    request.getNotificationType(),
                    userId,
                    payloadMap,
                    request.getFromEmail(),
                    request.getCcList(),
                    request.getBccList());
            notificationMessages.add(message);
        }

        return notificationGeneratedService.createNotificationGenerated(notificationMessages, request.getMediumTypes());
    }
}
