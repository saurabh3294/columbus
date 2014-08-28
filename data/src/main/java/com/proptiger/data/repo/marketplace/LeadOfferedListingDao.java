package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.LeadOfferedListing;

/**
 * 
 * @author azi
 * 
 */
public interface LeadOfferedListingDao extends JpaRepository<LeadOfferedListing, Integer> {
    public List<LeadOfferedListing> findByLeadOfferId(int leadOfferId);
    public List<LeadOfferedListing> findByLeadOfferIdIn(List<Integer> leadOfferIds);

    public List<LeadOfferedListing> findByLeadOfferIdAndListingIdIn(int leadOfferId, List<Integer> listingIds);

    @Query("select LOL from LeadOfferedListing LOL join fetch LOL.listing LI where LOL.id in (?1)")
    public List<LeadOfferedListing> getListingsById(List<Integer> maxleadOfferedListingIds);
}
