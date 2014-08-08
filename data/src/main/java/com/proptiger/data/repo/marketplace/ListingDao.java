package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Listing;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer>{

    List<Listing> findBySellerId(Integer sellerId);
    
    Listing findBySellerIdAndId(Integer sellerId, Integer listingId);
}
