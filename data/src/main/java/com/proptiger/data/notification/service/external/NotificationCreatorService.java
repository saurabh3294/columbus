package com.proptiger.data.notification.service.external;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.user.User;
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
     * Creates notification in the Database which will be later scheduled and
     * sent to the user.
     * 
     * @param request
     * @return
     */
    public List<NotificationGenerated> createNotificationGenerated(NotificationCreatorServiceRequest request) {

        List<NotificationMessage> notificationMessages = new ArrayList<NotificationMessage>();

        for (User user : request.getUsers()) {
            NotificationMessage message = notificationMessageService.createNotificationMessage(
                    request.getNotificationType(),
                    user.getId(),
                    request.getPayloadMap());
            notificationMessages.add(message);
        }

        return notificationGeneratedService.createNotificationGenerated(notificationMessages, request.getMediumTypes());
    }
}
