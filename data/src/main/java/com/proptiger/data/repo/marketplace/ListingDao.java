package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer> {
    
    @Query("SELECT LP FROM ListingPrice LP where LP.id IN (SELECT MAX(LP.id) FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId = ?1 AND LP.version='Website') ")
    public ListingPrice getListingPrice(Integer propertyId);    

}
