package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citruspay.pg.model.Enquiry;
import com.proptiger.data.model.CouponCatalogue;
import com.proptiger.data.model.enums.transaction.PaymentStatus;
import com.proptiger.data.model.enums.transaction.TransactionStatus;
import com.proptiger.data.model.transaction.Payment;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.service.transaction.CitrusPayPGService;
import com.proptiger.data.service.transaction.PaymentService;
import com.proptiger.data.service.transaction.TransactionService;

@Service
public class CitrusPayPGTransactionService {

    @Autowired
    private TransactionService        transactionService;

    @Autowired
    private PaymentService            paymentService;

    @Autowired
    private CouponCatalogueService    couponCatalogueService;

    @Autowired
    private CitrusPayPGService        citrusPayPGService;

    @Autowired
    private CouponNotificationService couponNotificationService;

    @Transactional
    public boolean saveRefundTransaction(Transaction transaction, Enquiry lastEnquiry, boolean incrementCouponInventory) {

        Payment payment = citrusPayPGService.createPaymentFromEnquiry(transaction, lastEnquiry);
        payment.setStatusId(PaymentStatus.Refunded.getId());
        transaction.setStatusId(TransactionStatus.Refunded.getId());

        // logger.info(Serializer.toJson(payment));
        // logger.info(Serializer.toJson(transaction));

        paymentService.save(payment);
        transactionService.save(transaction);

        /**
         * The case when coupon has not been granted but the payment has been
         * done.
         */
        CouponCatalogue couponCatalogue = null;
        if (incrementCouponInventory) {
            couponCatalogue = couponCatalogueService.updateCouponCatalogueInventoryLeft(transaction.getProductId(), 1);
        }

        couponNotificationService.notifyUserOnRefund(transaction, couponCatalogue);

        return true;
    }

    @Transactional
    public boolean handleOfflineRefund(Transaction transaction, Payment payment) {
        Payment refundPayment = paymentService.createOfflinePayment(
                transaction.getAmount(),
                payment.getTypeId(),
                PaymentStatus.Refunded,
                transaction.getId());

        paymentService.save(refundPayment);
        transaction.setStatusId(TransactionStatus.Refunded.getId());
        transactionService.save(transaction);
        CouponCatalogue couponCatalogue = couponCatalogueService.updateCouponCatalogueInventoryLeft(
                transaction.getProductId(),
                1);

        couponNotificationService.notifyUserForCancelCoupon(transaction, couponCatalogue);

        return true;
    }

}
