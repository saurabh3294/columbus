package com.proptiger.data.notification.service.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.external.NotificationCreatorServiceRequest;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;

@Service
public class NotificationCreatorService {

    @Autowired
    private NotificationMessageService   notificationMessageService;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

    /**
     * Creates notification in the Database which will be later scheduled and sent
     * to the user.
     * 
     * @param request
     * @return
     */
    public List<NotificationGenerated> createNotificationGenerated(NotificationCreatorServiceRequest request) {

        List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();

        Map<String, Object> payloadMap = request.getPayloadMap();
        payloadMap.put(Tokens.Default.Subject.name(), request.getEmailAttributes().getSubject());
        payloadMap.put(Tokens.Default.Body.name(), request.getEmailAttributes().getBody());
        payloadMap.put(Tokens.Default.Template.name(), request.getTemplate());

        for (User user : request.getUsers()) {
            NotificationMessage message = notificationMessageService.createNotificationMessage(
                    request.getNotificationType(),
                    user.getId(),
                    payloadMap,
                    request.getEmailAttributes().getFromEmail(),
                    request.getEmailAttributes().getCcList(),
                    request.getEmailAttributes().getBccList());
            notificationMessages.add(message);
        }

        return notificationGeneratedService.createNotificationGenerated(notificationMessages, request.getMediumTypes());
    }
}
