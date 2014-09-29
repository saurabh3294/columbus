/**
 * 
 */
package com.proptiger.data.repo.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.transaction.thirdparty.PaymentGatewayResponse;

/**
 * @author mandeep
 *
 */
@Repository
public interface CitrusPayPGResponseDao extends JpaRepository<PaymentGatewayResponse, Integer> {
}
