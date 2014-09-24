/**
 * 
 */
package com.proptiger.data.service.transaction;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.enums.transaction.TransactionStatus;
import com.proptiger.data.model.enums.transaction.TransactionType;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.transaction.TransactionDao;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.DateUtil;

/**
 * @author mandeep
 * 
 */
@Service
public class TransactionService {
    @Value("${transaction.peruser.max.count}")
    private static final int MAX_COUPON_PER_USER = 3;
    
    @Value("${transaction.refund.days.count}")
    private static final int MAX_REFUND_PERIOD = 30;
    
    @Autowired
    private TransactionDao     transactionDao;

    @Autowired
    private UserService        userService;

    @Autowired
    private CitrusPayPGService citrusPayPGService;

    public Transaction createTransaction(Transaction transaction) {
        transaction.getUser().setRegistered(false);
        User user = userService.createUser(transaction.getUser());

        validateMaxCouponsBought(user.getId());
        checkProductInventory(transaction);

        // Trying to reuse existing transaction wherever possible
        List<Transaction> transactions = transactionDao.getExistingReusableTransactions(user.getId());
        if (transactions != null && !transactions.isEmpty()) {
            transaction.setId(transactions.get(0).getId());
            transaction.setCreatedAt(new Date());
        }
        else {
            transaction.setId(null);
        }

        transaction.setUserId(user.getId());

        // TODO - To be replaced by actual coupon service
        transaction.setAmount(1);

        transaction.setStatusId(TransactionStatus.Incomplete.getId());
        transaction = transactionDao.saveAndFlush(transaction);
        transaction.setUser(user);
        return transaction;
    }

    // TODO
    private void checkProductInventory(Transaction transaction) {
        if (transaction.getTypeId() == TransactionType.BuyCoupon.getId()) {
            
        }
    }

    private void validateMaxCouponsBought(int userId) {
        List<Transaction> transactions = transactionDao.getCompletedTransactionsForUser(userId);
        if (transactions != null && transactions.size() >= MAX_COUPON_PER_USER) {
            throw new IllegalArgumentException("Cannot purchase more than " + MAX_COUPON_PER_USER + " coupon(s)");
        }
    }

    public Transaction getUpdatedTransaction(int transactionId) {
        Transaction transaction = transactionDao.findOne(transactionId);
        List<Transaction> transactions = transactionDao.getExistingReusableTransactions(transaction.getUserId());
        if (transactions != null && !transactions.isEmpty()
                && transaction.getStatusId() == TransactionStatus.Incomplete.getId()) {
            citrusPayPGService.updateDetails(transaction);
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
    
    public Transaction getNonRedeemTransactionByCode(String code){
        return transactionDao.getNonExercisedTransactionByCode(code);
    }
    
    @Transactional
    public int updateCouponRedeem(Transaction transaction){
        return transactionDao.updateCouponAsRedeem(transaction.getId());
    }
}
