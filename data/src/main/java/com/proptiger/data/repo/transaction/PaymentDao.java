/**
 * 
 */
package com.proptiger.data.repo.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.transaction.Payment;

/**
 * @author mandeep
 *
 */
@Repository
public interface PaymentDao extends JpaRepository<Payment, Integer> {
}
