package com.proptiger.data.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.mocker.EventMockerService;
import com.proptiger.data.mocker.NotificationMockerService;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.service.EventTypeToNotificationTypeMappingService;
import com.proptiger.data.notification.service.NotificationTypeGeneratedService;
import com.proptiger.data.service.AbstractTest;

/**
 * 
 * @author sahil
 * 
 */
public class NotificationTypeGeneratedServiceTest extends AbstractTest {

    @Autowired
    private NotificationTypeGeneratedService ntGeneratedService;

    @Autowired
    private EventMockerService               eventMockerService;

    @Autowired
    private NotificationMockerService        notificationMockerService;

    @Test
    public void testGetNotificationTypesForEventGenerated() {

        EventGenerated eventGenerated = eventMockerService.getMockEventGenerated();

        EventTypeToNotificationTypeMappingService ntMappingService = mock(EventTypeToNotificationTypeMappingService.class);
        List<NotificationType> notificationTypes = notificationMockerService.getMockNotificationTypes();
        when(ntMappingService.getNotificationTypesByEventType(eventGenerated.getEventType())).thenReturn(
                notificationTypes);
        ntGeneratedService.setNtMappingService(ntMappingService);

        List<NotificationTypeGenerated> ntGeneratedList = ntGeneratedService
                .getNotificationTypesForEventGenerated(eventGenerated);

        Assert.assertNotNull(notificationTypes);
        Assert.assertNotNull(ntGeneratedList);
        Assert.assertEquals(ntGeneratedList.size(), notificationTypes.size());

        for (NotificationTypeGenerated ntGenerated : ntGeneratedList) {
            Assert.assertEquals(ntGenerated.getEventGeneratedId().intValue(), eventGenerated.getId());

            NotificationTypePayload ntPayload = ntGenerated.getNotificationTypePayload();
            DefaultEventTypePayload eventTypePayload = (DefaultEventTypePayload) eventGenerated.getEventTypePayload();

            Assert.assertEquals(ntPayload.getOldValue(), eventTypePayload.getOldValue());
            Assert.assertEquals(ntPayload.getNewValue(), eventTypePayload.getNewValue());
            Assert.assertEquals(ntPayload.getTransactionDateName(), eventTypePayload.getTransactionDateKeyName());
            Assert.assertEquals(ntPayload.getTransactionDate(), eventTypePayload.getTransactionDateKeyValue());
            Assert.assertEquals(ntPayload.getTransactionIdName(), eventTypePayload.getTransactionKeyName());
            Assert.assertEquals(ntPayload.getTransactionId(), eventTypePayload.getTransactionId());
            Assert.assertEquals(ntPayload.getPrimaryKeyName(), eventTypePayload.getPrimaryKeyName());
            Assert.assertEquals(ntPayload.getPrimaryKeyValue(), eventTypePayload.getPrimaryKeyValue());
        }
    }

}
