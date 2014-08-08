package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.enums.Status;
import com.proptiger.data.model.Listing;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer>{

    List<Listing> findBySellerIdAndStatus(Integer sellerId, Status status);
    
    Listing findBySellerIdAndIdAndStatus(Integer sellerId, Integer listingId, Status status);
}
