package com.proptiger.data.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.CouponCatalogue;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.user.User;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.external.NotificationCreatorServiceRequest;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.external.NotificationCreatorService;
import com.proptiger.data.service.user.UserService;

@Service
public class CouponNotificationService {

    @Autowired
    private PropertyService            propertyService;

    @Autowired
    private UserService                userService;

    @Autowired
    private ApplicationContext         applicationContext;

    @Autowired
    private NotificationCreatorService notificationCreatorService;

    @Autowired
    private NotificationMessageService nMessageService;

    private CouponCatalogueService     couponCatalogueService;

    @Value("${mail.from.customer}")
    private String                     fromEmail;

    @Async
    public void notifyUserOnCouponBuy(Transaction transaction, CouponCatalogue couponCatalogue) {
        Map<String, Object> payloadMap = new HashMap<String, Object>();

        Property property = applicationContext.getBean(PropertyService.class).getProperty(
                couponCatalogue.getPropertyId());
        User user = userService.getUserById(transaction.getUserId());
        String dateString = new SimpleDateFormat("MMM d, yyy").format(couponCatalogue.getPurchaseExpiryAt());

        payloadMap.put(Tokens.CouponIssued.CouponCode.name(), transaction.getCode());
        payloadMap.put(Tokens.CouponIssued.CouponPrice.name(), couponCatalogue.getCouponPrice() + "");
        payloadMap.put(Tokens.CouponIssued.Date.name(), dateString);
        payloadMap.put(Tokens.CouponIssued.Discount.name(), couponCatalogue.getDiscount() + "");
        payloadMap.put(Tokens.CouponIssued.DiscountPrice.name(), getDiscountPrice(property, couponCatalogue));
        payloadMap.put(Tokens.CouponIssued.ProjectName.name(), property.getProject().getName());
        payloadMap.put(Tokens.CouponIssued.UnitName.name(), property.getUnitName());
        payloadMap.put(Tokens.CouponIssued.Size.name(), property.getSize().intValue() + "");
        payloadMap.put(Tokens.CouponIssued.UserName.name(), user.getFullName());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();

        bccList.addAll(couponCatalogue.getListBuilderEmail());

        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);

        // Sending it to user.
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponIssued,
                transaction.getUserId(),
                payloadMap,
                fromEmail,
                ccList,
                bccList,
                mediumTypes);

        notificationCreatorService.createNotificationGenerated(request);
    }

    @Async
    public void notifyUserOnRefund(Transaction transaction, CouponCatalogue couponCatalogue) {
        Map<String, Object> payloadMap = new HashMap<String, Object>();

        User user = userService.getUserById(transaction.getUserId());

        String couponCode = transaction.getCode() == null ? "" : transaction.getCode();

        payloadMap.put(Tokens.CouponRefunded.CouponCode.name(), couponCode);
        payloadMap.put(Tokens.CouponRefunded.CouponPrice.name(), transaction.getAmount() + "");
        payloadMap.put(Tokens.CouponRefunded.TransactionId.name(), "" + transaction.getId());
        payloadMap.put(Tokens.CouponIssued.UserName.name(), user.getFullName());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();

        if (couponCatalogue != null) {
            bccList.addAll(couponCatalogue.getListBuilderEmail());
        }

        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Email);

        // Sending it to user.
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponRefunded,
                transaction.getUserId(),
                payloadMap,
                fromEmail,
                ccList,
                bccList,
                mediumTypes);

        notificationCreatorService.createNotificationGenerated(request);
    }

    @Async
    public void notifyUserForCancelCoupon(Transaction transaction, CouponCatalogue couponCatalogue) {
        Map<String, Object> notificationPayloadMap = new HashMap<String, Object>();

        Property property = propertyService.getProperty(couponCatalogue.getPropertyId());
        User user = userService.getUserById(transaction.getUserId());

        notificationPayloadMap.put(Tokens.CouponCancelled.CouponCode.name(), transaction.getCode());
        notificationPayloadMap.put(Tokens.CouponCancelled.ProjectName.name(), property.getProject().getName());
        notificationPayloadMap.put(Tokens.CouponCancelled.UnitName.name(), property.getUnitName());
        notificationPayloadMap.put(Tokens.CouponCancelled.UserName.name(), user.getFullName());
        notificationPayloadMap.put(Tokens.CouponCancelled.Size.name(), property.getSize().intValue() + "");
        notificationPayloadMap.put(Tokens.CouponCancelled.Discount.name(), couponCatalogue.getDiscount() + "");
        notificationPayloadMap.put(
                Tokens.CouponCancelled.DiscountPrice.name(),
                getDiscountPrice(property, couponCatalogue));
        notificationPayloadMap.put(Tokens.CouponCancelled.CouponPrice.name(), couponCatalogue.getCouponPrice());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();
        bccList.addAll(couponCatalogue.getListBuilderEmail());

        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);

        // Sending it to user.
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponCancelled,
                transaction.getUserId(),
                notificationPayloadMap,
                fromEmail,
                ccList,
                bccList,
                mediumTypes);

        notificationCreatorService.createNotificationGenerated(request);
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
        notificationPayloadMap.put(Tokens.CouponRedeemed.Size.name(), property.getSize().intValue() + "");
        notificationPayloadMap.put(Tokens.CouponRedeemed.CouponPrice.name(), couponCatalogue.getCouponPrice() + "");
        notificationPayloadMap.put(
                Tokens.CouponRedeemed.DiscountPrice.name(),
                getDiscountPrice(property, couponCatalogue));
        notificationPayloadMap.put(Tokens.CouponRedeemed.UserName.name(), user.getFullName());
        notificationPayloadMap.put(Tokens.CouponRedeemed.CouponCode.name(), transaction.getCode());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();
        bccList.addAll(couponCatalogue.getListBuilderEmail());

        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);

        // Sending it to user.
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponRedeemed,
                transaction.getUserId(),
                notificationPayloadMap,
                fromEmail,
                ccList,
                bccList,
                mediumTypes);

        notificationCreatorService.createNotificationGenerated(request);

    }

    @Async
    public void notifyUserOnPaymentFailure(Transaction transaction) {
        Map<String, Object> payloadMap = new HashMap<String, Object>();

        User user = userService.getUserById(transaction.getUserId());
        CouponCatalogue couponCatalogue = getCouponCatalogueService().getCouponCatalogue(transaction.getProductId());
        Property property = propertyService.getProperty(couponCatalogue.getPropertyId());

        payloadMap.put(Tokens.CouponPaymentFailure.CouponPrice.name(), transaction.getAmount() + "");
        payloadMap.put(Tokens.CouponPaymentFailure.Discount.name(), couponCatalogue.getDiscount() + "");
        payloadMap.put(
                Tokens.CouponPaymentFailure.DiscountPrice.name(),
                getDiscountPrice(property, couponCatalogue) + "");
        payloadMap.put(Tokens.CouponPaymentFailure.ProjectName.name(), property.getProject().getName());
        payloadMap.put(Tokens.CouponPaymentFailure.UnitName.name(), property.getUnitName());
        payloadMap.put(Tokens.CouponPaymentFailure.Size.name(), property.getSize().intValue() + "");
        payloadMap.put(Tokens.CouponPaymentFailure.UserName.name(), user.getFullName());

        List<String> ccList = new ArrayList<String>();
        List<String> bccList = new ArrayList<String>();

        bccList.addAll(couponCatalogue.getListBuilderEmail());

        List<MediumType> mediumTypes = new ArrayList<MediumType>();
        mediumTypes.add(MediumType.Sms);
        mediumTypes.add(MediumType.Email);

        // Sending it to user.
        NotificationCreatorServiceRequest request = new NotificationCreatorServiceRequest(
                NotificationTypeEnum.CouponPaymentFailure,
                transaction.getUserId(),
                payloadMap,
                fromEmail,
                ccList,
                bccList,
                mediumTypes);

        notificationCreatorService.createNotificationGenerated(request);
    }

    private CouponCatalogueService getCouponCatalogueService() {
        if (couponCatalogueService == null) {
            couponCatalogueService = applicationContext.getBean(CouponCatalogueService.class);
        }

        return couponCatalogueService;
    }

    private String getDiscountPrice(Property property, CouponCatalogue couponCatalogue) {
        if (property.getBudget() == null) {
            return "";
        }

        Double discountPrice = property.getBudget() - couponCatalogue.getDiscount();

        return new Long(discountPrice.longValue()).toString();
    }

}
