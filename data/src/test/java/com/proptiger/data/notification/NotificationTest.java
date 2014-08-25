package com.proptiger.data.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.service.AbstractTest;

public class NotificationTest extends AbstractTest {

    @Autowired
    private NotificationInitiator notificationInitiator;

    @Test
    public void testNotificationTypeGenerator() {
        notificationInitiator.notificationTypeGenerator();
    }

    @Test
    public void testNotificationGenerator() {
        notificationInitiator.notificationGenerator();
    }
}
