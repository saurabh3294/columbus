package com.proptiger.data.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.core.service.AbstractTest;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.external.NotificationCreatorServiceRequest;
import com.proptiger.data.notification.service.external.NotificationCreatorService;

public class NotificationCreatorServiceTest extends AbstractTest {

    @Autowired
    private NotificationCreatorService notificationCreatorService;

    @Test
    public void testCreateNotificationGeneratedForEmail() {
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(1211883, new MailDetails(
                "This is a subject for XYZ",
                "This is a sample template for XYZ"));

        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }

    @Test
    public void testCreateNotificationGeneratedForAndroid() {

        String template = "{'id':121, 'notifications': ['notification_01', 'notification_02'] }";

        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.MarketplaceDefault,
                1211883,
                new DefaultMediumDetails(MediumType.MarketplaceApp, template));

        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }

    @Test
    public void testCreateNotificationGeneratedForCouponEmailAndSMS() {
        Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put(Tokens.CouponIssued.CouponCode.name(), "12AB56ab90zB345");
        templateMap.put(Tokens.CouponIssued.Date.name(), "24th September'2014");
        templateMap.put(Tokens.CouponIssued.CouponPrice.name(), "25000");
        templateMap.put(Tokens.CouponIssued.Discount.name(), "4 Lacs");
        templateMap.put(Tokens.CouponIssued.DiscountPrice.name(), "34.63 Lacs");
        templateMap.put(Tokens.CouponIssued.ProjectName.name(), "Satyam Greens");
        templateMap.put(Tokens.CouponIssued.Size.name(), "1150 sq ft");
        templateMap.put(Tokens.CouponIssued.UnitName.name(), "2BHK + 2T");
        templateMap.put(Tokens.CouponIssued.UserName.name(), "Sahil Garg");

        String ccList[] = { "garg.sahil25@gmail.com" };
        String fromEmail = "customer.service@proptiger.com";

        List<MediumDetails> mediumDetails = new ArrayList<MediumDetails>();
        mediumDetails.add(new MailDetails(ccList, null, fromEmail));
        mediumDetails.add(new DefaultMediumDetails(MediumType.Sms));

        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponIssued,
                1211883,
                templateMap,
                mediumDetails);

        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }
}
