package com.proptiger.data.notification;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.core.service.AbstractTest;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationStatus;
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

public class NotificationGeneratedServiceTest extends AbstractTest {

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    @Autowired
    private NotificationMessageService   nMessageService;

    @Autowired
    private NotificationMockerService    notificationMockerService;

    @Test
    public void testCreateNotificationGenerated() {
        List<MediumDetails> mediumDetails = new ArrayList<MediumDetails>();
        mediumDetails.add(new DefaultMediumDetails(MediumType.Sms));
        mediumDetails.add(new MailDetails());

        NotificationMessage message = notificationMockerService.getMockNotificationMessage();

        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(message);

        NotificationType notificationType = notificationMockerService.getMockNotificationType();

        NotificationTypeService notificationTypeService = mock(NotificationTypeService.class);
        when(notificationTypeService.findDefaultNotificationType()).thenReturn(notificationType);
        nGeneratedService.setNotificationTypeService(notificationTypeService);

        NotificationMediumService notificationMediumService = mock(NotificationMediumService.class);
        when(notificationMediumService.findNotificationMediumByMediumType(MediumType.Sms)).thenReturn(
                notificationMockerService.getMockNotificationMedium(MediumType.Sms));
        when(notificationMediumService.findNotificationMediumByMediumType(MediumType.Email)).thenReturn(
                notificationMockerService.getMockNotificationMedium(MediumType.Email));
        nGeneratedService.setNotificationMediumService(notificationMediumService);

        NotificationGeneratedDao notificationGeneratedDao = mock(NotificationGeneratedDao.class);
        when(notificationGeneratedDao.save((NotificationGenerated) anyObject())).then(returnsFirstArg());
        nGeneratedService.setNotificationGeneratedDao(notificationGeneratedDao);

        List<NotificationGenerated> notificationGenerateds = nGeneratedService.createNotificationGenerated(
                nMessages,
                mediumDetails);

        Assert.assertNotNull(notificationGenerateds);
        Assert.assertEquals(notificationGenerateds.size(), mediumDetails.size() * nMessages.size());

        validateNotificationGenerated(notificationGenerateds, message);
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
        when(nMappingService.getMediumsByNotificationTypeId(notificationTypeId)).thenReturn(typeMediumMapping.get(notificationTypeId));
        nGeneratedService.setnMappingService(nMappingService);

        NotificationGeneratedDao notificationGeneratedDao = mock(NotificationGeneratedDao.class);
        when(notificationGeneratedDao.save((NotificationGenerated) anyObject())).then(returnsFirstArg());
        nGeneratedService.setNotificationGeneratedDao(notificationGeneratedDao);

        List<NotificationGenerated> notificationGenerateds = nGeneratedService.generateNotficationGenerated(nMessages);
        Assert.assertNotNull(notificationGenerateds);
        Assert.assertEquals(notificationGenerateds.size(), notificationMediumList.size());

        validateNotificationGenerated(notificationGenerateds, message);
    }

    private void validateNotificationGenerated(
            List<NotificationGenerated> notificationGenerateds,
            NotificationMessage message) {

        for (NotificationGenerated notificationGenerated : notificationGenerateds) {
            Assert.assertEquals(notificationGenerated.getUserId(), message.getUserId());
            Assert.assertEquals(notificationGenerated.getNotificationMessage().getId(), message.getId());
            Assert.assertEquals(notificationGenerated.getNotificationType().getId(), message.getNotificationType()
                    .getId());
            Assert.assertEquals(notificationGenerated.getNotificationMessagePayload().getExtraAttributes(), message
                    .getNotificationMessagePayload().getExtraAttributes());
            Assert.assertEquals(notificationGenerated.getObjectId(), message.getNotificationMessagePayload()
                    .getNotificationTypePayload().getPrimaryKeyValue());
            Assert.assertEquals(
                    notificationGenerated.getNotificationMessage().getNotificationStatus(),
                    NotificationStatus.Generated);
            Assert.assertNotNull(notificationGenerated.getData());
        }
    }
}
