package com.proptiger.data.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.processor.DefaultNotificationMessageProcessor;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeService;
import com.proptiger.data.notification.service.UserNotificationTypeSubscriptionService;
import com.proptiger.data.service.AbstractTest;
import com.proptiger.data.service.user.portfolio.PortfolioService;

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
        Assert.assertEquals((String) extraAttributes.get(Tokens.Subject.name()), subject);
        Assert.assertEquals((String) extraAttributes.get(Tokens.Body.name()), body);
    }

    @Test
    public void testCreateNotificationMessageForTemplateMap() {
        NotificationType notificationType = notificationMockerService.getMockNotificationType();
        String typeName = notificationType.getName();
        Integer userId = 5435;

        final String MOCK_KEY = "mock-key";
        final String MOCK_VALUE = "mock-value";
        Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put(MOCK_KEY, MOCK_VALUE);

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findByName(typeName)).thenReturn(notificationType);
        nMessageService.setNotiTypeService(notificationTypeService);

        NotificationMessage message = nMessageService.createNotificationMessage(typeName, userId, templateMap);
        Assert.assertNotNull(message);
        Assert.assertEquals(message.getUserId(), userId);
        Assert.assertNotNull(message.getNotificationType());
        Assert.assertNotNull(message.getNotificationType().getName());
        Assert.assertEquals(message.getNotificationType().getName(), typeName);
        Assert.assertNotNull(message.getNotificationMessagePayload());

        Map<String, Object> extraAttributes = message.getNotificationMessagePayload().getExtraAttributes();
        Assert.assertNotNull(extraAttributes.get(MOCK_KEY));
        Assert.assertEquals(extraAttributes.get(MOCK_KEY), MOCK_VALUE);
    }

    @Test
    public void testCreateNotificationMessageForTemplate() {
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

    @Test
    public void testGetNotificationMessagesForNotificationTypeGenerated() {
        NotificationTypeGenerated ntGenerated = notificationMockerService.getMockNotificationTypeGenerated();
        List<ForumUser> userList = notificationMockerService.getMockUserList();
        Integer propertyId = (Integer) ntGenerated.getNotificationTypePayload().getPrimaryKeyValue();
        List<PortfolioListing> portfolioListings = notificationMockerService.getMockPortfolioListings(propertyId);
        DefaultNotificationMessageProcessor nMessageProcessor = (DefaultNotificationMessageProcessor) ntGenerated
                .getNotificationType().getNotificationTypeConfig().getNotificationMessageProcessorObject();

        UserNotificationTypeSubscriptionService userNTSubscriptionService = mock(UserNotificationTypeSubscriptionService.class);
        when(userNTSubscriptionService.getUnsubscribedUsersByNotificationType(ntGenerated.getNotificationType()))
                .thenReturn(userList);
        nMessageService.setUserNTSubscriptionService(userNTSubscriptionService);

        PortfolioService portfolioService = mock(PortfolioService.class);
        when(portfolioService.getActivePortfolioListingsByPropertyId(propertyId)).thenReturn(portfolioListings);
        nMessageProcessor.setPortfolioService(portfolioService);

        List<NotificationMessage> notificationMessageList = nMessageService
                .getNotificationMessagesForNotificationTypeGenerated(ntGenerated);
        Assert.assertNotNull(notificationMessageList);
        Assert.assertEquals(notificationMessageList.size(), userList.size());

        for (NotificationMessage message : notificationMessageList) {
            Assert.assertEquals(message.getNotificationTypeGeneratedId().intValue(), ntGenerated.getId());
            Assert.assertEquals(message.getNotificationType().getName(), ntGenerated.getNotificationType().getName());
            Assert.assertEquals(message.getNotificationMessagePayload().getNotificationTypePayload()
                    .getPrimaryKeyValue(), ntGenerated.getNotificationTypePayload().getPrimaryKeyValue());
        }

    }

}
