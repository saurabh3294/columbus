package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer> {
    
    @Query("SELECT LPr FROM ListingPrice LPr JOIN fetch LPr.listing L where LPr.id IN (SELECT MAX(LP.id) AS listing_price_id FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId IN ?1 AND LP.version='Website' GROUP BY L.propertyId) ")
    public List<ListingPrice> getListingPrice(List<Integer> propertyId);    

}
