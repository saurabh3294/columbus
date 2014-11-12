/**
 * 
 */
package com.proptiger.data.service.transaction;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.citruspay.pg.exception.CitruspayException;
import com.citruspay.pg.model.Enquiry;
import com.citruspay.pg.model.EnquiryCollection;
import com.citruspay.pg.model.Refund;
import com.citruspay.pg.net.RequestSignature;
import com.citruspay.pg.util.CitruspayConstant;
import com.google.gson.Gson;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.model.cms.CouponCatalogue;
import com.proptiger.core.model.user.User;
import com.proptiger.data.model.enums.transaction.PaymentStatus;
import com.proptiger.data.model.enums.transaction.PaymentType;
import com.proptiger.data.model.enums.transaction.TransactionStatus;
import com.proptiger.data.model.transaction.Payment;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.model.transaction.thirdparty.CitrusPayPGEnquiryTransactionType;
import com.proptiger.data.model.transaction.thirdparty.CitrusPayPGInitiatePaymentRequestParams;
import com.proptiger.data.model.transaction.thirdparty.CitrusPayPGPaymentResponseData;
import com.proptiger.data.model.transaction.thirdparty.CitrusPayPGPaymentStatus;
import com.proptiger.data.model.transaction.thirdparty.EnquiryResponseCode;
import com.proptiger.data.model.transaction.thirdparty.PaymentGatewayResponse;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.repo.transaction.CitrusPayPGResponseDao;
import com.proptiger.data.service.CitrusPayPGTransactionService;
import com.proptiger.data.service.CouponCatalogueService;
import com.proptiger.data.service.CouponNotificationService;
import com.proptiger.data.service.user.UserService;

/**
 * @author mandeep
 * 
 */
@Service
public class CitrusPayPGService {
    private static final String           CURRENCY_INR                             = "INR";

    private static final String           ENQUIRY_COLLECTION_SUCCESS_RESPONSE_CODE = "200";

    private static final String           FORWARD_SLASH                            = "/";

    private static final String           SUCCESS_RESPONSE_CODE                    = "0";

    @Value("${paymentgateway.citruspay.merchant.bankname}")
    private String                        CITRUS_PAY_PG_MERCHANT_BANKNAME;

    @Value("${paymentgateway.citruspay.merchant.url}")
    private String                        CITRUS_PAY_PG_MERCHANT_URL;

    @Value("${paymentgateway.merchant.secret.key}")
    private String                        CITRUS_PAY_PG_MERCHANT_SECRET_KEY;

    @Value("${paymentgateway.merchant.return.url}")
    private String                        CITRUS_PAY_PG_MERCHANT_RETURN_URL;

    @Value("${paymentgateway.merchant.notify.url}")
    private String                        CITRUS_PAY_PG_MERCHANT_NOTIFY_URL;

    @Value("${paymentgateway.merchant.access.key}")
    private String                        CITRUS_PAY_PG_MERCHANT_ACCESS_KEY;

    @Autowired
    private NotificationGeneratedService  notificationGeneratedService;

    @Autowired
    private NotificationMessageService    notificationMessageService;

    @Autowired
    private NotificationGeneratedService  nGeneratedService;

    private static Logger                 logger                                   = LoggerFactory
                                                                                           .getLogger(CitrusPayPGService.class);

    @Autowired
    private TransactionService            transactionService;

    @Autowired
    private CouponCatalogueService        couponCatalogueService;

    @Autowired
    private PaymentService                paymentService;

    @Autowired
    private CitrusPayPGResponseDao        citrusPayPGResponseDao;

    @Autowired
    private NotificationMessageService    nMessageService;

    @Autowired
    private UserService                   userService;

    @Autowired
    private ApplicationContext            applicationContext;

    @Autowired
    private CouponNotificationService     couponNotificationService;

    @Autowired
    private CitrusPayPGTransactionService citrusPayPGTransactionService;

    public URI initiatePaymentRequest(Transaction transaction) {
        String signature = createSignature(transaction);

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.merchantTxnId.name(), String.valueOf(transaction.getId()));
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.firstName.name(), transaction.getUser().getFullName());
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.currency.name(), CURRENCY_INR);
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.email.name(), transaction.getUser().getEmail());
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.orderAmount.name(), String.valueOf(transaction.getAmount()));
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.reqtime.name(), String.valueOf(System.currentTimeMillis()));
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.secSignature.name(), signature);
        mvm.add(CitrusPayPGInitiatePaymentRequestParams.notifyUrl.name(), CITRUS_PAY_PG_MERCHANT_NOTIFY_URL);
        mvm.add(
                CitrusPayPGInitiatePaymentRequestParams.returnUrl.name(),
                CITRUS_PAY_PG_MERCHANT_RETURN_URL + "?transactionId=" + transaction.getId());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_key", CITRUS_PAY_PG_MERCHANT_ACCESS_KEY);
        headers.add("signature", signature);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, headers);

        ResponseEntity<String> exchange = restTemplate.exchange(
                CITRUS_PAY_PG_MERCHANT_URL,
                HttpMethod.POST,
                requestEntity,
                String.class);
        return exchange.getHeaders().getLocation();
    }

    private String createSignature(Transaction transaction) {
        String vanityURLPart = "";

        if (CITRUS_PAY_PG_MERCHANT_URL.lastIndexOf(FORWARD_SLASH) != -1) {
            vanityURLPart = CITRUS_PAY_PG_MERCHANT_URL
                    .substring(CITRUS_PAY_PG_MERCHANT_URL.lastIndexOf(FORWARD_SLASH) + 1);
        }

        String generateHMAC = RequestSignature.generateHMAC(
                vanityURLPart + transaction.getAmount() + transaction.getId() + CURRENCY_INR,
                CITRUS_PAY_PG_MERCHANT_SECRET_KEY);
        return generateHMAC;
    }

    public void handlePaymentResponse(Object object) {
        PaymentGatewayResponse response = new PaymentGatewayResponse();
        response.setJsonDump(new Gson().toJson(object));
        citrusPayPGResponseDao.saveAndFlush(response);

        CitrusPayPGPaymentResponseData data = new Gson().fromJson(
                response.getJsonDump(),
                CitrusPayPGPaymentResponseData.class);

        boolean couponBuyed = persistTransactionOnPaymentResponse(data, response);

    }

    @Transactional
    private boolean persistTransactionOnPaymentResponse(
            CitrusPayPGPaymentResponseData data,
            PaymentGatewayResponse response) {
        Transaction transaction = transactionService.getTransaction(data.getTxId());
        validateResponse(data, transaction);

        Payment payment = new Payment();
        payment.setTransactionId(transaction.getId());
        payment.setAmount((int) data.getAmount());
        payment.setCitrusPayGatewayTransactionId(data.getTxRefNo());
        payment.setGatewayTransactionId(data.getPgTxnNo());
        payment.setPaymentGatewayResponseId(response.getId());
        payment.setTypeId(PaymentType.Online.getId());
        if (data.getPgRespCode() == 0 && CitrusPayPGPaymentStatus.SUCCESS.name().equals(data.getTxStatus())) {
            /**
             * reducing the coupon inventory in the coupon Catalogue table. If
             * it coupon reduction failed then coupon cannot be bought. Hence,
             * not saving the transaction.
             */
            CouponCatalogue couponCatalogue = couponCatalogueService.updateCouponCatalogueInventoryLeft(
                    transaction.getProductId(),
                    -1);
            if (couponCatalogue == null) {
                return false;
            }

            transaction.setStatusId(TransactionStatus.Complete.getId());
            transaction.setCode(createCouponCode(transaction));
            transactionService.save(transaction);
            payment.setStatusId(PaymentStatus.Success.getId());

            /**
             * Notify User about the payment of coupon.
             */
            couponNotificationService.notifyUserOnCouponBuy(transaction, couponCatalogue);

        }
        else {
            payment.setStatusId(PaymentStatus.Failed.getId());
        }

        paymentService.save(payment);

        return true;
    }

    public String createCouponCode(Transaction transaction) {
        return "PT" + transaction.getId()
                + RandomStringUtils.randomAlphabetic(7 - (int) Math.log10(transaction.getId())).toUpperCase();
    }

    private void validateResponse(CitrusPayPGPaymentResponseData data, Transaction transaction) {
        validateResponseSignature(data);
        validateAmount(data, transaction);
        validateUserData(data, transaction.getUser());
    }

    private void validateUserData(CitrusPayPGPaymentResponseData data, User user) {
        if (!user.getEmail().equalsIgnoreCase(data.getEmail()) || !user.getFullName().equalsIgnoreCase(
                data.getFirstName())) {
            throw new IllegalArgumentException("Mismatch in user data - Found: " + data.getFirstName()
                    + "<"
                    + data.getEmail()
                    + ">, Expected: "
                    + user.getFullName()
                    + "<"
                    + user.getEmail()
                    + ">");
        }
    }

    private void validateAmount(CitrusPayPGPaymentResponseData data, Transaction transaction) {
        if (Math.abs(data.getAmount() - transaction.getAmount()) > 0.01) {
            throw new IllegalArgumentException("Amount mismatch - Found: " + data.getAmount()
                    + ", Expected: "
                    + transaction.getAmount());
        }
    }

    private void validateResponseSignature(CitrusPayPGPaymentResponseData response) {
        String data = "";
        String txnId = String.valueOf(response.getTxId());
        String txnStatus = response.getTxStatus();
        String amount = String.valueOf(response.getAmount());
        String pgTxnId = String.valueOf(response.getPgTxnNo());
        String issuerRefNo = response.getIssuerRefNo();
        String authIdCode = response.getAuthIdCode();
        String firstName = response.getFirstName();
        String lastName = response.getLastName();
        String pgRespCode = String.valueOf(response.getPgRespCode());
        String zipCode = response.getAddressZip();
        String responseSignature = response.getSignature();
        String computedSignature = "";
        boolean flag = true;
        if (txnId != null) {
            data += txnId;
        }
        if (txnStatus != null) {
            data += txnStatus;
        }
        if (amount != null) {
            data += amount;
        }
        if (pgTxnId != null) {
            data += pgTxnId;
        }
        if (issuerRefNo != null) {
            data += issuerRefNo;
        }
        if (authIdCode != null) {
            data += authIdCode;
        }
        if (firstName != null) {
            data += firstName;
        }
        if (lastName != null) {
            data += lastName;
        }
        if (pgRespCode != null) {
            data += pgRespCode;
        }
        if (zipCode != null) {
            data += zipCode;
        }

        try {
            computedSignature = RequestSignature.generateHMAC(data, CITRUS_PAY_PG_MERCHANT_SECRET_KEY);
            if (responseSignature != null && !responseSignature.equalsIgnoreCase("")
                    && !computedSignature.equalsIgnoreCase(responseSignature)) {
                flag = false;
            }
        }
        catch (Exception e) {
            logger.error("Error computing signature", e);
        }

        if (!flag) {
            throw new IllegalArgumentException("Invalid response signature");
        }
    }

    private EnquiryCollection fetchEnquiryCollection(int transactionId) {
        String key = CITRUS_PAY_PG_MERCHANT_SECRET_KEY;
        com.citruspay.pg.util.CitruspayConstant.merchantKey = key;
        String merchantId = CITRUS_PAY_PG_MERCHANT_ACCESS_KEY;
        String merchantTxnId = String.valueOf(transactionId);
        Map<String, Object> map = new HashMap<>();
        map.put("merchantAccessKey", merchantId);
        map.put("transactionId", merchantTxnId);

        EnquiryCollection enquiryResult = null;
        try {
            enquiryResult = com.citruspay.pg.model.Enquiry.create(map);
        }
        catch (CitruspayException e) {
            logger.error("Could not fetch payment details", e);
        }

        return enquiryResult;
    }

    public void updateDetails(Transaction transaction) {
        TransactionStatus transactionStatus = null;
        PaymentStatus paymentStatus = null;
        Enquiry lastEnquiry = null;

        EnquiryCollection enquiryCollection = fetchEnquiryCollection(transaction.getId());
        if (enquiryCollection != null && enquiryCollection.getEnquiry()!= null && ENQUIRY_COLLECTION_SUCCESS_RESPONSE_CODE.equals(enquiryCollection
                .getRespCode())) {
            Enquiry lastEnquiryFound = null;
            boolean wasPaymentDone = false;
            for (Enquiry enquiry : enquiryCollection.getEnquiry()) {
                lastEnquiryFound = enquiry;
                if (enquiry.getRespCode().equals(EnquiryResponseCode.SuccessPayment.getResponseCode())) {
                    if (CitrusPayPGEnquiryTransactionType.SALE.name().equalsIgnoreCase(enquiry.getTxnType())) {
                        paymentStatus = PaymentStatus.Success;
                        transactionStatus = TransactionStatus.Complete;
                        lastEnquiry = enquiry;
                        wasPaymentDone = true;
                    }
                }
                else if (enquiry.getRespCode().equals(EnquiryResponseCode.RefundSuccess.getResponseCode())) {

                    if (CitrusPayPGEnquiryTransactionType.REFUND.name().equalsIgnoreCase(enquiry.getTxnType())) {
                        paymentStatus = PaymentStatus.Refunded;
                        transactionStatus = TransactionStatus.Refunded;
                        lastEnquiry = enquiry;
                        break;
                    }
                }
            }

            if (lastEnquiry != null) {
                if (lastEnquiry.getCurrency().equalsIgnoreCase(CURRENCY_INR)) {
                    if (transactionStatus == TransactionStatus.Complete) {
                        if (transaction.getStatusId() == TransactionStatus.Incomplete.getId()) {
                            handleSuccessPayment(transaction, transactionStatus, paymentStatus, lastEnquiry);
                        }
                        else if (transaction.getStatusId() == TransactionStatus.RefundInitiated.getId()) {
                            handleRefundInitiatedTransaction(transaction);
                        }
                    }
                    // TODO only for completed state inventory should be reduced
                    else if (transactionStatus == TransactionStatus.Refunded && (transaction.getStatusId() == TransactionStatus.Complete
                            .getId() || transaction.getStatusId() == TransactionStatus.RefundInitiated.getId())) {
                        handleRefundPayment(transaction, paymentStatus, lastEnquiry);
                    }
                }
                else {
                    logger.error("Currency mismatch - Found: " + lastEnquiry.getCurrency() + ", Expected: INR");
                }
            }
            // Transaction was not successful. It was neither refunded or sale.
            else {
                // If there never was any successful payment done then it
                // qualify for incomplete payment failure.
                // Only for transactions that are marked as incomplete.
                handleTransactionFailure(transaction, wasPaymentDone, lastEnquiryFound);
            }

        }
    }

    @Transactional
    /**
     * If there never was any successful payment done then it qualify for incomplete payment failure. Only for transactions that are marked as incomplete.
     * @param transaction
     * @param wasPaymentDone
     * @param lastEnquiryFound
     */
    private void handleTransactionFailure(Transaction transaction, boolean wasPaymentDone, Enquiry lastEnquiryFound) {
        if (lastEnquiryFound != null && lastEnquiryFound.getRespCode().equalsIgnoreCase(
                EnquiryResponseCode.FailPayment.getResponseCode())
                && !wasPaymentDone
                && transaction.getStatusId() == TransactionStatus.Incomplete.getId()) {
            
            transactionService.updateTransactionStatusByOldStatus(
                    transaction.getId(),
                    TransactionStatus.TransactionCancelled,
                    TransactionStatus.Incomplete);
            couponNotificationService.notifyUserOnPaymentFailure(transaction);
        }
    }

    /**
     * If refund was not successful then the refund initiated transaction should
     * be change to complete state.
     * 
     * @param transaction
     */
    private void handleRefundInitiatedTransaction(Transaction transaction) {
        transactionService.updateTransactionStatusByOldStatus(
                transaction.getId(),
                TransactionStatus.Complete,
                TransactionStatus.RefundInitiated);
    }

    private void handleRefundPayment(Transaction transaction, PaymentStatus paymentStatus, Enquiry lastEnquiry) {
        if (Math.abs(Double.valueOf(lastEnquiry.getAmount()) - transaction.getAmount()) < 0.01) {
            int transactionOldStatusId = transaction.getStatusId();
            Payment payment = createPaymentFromEnquiry(transaction, lastEnquiry);
            payment.setStatusId(paymentStatus.getId());
            transaction.setStatusId(TransactionStatus.Refunded.getId());
            paymentService.save(payment);
            transactionService.save(transaction);
            /**
             * check if coupon has been granted only then refund it.
             */
            if (transaction.getCode() != null && (transactionOldStatusId == TransactionStatus.Complete.getId() || transactionOldStatusId == TransactionStatus.RefundInitiated
                    .getId())) {

                couponCatalogueService.updateCouponCatalogueInventoryLeft(transaction.getProductId(), 1);
            }

            couponNotificationService.notifyUserOnRefund(
                    transaction,
                    couponCatalogueService.getCouponCatalogue(transaction.getProductId()));

        }
        else {
            logger.error("Amount mismatch - Found: " + lastEnquiry.getAmount()
                    + ", Expected: "
                    + transaction.getAmount());
        }
    }

    /**
     * TODO to review the transactional annotation as payment need to saved
     * compulsory. Payment saving is must.
     * 
     * @param transaction
     * @param transactionStatus
     * @param paymentStatus
     * @param lastEnquiry
     */
    @Transactional
    private synchronized void handleSuccessPayment(
            Transaction transaction,
            TransactionStatus transactionStatus,
            PaymentStatus paymentStatus,
            Enquiry lastEnquiry) {
        
        // getting the latest transaction in the database.
        transaction = transactionService.getTransaction(transaction.getId());
        // Only Incomplete Transaction are allowed to proceed further.
        if(transaction.getStatusId() != TransactionStatus.Incomplete.getId()){
            return;
        }
        
        if (Math.abs(Double.valueOf(lastEnquiry.getAmount()) - transaction.getAmount()) < 0.01) {

            CouponCatalogue couponCatalogue = couponCatalogueService.updateCouponCatalogueInventoryLeft(
                    transaction.getProductId(),
                    -1);

            // Coupon Inventory did not get updated.
            if (couponCatalogue == null) {
                handleRefundByTransactionId(transaction, false);
                /**
                 * No need to process further. As transaction has been refunded.
                 * Hence returning.
                 */
                return;
                /*
                 * transactionStatus = TransactionStatus.Refunded; paymentStatus
                 * = PaymentStatus.Refunded; initiateRefund(transaction,
                 * lastEnquiry);
                 */
            }
            else {
                transaction.setCode(createCouponCode(transaction));
            }

            Payment payment = createPaymentFromEnquiry(transaction, lastEnquiry);
            payment.setStatusId(paymentStatus.getId());
            transaction.setStatusId(transactionStatus.getId());

            paymentService.save(payment);
            transactionService.save(transaction);

            if (transaction.getStatusId() == TransactionStatus.Complete.getId()) {
                couponNotificationService.notifyUserOnCouponBuy(transaction, couponCatalogue);
            }

        }
        else {
            // TODO Refund or punish
            logger.error("Amount mismatch - Found: " + lastEnquiry.getAmount()
                    + ", Expected: "
                    + transaction.getAmount());
        }
    }

    private boolean initiateRefund(Transaction transaction, Enquiry lastEnquiry) {
        CitruspayConstant.merchantKey = CITRUS_PAY_PG_MERCHANT_SECRET_KEY;
        Map<String, Object> params = new HashMap<>();
        params.put("merchantAccessKey", CITRUS_PAY_PG_MERCHANT_ACCESS_KEY);
        params.put("transactionId", String.valueOf(transaction.getId()));
        params.put("pgTxnId", lastEnquiry.getPgTxnId());
        params.put("RRN", lastEnquiry.getRrn());
        params.put("authIdCode", lastEnquiry.getAuthIdCode());
        params.put("currencyCode", lastEnquiry.getCurrency());
        params.put("txnType", lastEnquiry.getTxnType());
        params.put("amount", lastEnquiry.getAmount());
        params.put("bankName", CITRUS_PAY_PG_MERCHANT_BANKNAME);

        Refund refund = null;
        try {
            refund = Refund.create(params);
        }
        catch (CitruspayException e) {
            logger.error("Error while auto initiating refund for transaction id: " + transaction.getId(), e);
            return false;
        }

        if (refund == null || !SUCCESS_RESPONSE_CODE.equalsIgnoreCase(refund.getRespCode())) {
            logger.error("Error while auto initiating refund for transaction id: " + transaction.getId()
                    + ". Need to refund manually.");
            return false;
            // Tokens.CouponCode
            // Tokens.Date;
            //
            // notificationMessageService.createNotificationMessage(NotificationTypeEnum.CouponRefunded,
            // userId, payloadMap)
            // notificationGeneratedService.createNotificationGenerated(nMessages,
            // mediumTypes)

            // TODO send notification
        }

        return true;
    }

    private boolean existsProductInventory(Transaction transaction) {
        return couponCatalogueService.isPurchasable(transaction.getProductId());
    }

    public Payment createPaymentFromEnquiry(Transaction transaction, Enquiry lastEnquiry) {
        Payment payment = new Payment();
        payment.setTransactionId(transaction.getId());
        payment.setAmount(Double.valueOf(lastEnquiry.getAmount()).intValue());
        payment.setCitrusPayGatewayTransactionId(lastEnquiry.getTxnId());
        payment.setGatewayTransactionId(Long.valueOf(lastEnquiry.getPgTxnId()));
        payment.setPaymentGatewayResponseId(null);
        payment.setTypeId(PaymentType.Online.getId());
        return payment;
    }

    public boolean handleRefundByTransactionId(Transaction transaction, boolean incrementCouponInventory) {
        Object[] transactionStatusData = checkTransactionStatus(transaction);
        Enquiry lastEnquiry = (Enquiry) transactionStatusData[0];
        TransactionStatus transactionStatus = (TransactionStatus) transactionStatusData[1];
        PaymentStatus paymentStatus = (PaymentStatus) transactionStatusData[2];

        if (!transactionStatus.equals(TransactionStatus.Complete)) {
            // TODO handle this case.
            return false;
        }

        boolean refundStatus = initiateRefund(transaction, lastEnquiry);

        // If refund Status failed then return false;
        // handle failure status.
        if (!refundStatus) {
            return false;
        }

        transactionStatusData = checkTransactionStatus(transaction);
        lastEnquiry = (Enquiry) transactionStatusData[0];
        transactionStatus = (TransactionStatus) transactionStatusData[1];
        paymentStatus = (PaymentStatus) transactionStatusData[2];

        if (!transactionStatus.equals(TransactionStatus.Refunded)) {
            // TODO handle this case.
            return false;
        }

        citrusPayPGTransactionService.saveRefundTransaction(transaction, lastEnquiry, incrementCouponInventory);

        return true;
    }

    private Object[] checkTransactionStatus(Transaction transaction) {

        EnquiryCollection enquiryCollection = fetchEnquiryCollection(transaction.getId());

        if (enquiryCollection == null || enquiryCollection.getEnquiry() == null
                || enquiryCollection.getEnquiry().isEmpty()) {
            throw new BadRequestException(" Transaction Id " + transaction.getId() + " does not exists.");
        }
        Enquiry lastEnquiry = null;
        TransactionStatus transactionStatus = null;
        PaymentStatus paymentStatus = null;

        if (enquiryCollection != null && ENQUIRY_COLLECTION_SUCCESS_RESPONSE_CODE.equals(enquiryCollection
                .getRespCode())) {
            for (Enquiry enquiry : enquiryCollection.getEnquiry()) {
                if (enquiry.getRespCode().equals(EnquiryResponseCode.SuccessPayment.getResponseCode())) {

                    if (CitrusPayPGEnquiryTransactionType.SALE.name().equalsIgnoreCase(enquiry.getTxnType())) {
                        paymentStatus = PaymentStatus.Success;
                        transactionStatus = TransactionStatus.Complete;
                        lastEnquiry = enquiry;
                    }

                }
                else if (enquiry.getRespCode().equals(EnquiryResponseCode.RefundSuccess.getResponseCode())) {

                    if (CitrusPayPGEnquiryTransactionType.REFUND.name().equalsIgnoreCase(enquiry.getTxnType())) {
                        paymentStatus = PaymentStatus.Refunded;
                        transactionStatus = TransactionStatus.Refunded;
                        lastEnquiry = enquiry;
                        // already refunded.
                        break;
                    }

                }

                else if (enquiry.getRespCode().equals(EnquiryResponseCode.RefundInitiated.getResponseCode())) {
                    transactionStatus = TransactionStatus.RefundInitiated;
                }
            }
        }

        Object transactionStatusData[] = new Object[3];
        transactionStatusData[0] = (Object) lastEnquiry;
        transactionStatusData[1] = (Object) transactionStatus;
        transactionStatusData[2] = (Object) paymentStatus;

        return transactionStatusData;
    }

    public void updateRefundableTransaction() {
        List<Transaction> transactions = transactionService.getRefundableTransactions();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                updateDetails(transaction);
            }
        }
    }

}