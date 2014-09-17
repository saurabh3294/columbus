package com.proptiger.data.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.service.AbstractTest;

public class NotificationTest extends AbstractTest {

    @Autowired
    private NotificationInitiator        notificationInitiator;

    @Autowired
    private NotificationGeneratedService nGeneratedService;
    
    @Autowired
    private NotificationMessageService notificationMessageService;

//    // @Test
//    public void testNotificationTypeGenerator() {
//        notificationInitiator.notificationTypeGenerator();
//    }
//
//    // @Test
//    public void testNotificationGenerator() {
//        notificationInitiator.notificationGenerator();
//    }
//
//    @Test
//    public void createNotification() {
//        logger.info("createNotification started");
//        List<MediumType> mediumTypes = new ArrayList<MediumType>();
//        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
//        
////        mediumTypes.add(MediumType.Email);
////        nMessages.add(notificationMessageService.createNotificationMessage(1211883, "This is a subject for XYZ", "This is a sample template for XYZ"));
////        nMessages.add(notificationMessageService.createNotificationMessage(1211884, "This is a subject for ABC", "This is a sample template for ABC"));
//        
//        mediumTypes.add(MediumType.MarketplaceApp);
//        nMessages.add(notificationMessageService.createNotificationMessage("marketplace_default", 1211883, "{'id':121, 'notifications': ['notification_01', 'notification_02'] }"));
//        nMessages.add(notificationMessageService.createNotificationMessage("marketplace_default", 1211884, "{'id':122, 'notifications': ['notification_03', 'notification_04'] }"));
//  
//        List<NotificationGenerated> notificationGenerateds = nGeneratedService.createNotificationGenerated(
//                nMessages,
//                mediumTypes);
//        logger.info("createNotification ended count: " + notificationGenerateds.size());
//    }
}
