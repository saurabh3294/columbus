package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.model.CouponCatalogue;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserAttribute;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.repo.CouponCatalogueDao;
import com.proptiger.data.service.transaction.TransactionService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.exception.BadRequestException;

@Service
public class CouponCatalogueService {

    @Autowired
    private CouponCatalogueDao         couponCatalogueDao;

    @Autowired
    private UserService                userService;

    @Autowired
    private ApplicationContext         applicationContext;

    @Autowired
    private NotificationMessageService nMessageService;
    
    @Autowired
    private NotificationGeneratedService nGeneratedService;
    
    @Value("${mail.from.customer}")
    private String fromEmail;
    
    @Autowired
    private CouponNotificationService couponNotificationService;

    // Do not autowire them. Use getter to use them.
    private TransactionService         transactionService;
    private PropertyService            propertyService;

    /**
     * This method will return the coupon catalogue for a propertyId
     * 
     * @param propertyId
     * @return
     */
    public CouponCatalogue getCouponCatalogueByPropertyId(int propertyId) {
        return couponCatalogueDao.findByPropertyIdAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
                propertyId,
                0,
                new Date());
    }

    /**
     * This method will return the coupon catalogue for the properties.
     * 
     * @param propertyIds
     * @return
     */
    public List<CouponCatalogue> getCouponCataloguesByPropertyIds(List<Integer> propertyIds) {
        return couponCatalogueDao.findByPropertyIdInAndInventoryLeftGreaterThanAndPurchaseExpiryAtGreaterThan(
                propertyIds,
                0,
                new Date());
    }

    /**
     * This method will create a Map Of propertyId and Coupon Catalogue which
     * have coupons left to be sold.
     * 
     * @param propertyIds
     * @return
     */
    public Map<Integer, CouponCatalogue> getCouponCatalogueMapByPropertyIds(List<Integer> propertyIds) {
        List<CouponCatalogue> listCouponCatalogue = getCouponCataloguesByPropertyIds(propertyIds);

        Map<Integer, CouponCatalogue> map = new HashMap<Integer, CouponCatalogue>();

        if (listCouponCatalogue == null || listCouponCatalogue.isEmpty())
            return map;

        for (CouponCatalogue couponCatalogue : listCouponCatalogue) {
            map.put(couponCatalogue.getPropertyId(), couponCatalogue);
        }

        return map;
    }

    /**
     * This method will update the number of coupons left after coupon refund or
     * purchase.
     * 
     * @param couponId
     * @param inventoryCount
     * @return
     */
    @Transactional
    public CouponCatalogue updateCouponCatalogueInventoryLeft(int couponId, int inventoryCount) {
        Integer numberOfRowsAffected = couponCatalogueDao.updateCouponInventory(couponId, inventoryCount, new Date());
        if (numberOfRowsAffected != null && numberOfRowsAffected > 0) {
            return couponCatalogueDao.findOne(couponId);
        }

        return null;
    }

    /**
     * This method will return the valid coupons based on coupon Id by checking
     * its expiry date.
     */
    public boolean isPurchasable(int couponId) {
        CouponCatalogue coupon = couponCatalogueDao.findByIdAndPurchaseExpiryAtGreaterThan(couponId, new Date());
        return coupon != null && coupon.getInventoryLeft() > 0;
    }

    /**
     * Returns all details
     * 
     * @param id
     * @return
     */
    public CouponCatalogue getCouponCatalogue(int id) {
        CouponCatalogue coupon = couponCatalogueDao.findOne(id);
        coupon.setProperty(getPropertyService().getProperty(coupon.getPropertyId()));
        return coupon;
    }

    /**
     * This method will redeem coupon.
     * 
     * @param couponCode
     * @return
     */
    public int redeemCoupon(String couponCode, String userProofId) {
        Transaction transaction = getTransactionService().getNonRedeemTransactionByCode(couponCode);
        if (transaction == null) {
            throw new BadRequestException(
                    ResponseCodes.RESOURCE_NOT_FOUND,
                    "Coupon Code does not exists or has been redeemed.");
        }

        UserAttribute userAttribute = userService.checkUserAttributesByAttributeValue(
                transaction.getUserId(),
                userProofId);
        if (userAttribute == null) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    "User Identity for this Coupon code does not match with our records.");
        }

        CouponCatalogue couponCatalogue = couponCatalogueDao.findOne(transaction.getProductId());

        /*
         * Coupon Expired then throw Exception.
         */
        if (couponCatalogue.getPurchaseExpiryAt().before(new Date())) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, "Coupon has been expired. Hence cannot be redeemed.");
        }
        int status = getTransactionService().updateCouponRedeem(transaction);

        if (status < 1) {
            throw new BadRequestException(
                    ResponseCodes.NAME_ALREADY_EXISTS,
                    " Coupon has already been redeem or been refunded.");
        }

        couponNotificationService.notifyUserOnCouponRedeem(transaction, couponCatalogue);
        return status;
    }

    /**
     * Fetch user details of the user based on Coupon Code.
     * 
     * @param couponCode
     * @return
     */
    @Transactional
    public User fetchUserDetailsOfCouponBuyer(String couponCode, String userProofId) {
        Transaction transaction = getTransactionService().getTransactionsByCouponCode(couponCode);
        if (transaction == null) {
            throw new BadRequestException(ResponseCodes.RESOURCE_NOT_FOUND, "Coupon Code does not exits.");
        }

        UserAttribute userAttribute = userService.checkUserAttributesByAttributeValue(
                transaction.getUserId(),
                userProofId);
        if (userAttribute == null) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    "User Identity for this Coupon code does not match with our records.");
        }

        User user = userService.getUserById(transaction.getUserId());
        /**
         * Get call to get them from db as they are fetched in LAZY.
         */
        Hibernate.initialize(user.getAttributes());

        return user;
    }

    

    /**
     * fetching coupon details based on coupon Code.
     */
    public Transaction fetchCouponDetails(String couponCode, String userProofId) {
        Transaction transaction = getTransactionService().getTransactionsByCouponCode(couponCode);
        
        
        if (transaction == null) {
            throw new BadRequestException(ResponseCodes.RESOURCE_NOT_FOUND, "Coupon Code does not exits.");
        }
        
        transaction = getTransactionService().getUpdatedTransaction(transaction.getId());
        
        UserAttribute userAttribute = userService.checkUserAttributesByAttributeValue(
                transaction.getUserId(),
                userProofId);
        if (userAttribute == null) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    "User Identity for this Coupon code does not match with our records.");
        }

        return transaction;
    }

    
    public boolean cancelCoupon(String couponCode, String userProofId){
        Transaction transaction = getTransactionService().getNonRedeemTransactionByCode(couponCode);
        
        if (transaction == null) {
            throw new BadRequestException(ResponseCodes.BAD_CREDENTIAL, "Coupon Code does not exits or has been redeemed or been refunded already.");
        }

        UserAttribute userAttribute = userService.checkUserAttributesByAttributeValue(
                transaction.getUserId(),
                userProofId);
        if (userAttribute == null) {
            throw new BadRequestException(
                    ResponseCodes.BAD_CREDENTIAL,
                    "User Identity for this Coupon code does not match with our records.");
        }
        
        boolean refundStatus = getTransactionService().handleTransactionRefund(transaction);
        
        if(refundStatus){
            couponNotificationService.notifyUserForCancelCoupon(transaction, getCouponCatalogue(transaction.getProductId()));
        }
        
        return refundStatus;
    }
    
    
    
    private TransactionService getTransactionService() {
        if (transactionService == null) {
            transactionService = applicationContext.getBean(TransactionService.class);
        }
        return transactionService;
    }

    private PropertyService getPropertyService() {
        if (propertyService == null) {
            propertyService = applicationContext.getBean(PropertyService.class);
        }
        return propertyService;
    }
    
    
}
