/**
 * 
 */
package com.proptiger.data.service.transaction;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.model.enums.transaction.TransactionStatus;
import com.proptiger.data.model.enums.transaction.TransactionType;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.transaction.TransactionDao;
import com.proptiger.data.service.CouponCatalogueService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.DateUtil;
import com.proptiger.exception.ProAPIException;

/**
 * @author mandeep
 * 
 */
@Service
public class TransactionService {
    @Value("${transaction.peruser.max.count}")
    private int                    MAX_COUPON_PER_USER;

    @Value("${transaction.refund.days.count}")
    private int                    MAX_REFUND_PERIOD;

    @Autowired
    private TransactionDao         transactionDao;

    @Autowired
    private UserService            userService;

    @Autowired
    private ApplicationContext     applicationContext;

    // do not autowire this class.
    private CitrusPayPGService     citrusPayPGService;

    @Autowired
    private CouponCatalogueService couponCatalogueService;

    public Transaction createTransaction(Transaction transaction) {
        transaction.getUser().setRegistered(false);
        User user = userService.createUser(transaction.getUser());

        validateMaxCouponsBought(user.getId());

        // Trying to reuse existing transaction wherever possible
        // TODO - remove as one user's payment might not be inserted because of same
        List<Transaction> transactions = transactionDao.getExistingReusableTransactions(user.getId());
        if (transactions != null && !transactions.isEmpty()) {
            transaction.setId(transactions.get(0).getId());
            transaction.setCreatedAt(transactions.get(0).getCreatedAt());
        }
        else {
            transaction.setId(null);
        }

        transaction.setUserId(user.getId());

        if (transaction.getTypeId() == TransactionType.BuyCoupon.getId()) {
            if (!couponCatalogueService.isPurchasable(transaction.getProductId())) {
                throw new ProAPIException(ResponseCodes.COUPONS_SOLD_OUT, "Coupons sold out!");
            }

            transaction.setAmount(couponCatalogueService.getCouponCatalogue(transaction.getProductId())
                    .getCouponPrice());
        }

        transaction.setStatusId(TransactionStatus.Incomplete.getId());
        transaction = transactionDao.saveAndFlush(transaction);
        transaction.setUser(user);
        return transaction;
    }

    private void validateMaxCouponsBought(int userId) {
        List<Transaction> transactions = transactionDao.getCompletedTransactionsForUser(userId);
        if (transactions != null && transactions.size() >= MAX_COUPON_PER_USER) {
            throw new ProAPIException(
                    ResponseCodes.MAX_COUPON_BUY_LIMIT,
                    "Cannot purchase more than " + MAX_COUPON_PER_USER + " coupon(s)");
        }
    }

    public Transaction getUpdatedTransaction(int transactionId) {
        Transaction transaction = transactionDao.findOne(transactionId);
        List<Transaction> transactions = transactionDao.getExistingReusableTransactions(transaction.getUserId());
        if (transactions != null && !transactions.isEmpty()
                && transaction.getStatusId() == TransactionStatus.Incomplete.getId()) {
            getCitrusPayPGService().updateDetails(transaction);
        }

        transaction = transactionDao.findOne(transactionId);
        if (transaction.getTypeId() == TransactionType.BuyCoupon.getId()) {
            transaction.setUser(userService.getUserById(transaction.getUserId()));
            userService.enrichUserDetails(transaction.getUser());
            transaction.setProduct(couponCatalogueService.getCouponCatalogue(transaction.getProductId()));
        }

        return transaction;
    }

    public Transaction getCompletedTransactionForUserAndCouponCode(int userId, String couponCode) {
        return transactionDao.getTransaction(userId, couponCode);
    }

    public Transaction getTransaction(int transactionId) {
        return transactionDao.findOne(transactionId);
    }

    public void save(Transaction transaction) {
        transactionDao.saveAndFlush(transaction);
    }

    public List<Transaction> getRefundableTransactions() {
        return transactionDao.getRefundableTransactions(DateUtil.addDays(new Date(), -1 * MAX_REFUND_PERIOD));
    }

    public Transaction getNonRedeemTransactionByCode(String code) {
        return transactionDao.getNonExercisedTransactionByCode(code);
    }

    public Transaction getTransactionsByCouponCode(String code) {
        return transactionDao.getTransactionByCode(code);
    }

    @Transactional
    public int updateCouponRedeem(Transaction transaction) {
        return transactionDao.updateCouponAsRedeem(transaction.getId());
    }

    private CitrusPayPGService getCitrusPayPGService() {
        if (citrusPayPGService == null) {
            citrusPayPGService = applicationContext.getBean(CitrusPayPGService.class);
        }
        return citrusPayPGService;
    }
}
