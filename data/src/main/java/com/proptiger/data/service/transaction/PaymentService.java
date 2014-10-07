/**
 * 
 */
package com.proptiger.data.service.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.enums.transaction.PaymentStatus;
import com.proptiger.data.model.enums.transaction.PaymentType;
import com.proptiger.data.model.transaction.Payment;
import com.proptiger.data.repo.transaction.PaymentDao;

/**
 * @author mandeep
 *
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;
    
    public void save(Payment payment) {
        paymentDao.saveAndFlush(payment);        
    }
    
    public Payment createOfflinePayment(int amount, PaymentType paymentType, PaymentStatus paymentStatus, int transactionId){
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setTypeId(paymentType.getId());
        payment.setStatusId(paymentStatus.getId());
        payment.setTransactionId(transactionId);
        
        return payment;
    }

}
