package com.proptiger.data.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.service.AbstractTest;

public class NotificationTest extends AbstractTest {

    @Autowired
    private NotificationInitiator        notificationInitiator;

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    // @Test
    public void testNotificationTypeGenerator() {
        notificationInitiator.notificationTypeGenerator();
    }

    // @Test
    public void testNotificationGenerator() {
        notificationInitiator.notificationGenerator();
    }

    // @Test
    public void createNotification() {
        logger.info("createNotification started");
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Email);
        mediumTypes.add(MediumType.Android);
        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(getNotificationMessage(1211884, "This is a sample template for ABC"));
        nMessages.add(getNotificationMessage(1211883, "This is a sample template for XYZ"));

        List<NotificationGenerated> notificationGenerateds = nGeneratedService.createNotificationGenerated(
                nMessages,
                mediumTypes);
        logger.info("createNotification ended count: " + notificationGenerateds.size());
    }

    private NotificationMessage getNotificationMessage(Integer userId, String template) {
        NotificationMessage nMessage = new NotificationMessage();
        nMessage.setUserId(userId);
        NotificationMessagePayload payload = new NotificationMessagePayload();
        Map<String, Object> extraAttributes = new HashMap<String, Object>();
        extraAttributes.put(Tokens.Template.name(), template);
        payload.setExtraAttributes(extraAttributes);
        nMessage.setNotificationMessagePayload(payload);
        return nMessage;
    }
}
