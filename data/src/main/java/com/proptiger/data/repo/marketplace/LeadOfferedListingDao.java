package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.LeadOfferedListing;

/**
 * 
 * @author azi
 * 
 */
public interface LeadOfferedListingDao extends JpaRepository<LeadOfferedListing, Integer> {
    public List<LeadOfferedListing> findByLeadOfferId(int leadOfferId);
}
