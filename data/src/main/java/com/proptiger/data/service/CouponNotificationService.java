package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.CouponCatalogue;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.model.user.User;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.service.user.UserService;

@Service
public class CouponNotificationService {

    @Autowired
    private PropertyService              propertyService;

    @Autowired
    private UserService                  userService;

    @Autowired
    private ApplicationContext           applicationContext;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

    @Autowired
    private NotificationMessageService   nMessageService;

    @Value("${mail.from.customer}")
    private String                       fromEmail;

    public void notifyUserOnCouponBuy(Transaction transaction, CouponCatalogue couponCatalogue) {
        Map<String, Object> payloadMap = new HashMap<String, Object>();

        Property property = applicationContext.getBean(PropertyService.class).getProperty(
                couponCatalogue.getPropertyId());
        User user = userService.getUserById(transaction.getUserId());

        payloadMap.put(Tokens.CouponIssued.CouponCode.name(), transaction.getCode());
        payloadMap.put(Tokens.CouponIssued.CouponPrice.name(), couponCatalogue.getCouponPrice());
        payloadMap.put(Tokens.CouponIssued.Date.name(), couponCatalogue.getPurchaseExpiryAt());
        payloadMap.put(Tokens.CouponIssued.Discount.name(), couponCatalogue.getDiscount());
        payloadMap.put(Tokens.CouponIssued.DiscountPrice.name(), property.getBudget() - couponCatalogue.getDiscount());
        payloadMap.put(Tokens.CouponIssued.ProjectName.name(), property.getProject().getName());
        payloadMap.put(Tokens.CouponIssued.UnitName.name(), property.getUnitName());
        payloadMap.put(Tokens.CouponIssued.Size.name(), property.getSize());
        payloadMap.put(Tokens.CouponIssued.UserName.name(), user.getFullName());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();

        bccList.add(couponCatalogue.getBuilderEmail());

        // Sending it to user.
        NotificationMessage nMessage = nMessageService.createNotificationMessage(
                NotificationTypeEnum.CouponIssued.getName(),
                transaction.getUserId(),
                payloadMap,
                fromEmail,
                ccList,
                bccList);

        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(nMessage);
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);

        notificationGeneratedService.createNotificationGenerated(nMessages, mediumTypes);

    }
    
    public void notifyUserOnRefund(Transaction transaction, CouponCatalogue couponCatalogue){
        Map<String, Object> payloadMap = new HashMap<String, Object>();

        User user = userService.getUserById(transaction.getUserId());

        payloadMap.put(Tokens.CouponRefunded.CouponCode.name(), transaction.getCode());
        payloadMap.put(Tokens.CouponRefunded.CouponPrice.name(), transaction.getAmount());
        payloadMap.put(Tokens.CouponRefunded.TransactionId.name(), transaction.getId());
        payloadMap.put(Tokens.CouponIssued.UserName.name(), user.getFullName());
        
        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();
        
        if(couponCatalogue != null){
            bccList.add(couponCatalogue.getBuilderEmail());
        }
        
        // Sending it to user.
        NotificationMessage nMessage = nMessageService.createNotificationMessage(
                NotificationTypeEnum.CouponRefunded.getName(),
                transaction.getUserId(),
                payloadMap,
                fromEmail,
                ccList,
                bccList);
        
        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(nMessage);
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Email);

        notificationGeneratedService.createNotificationGenerated(nMessages, mediumTypes);
    }
    
    public void notifyUserForCancelCoupon(Transaction transaction, CouponCatalogue couponCatalogue){
        Map<String, Object> notificationPayloadMap = new HashMap<String, Object>();

        Property property = propertyService.getProperty(couponCatalogue.getPropertyId());
        User user = userService.getUserById(transaction.getUserId());

        notificationPayloadMap.put(Tokens.CouponCancelled.CouponCode.name(), transaction.getCode());
        notificationPayloadMap.put(Tokens.CouponCancelled.ProjectName.name(), property.getProject().getName());
        notificationPayloadMap.put(Tokens.CouponCancelled.UnitName.name(), property.getUnitName());
        notificationPayloadMap.put(Tokens.CouponCancelled.UserName.name(), user.getFullName());
        notificationPayloadMap.put(Tokens.CouponCancelled.Size.name(), property.getSize());
        notificationPayloadMap.put(Tokens.CouponCancelled.Discount.name(), couponCatalogue.getDiscount());
        notificationPayloadMap.put(Tokens.CouponCancelled.DiscountPrice.name(), property.getBudget() - couponCatalogue.getDiscount());
        notificationPayloadMap.put(Tokens.CouponCancelled.CouponPrice.name(), couponCatalogue.getCouponPrice());
        
        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();
        bccList.add(couponCatalogue.getBuilderEmail());

        NotificationMessage nMessage = nMessageService.createNotificationMessage(
                NotificationTypeEnum.CouponCancelled.getName(),
                transaction.getUserId(),
                notificationPayloadMap,
                fromEmail,
                ccList,
                bccList);
        
        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(nMessage);
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);
        
        notificationGeneratedService.createNotificationGenerated(nMessages, mediumTypes);
    }
    
    /**
     * Notify user by email/sms when user coupon has been redeemed.
     * 
     * @param transaction
     * @param couponCatalogue
     */
    @Async
    public void notifyUserOnCouponRedeem(Transaction transaction, CouponCatalogue couponCatalogue) {
        Map<String, Object> notificationPayloadMap = new HashMap<String, Object>();

        Property property = propertyService.getProperty(couponCatalogue.getPropertyId());
        User user = userService.getUserById(transaction.getUserId());

        notificationPayloadMap.put(Tokens.CouponRedeemed.RedeemedDate.name(), transaction.getUpdatedAt());
        notificationPayloadMap.put(Tokens.CouponRedeemed.ProjectName.name(), property.getProject().getName());
        notificationPayloadMap.put(Tokens.CouponRedeemed.UnitName.name(), property.getUnitName());
        notificationPayloadMap.put(Tokens.CouponRedeemed.Size.name(), property.getSize());
        notificationPayloadMap.put(Tokens.CouponRedeemed.CouponPrice.name(), couponCatalogue.getCouponPrice());
        notificationPayloadMap.put(Tokens.CouponRedeemed.DiscountPrice.name(), property.getBudget() - couponCatalogue.getDiscount());
        notificationPayloadMap.put(Tokens.CouponRedeemed.UserName.name(), user.getFullName());
        notificationPayloadMap.put(Tokens.CouponRedeemed.CouponCode.name(), transaction.getCode());


        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();
        bccList.add(couponCatalogue.getBuilderEmail());
        
        NotificationMessage nMessage = nMessageService.createNotificationMessage(
                NotificationTypeEnum.CouponRedeemed.getName(),
                transaction.getUserId(),
                notificationPayloadMap,
                fromEmail,
                ccList,
                bccList);
        
        List<NotificationMessage> nMessages = new ArrayList<NotificationMessage>();
        nMessages.add(nMessage);
        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);
        
        notificationGeneratedService.createNotificationGenerated(nMessages, mediumTypes);
        
    }
}
