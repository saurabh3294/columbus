package com.proptiger.data.notification;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.proptiger.core.enums.notification.NotificationTypeEnum;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
import com.proptiger.core.service.AbstractTest;
import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.processor.message.DefaultNotificationMessageProcessor;
import com.proptiger.data.notification.repo.NotificationMessageDao;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeService;
import com.proptiger.data.notification.service.UserNotificationTypeSubscriptionService;
import com.proptiger.data.service.PropertyService;
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

    @BeforeMethod
    private void init() {
        NotificationMessageDao notificationMessageDao = mock(NotificationMessageDao.class);
        when(notificationMessageDao.saveAndFlush((NotificationMessage) anyObject())).then(returnsFirstArg());
        nMessageService.setNotificationMessageDao(notificationMessageDao);
    }

    @Test
    public void testCreateNotificationMessageForTemplateMap() {
        String typeName = NotificationTypeEnum.Default.getName();
        NotificationType notificationType = notificationMockerService.getMockNotificationType(typeName);
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

        NotificationMessagePayload payload = message.getNotificationMessagePayload();
        Assert.assertNotNull(payload);

        Map<String, Object> extraAttributes = payload.getExtraAttributes();
        Assert.assertNotNull(extraAttributes.get(MOCK_KEY));
        Assert.assertEquals(extraAttributes.get(MOCK_KEY), MOCK_VALUE);

    }

    @Test
    public void testGetNotificationMessagesForNotificationTypeGenerated() {
        NotificationTypeGenerated ntGenerated = notificationMockerService.getMockNotificationTypeGenerated();
        List<User> userList = notificationMockerService.getMockUserList();
        Integer propertyId = (Integer) ntGenerated.getNotificationTypePayload().getPrimaryKeyValue();
        List<PortfolioListing> portfolioListings = notificationMockerService.getMockPortfolioListings(propertyId);
        DefaultNotificationMessageProcessor nMessageProcessor = (DefaultNotificationMessageProcessor) ntGenerated
                .getNotificationType().getNotificationTypeConfig().getNotificationMessageProcessorObject();

        UserNotificationTypeSubscriptionService userNTSubscriptionService = mock(UserNotificationTypeSubscriptionService.class);
        when(
                userNTSubscriptionService.getUnsubscribedUsersByNotificationType(ntGenerated.getNotificationType()
                        .getId())).thenReturn(userList);
        nMessageService.setUserNTSubscriptionService(userNTSubscriptionService);

        Property property = new Property();
        property.setPropertyId(propertyId);

        List<Property> properties = new ArrayList<Property>();
        properties.add(property);

        PortfolioService portfolioService = mock(PortfolioService.class);
        when(portfolioService.getActivePortfolioListingsByPropertiesAndUsers(properties, userList, Boolean.FALSE))
                .thenReturn(portfolioListings);
        nMessageProcessor.setPortfolioService(portfolioService);

        PropertyService propertyService = mock(PropertyService.class);
        when(propertyService.getProperty(propertyId)).thenReturn(property);
        nMessageProcessor.setPropertyService(propertyService);

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
