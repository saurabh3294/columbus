/**
 * 
 */
package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.MasterLeadOfferStatus;

/**
 * @author Anubhav
 * 
 */

public interface MasterLeadOfferStatusDao extends JpaRepository<MasterLeadOfferStatus, Integer> {
    public MasterLeadOfferStatus findById(int statusId);
}
