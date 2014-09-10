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

    @Query("SELECT LPr FROM ListingPrice LPr JOIN fetch LPr.listing L where LPr.id IN ?1")
    public List<ListingPrice> getListingPrice(List<Integer> listingPriceId);

    @Query(" SELECT MAX(LP.id) as maxId FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId IN ?1 AND LP.version = 'Website' GROUP BY L.propertyId")
    public List<Integer> getListingPriceIds(List<Integer> propertyId);

}
