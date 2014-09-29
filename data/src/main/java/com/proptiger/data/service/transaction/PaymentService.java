/**
 * 
 */
package com.proptiger.data.service.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
