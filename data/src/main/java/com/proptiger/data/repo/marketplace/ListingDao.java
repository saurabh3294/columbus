package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.Enquiry.EnquiryCustomDetails;
import com.proptiger.data.model.ForumUser.WhoAmIDetail;
import com.proptiger.data.model.ListingPrice;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer> {
    
    @Query("SELECT LP FROM ListingPrice LP where LP.id IN (SELECT MAX(LP.id) FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId = ?1) ")
    public ListingPrice getListingPrice(Integer propertyId);    

}
