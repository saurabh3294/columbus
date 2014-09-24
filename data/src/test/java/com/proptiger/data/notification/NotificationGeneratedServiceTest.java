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
import org.testng.annotations.Test;

import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.repo.NotificationGeneratedDao;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMediumService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationTypeNotificationMediumMappingService;
import com.proptiger.data.notification.service.NotificationTypeService;
import com.proptiger.data.service.AbstractTest;

public class NotificationGeneratedServiceTest extends AbstractTest {

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    @Autowired
    private NotificationMessageService   nMessageService;

    @Autowired
    private NotificationMockerService    notificationMockerService;

    @Test
    public void testCreateNotificationGeneratedForEmail() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Email);

        // NotificationMessage message =
        // nMessageService.createNotificationMessage(1211883,
        // "This is a subject for XYZ", "This is a sample template for XYZ");
        NotificationMessage message = notificationMockerService.getMockNotificationMessageForEmail();

        testCreateNotificationGenerated(message, mediumTypes);
    }

    @Test
    public void testCreateNotificationGeneratedForSms() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        
        Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put(Tokens.CouponIssued.CouponCode.name(), "12AB56ab90zB345");
        templateMap.put(Tokens.CouponIssued.Date.name(), "24th September'2014");

        // NotificationMessage message =
        // nMessageService.createNotificationMessage(NotificationTypeEnum.CouponIssued.getName(),
        // 1211883, templateMap);
        NotificationMessage message = notificationMockerService.getMockNotificationMessageForTemplateMap(templateMap);

        testCreateNotificationGenerated(message, mediumTypes);
    }

    @Test
    public void testCreateNotificationGeneratedForAndroid() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.MarketplaceApp);

        String template = "{'id':121, 'notifications': ['notification_01', 'notification_02'] }";
        // NotificationMessage message =
        // nMessageService.createNotificationMessage(NotificationTypeEnum.MarketplaceDefault.getName(),
        // 1211883, template);
        NotificationMessage message = notificationMockerService.getMockNotificationMessageForTemplate(template);

        testCreateNotificationGenerated(message, mediumTypes);
    }

    @Test
    public void testGenerateNotficationGenerated() {
        NotificationMessage message = notificationMockerService.getMockNotificationMessage();
        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(message);

        NotificationType notificationType = notificationMockerService.getMockNotificationType();
        Integer notificationTypeId = message.getNotificationType().getId();
        Map<Integer, List<NotificationMedium>> typeMediumMapping = notificationMockerService
                .getMockTypeMediumMapping(notificationTypeId);
        List<NotificationMedium> notificationMediumList = typeMediumMapping.get(notificationTypeId);

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findDefaultNotificationType()).thenReturn(notificationType);
        nGeneratedService.setNotificationTypeService(notificationTypeService);

        NotificationTypeNotificationMediumMappingService nMappingService = mock(NotificationTypeNotificationMediumMappingService.class);
        when(nMappingService.getTypeMediumMapping()).thenReturn(typeMediumMapping);
        nGeneratedService.setnMappingService(nMappingService);

        NotificationGeneratedDao notificationGeneratedDao = mock(NotificationGeneratedDao.class);
        when(notificationGeneratedDao.save((NotificationGenerated) anyObject())).then(returnsFirstArg());
        nGeneratedService.setNotificationGeneratedDao(notificationGeneratedDao);

        List<NotificationGenerated> notificationGenerateds = nGeneratedService.generateNotficationGenerated(nMessages);
        Assert.assertNotNull(notificationGenerateds);
        Assert.assertEquals(notificationGenerateds.size(), notificationMediumList.size());

        validateNotificationGenerated(notificationGenerateds, message, notificationMediumList.get(0));
    }

    private void testCreateNotificationGenerated(NotificationMessage message, List<MediumType> mediumTypes) {

        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(message);

        NotificationType notificationType = notificationMockerService.getMockNotificationType();
        NotificationMedium notificationMedium = notificationMockerService.getMockNotificationMedium(mediumTypes.get(0));

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findDefaultNotificationType()).thenReturn(notificationType);
        nGeneratedService.setNotificationTypeService(notificationTypeService);

        NotificationMediumService notificationMediumService = mock(NotificationMediumService.class);
        when(notificationMediumService.findNotificationMediumByMediumType(mediumTypes.get(0))).thenReturn(
                notificationMedium);
        nGeneratedService.setNotificationMediumService(notificationMediumService);

        NotificationGeneratedDao notificationGeneratedDao = mock(NotificationGeneratedDao.class);
        when(notificationGeneratedDao.save((NotificationGenerated) anyObject())).then(returnsFirstArg());
        nGeneratedService.setNotificationGeneratedDao(notificationGeneratedDao);

        List<NotificationGenerated> notificationGenerateds = nGeneratedService.createNotificationGenerated(
                nMessages,
                mediumTypes);

        Assert.assertNotNull(notificationGenerateds);
        Assert.assertEquals(notificationGenerateds.size(), mediumTypes.size() * nMessages.size());

        validateNotificationGenerated(notificationGenerateds, message, notificationMedium);
    }

    private void validateNotificationGenerated(
            List<NotificationGenerated> notificationGenerateds,
            NotificationMessage message,
            NotificationMedium notificationMedium) {

        for (NotificationGenerated notificationGenerated : notificationGenerateds) {
            Assert.assertEquals(notificationGenerated.getUserId(), message.getUserId());
            Assert.assertEquals(notificationGenerated.getNotificationMedium().getId(), notificationMedium.getId());
            Assert.assertEquals(notificationGenerated.getNotificationMessage().getId(), message.getId());
            Assert.assertEquals(notificationGenerated.getNotificationType().getId(), message.getNotificationType()
                    .getId());
            Assert.assertEquals(
                    notificationGenerated.getNotificationMessagePayload(),
                    message.getNotificationMessagePayload());
            Assert.assertEquals(notificationGenerated.getObjectId(), message.getNotificationMessagePayload()
                    .getNotificationTypePayload().getPrimaryKeyValue());
            Assert.assertEquals(
                    notificationGenerated.getNotificationMessage().getNotificationStatus(),
                    NotificationStatus.Generated);
            Assert.assertNotNull(notificationGenerated.getData());
        }
    }
}
