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

    @Query("select LOL from LeadOfferedListing LOL where LOL.leadOfferId in (?1) and LOL.listingId in (?2)")
    public List<LeadOfferedListing> findByLeadOfferIdAndListingIdIn(int leadOfferId, List<Integer> listingIds);

    @Query("select DISTINCT(LOL) from LeadOfferedListing LOL join fetch LOL.listing LI left join fetch LI.projectSupply left join fetch LI.currentListingPrice join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.projectStatusMaster join fetch LIPP.builder join fetch LIPP.locality LIPPL join fetch LIPPL.suburb LIPPLS join fetch LIPPLS.city where LIPP.version = 'Website' and LOL.id in (?1)")
    public List<LeadOfferedListing> getListingsById(List<Integer> maxleadOfferedListingIds);
}
