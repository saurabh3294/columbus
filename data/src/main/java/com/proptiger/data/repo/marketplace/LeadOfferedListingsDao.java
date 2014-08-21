package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.LeadOfferedListing;

public interface LeadOfferedListingsDao extends JpaRepository<LeadOfferedListing, Integer> {
    public List<LeadOfferedListing> findByLeadOfferIdIn(List<Integer> leadOfferIds);

    public List<LeadOfferedListing> findByLeadOfferIdAndListingIdIn(int leadOfferId, List<Integer> listingIds);
}
