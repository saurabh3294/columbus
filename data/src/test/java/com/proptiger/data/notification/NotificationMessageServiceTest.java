package com.proptiger.data.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeService;
import com.proptiger.data.service.AbstractTest;

/**
 * 
 * @author sahil
 * 
 */
public class NotificationMessageServiceTest extends AbstractTest {

    @Autowired
    private NotificationMessageService nMessageService;

    @Autowired
    private NotificationMockerService  notificationMockerService;

    @Test
    public void testCreateNotificationMessageForEmail() {
        Integer userId = 5435;
        String subject = "Mock Subject";
        String body = "Mock Body";

        NotificationType notificationType = notificationMockerService.getMockNotificationType();

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findDefaultNotificationType()).thenReturn(notificationType);
        nMessageService.setNotiTypeService(notificationTypeService);

        NotificationMessage message = nMessageService.createNotificationMessage(userId, subject, body);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getUserId(), userId);
        Assert.assertNotNull(message.getNotificationType());
        Assert.assertNotNull(message.getNotificationType().getName());
        Assert.assertEquals(message.getNotificationType().getName(), notificationType.getName());
        Assert.assertNotNull(message.getNotificationMessagePayload());
        
        Map<String, Object> extraAttributes = message.getNotificationMessagePayload().getExtraAttributes();
        Assert.assertEquals((String)extraAttributes.get(Tokens.Subject.name()), subject);
        Assert.assertEquals((String)extraAttributes.get(Tokens.Body.name()), body);
    }

    @Test
    public void testCreateNotificationMessageForMobile() {

        NotificationType notificationType = notificationMockerService.getMockNotificationType();
        String typeName = notificationType.getName();
        Integer userId = 5435;
        String template = "Mock Template";

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findByName(typeName)).thenReturn(notificationType);
        nMessageService.setNotiTypeService(notificationTypeService);

        NotificationMessage message = nMessageService.createNotificationMessage(typeName, userId, template);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getUserId(), userId);
        Assert.assertNotNull(message.getNotificationType());
        Assert.assertNotNull(message.getNotificationType().getName());
        Assert.assertEquals(message.getNotificationType().getName(), typeName);
        Assert.assertNotNull(message.getNotificationMessagePayload());
        
        Map<String, Object> extraAttributes = message.getNotificationMessagePayload().getExtraAttributes();
        Assert.assertEquals(extraAttributes.get(Tokens.Template.name()), template);
    }

}
