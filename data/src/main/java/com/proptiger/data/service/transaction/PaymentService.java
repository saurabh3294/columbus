/**
 * 
 */
package com.proptiger.data.service.transaction;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.model.enums.transaction.PaymentStatus;
import com.proptiger.data.model.enums.transaction.PaymentType;
import com.proptiger.data.model.transaction.Payment;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.repo.transaction.PaymentDao;
import com.proptiger.exception.BadRequestException;

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
    
    public Payment createOfflinePayment(int amount, int paymentTypeId, PaymentStatus paymentStatus, int transactionId){
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setTypeId(paymentTypeId);
        payment.setStatusId(paymentStatus.getId());
        payment.setTransactionId(transactionId);
        
        return payment;
    }
}
