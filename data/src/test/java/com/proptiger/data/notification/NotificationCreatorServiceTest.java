package com.proptiger.data.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.external.NotificationCreatorService;
import com.proptiger.data.notification.external.NotificationCreatorServiceRequest;
import com.proptiger.data.service.AbstractTest;

public class NotificationCreatorServiceTest extends AbstractTest {

    @Autowired
    private NotificationCreatorService notificationCreatorService;

    @Test
    public void testCreateNotificationGeneratedForEmail() {
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                1211883,
                "This is a subject for XYZ",
                "This is a sample template for XYZ");
        
        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }
    
    @Test
    public void testCreateNotificationGeneratedForAndroid() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.MarketplaceApp);

        String template = "{'id':121, 'notifications': ['notification_01', 'notification_02'] }";

        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.MarketplaceDefault,
                1211883,
                template,
                mediumTypes);
        
        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }
    
    @Test
    public void testCreateNotificationGeneratedForSms() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);

        Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put(Tokens.CouponIssued.CouponCode.name(), "12AB56ab90zB345");
        templateMap.put(Tokens.CouponIssued.Date.name(), "24th September'2014");

        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponIssued,
                1211883,
                templateMap,
                mediumTypes);
        
        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }

    @Test
    public void testCreateNotificationGeneratedForCouponEmailAndSMS() {
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Email);
        mediumTypes.add(MediumType.Sms);

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

        List<String> ccList = new ArrayList<String>();
        ccList.add("mukand.agarwal@proptiger.com");

        String fromEmail = "customer.service@proptiger.com";

        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponIssued,
                1211883,
                templateMap,
                fromEmail,
                ccList,
                null,
                mediumTypes);

        Assert.assertNotNull(request);
        // notificationCreatorService.createNotificationGenerated(request);
    }
}
