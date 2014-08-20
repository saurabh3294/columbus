package com.proptiger.data.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.notification.generator.NotificationGenerator;
import com.proptiger.data.service.AbstractTest;

public class NotificationTest extends AbstractTest {

    @Autowired
    private NotificationGenerator notificationGenerator;

    @Test
    public void testNotificationGenerator() {
        notificationGenerator.generateNotifications();
    }
}
