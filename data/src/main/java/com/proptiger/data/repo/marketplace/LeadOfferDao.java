package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOfferedListing;

public interface LeadOfferDao extends JpaRepository<LeadOffer, Integer>, LeadOfferCustomDao {
    @Query("select count(LO) from LeadOffer LO join LO.masterLeadOfferStatus MLOS where LO.leadId = ?1 and MLOS.claimedFlag = 1")
    long getCountClaimed(Integer leadId);

    @Query("select LO from LeadOffer LO join LO.lead L where L.cityId = ?1 and L.clientId = ?2 order by LO.id desc")
    public List<LeadOffer> getLeadOffers(int cityId, int clientId);

    @Query("select LO from LeadOffer LO where LO.leadId = ?1 order by LO.statusId")
    public List<LeadOffer> getLeadOffers(int leadId);

    @Query("select LO from LeadOffer LO join fetch LO.lead L where LO.agentId = ?1")
    public List<LeadOffer> getLeadOffersForAgent(int agentId);

    @Query("select LOL from LeadOfferedListing LOL join fetch LOL.listing LI where LOL.leadOfferId in (?1)")
    public List<LeadOfferedListing> getLeadOfferedListings(List<Integer> leadOfferIds);

    public LeadOffer findByIdAndAgentId(int leadOfferId, Integer userIdentifier);

    @Query("select LI from LeadOffer LO join LO.matchingListings LI join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.locality where LO.id = ?1 and LO.lead.cityId = LI.property.project.locality.suburb.cityId and LI.status = 'Active' group by LI")
    public List<Listing> getMatchingListings(int leadOfferId);

    @Query("select LO from LeadOffer LO join fetch LO.lead L where LO.id = ?1")
    public LeadOffer findById(int leadOfferId);

    public List<LeadOffer> findByLeadId(int leadId);

    @Modifying
    @Transactional
    @Query("update LeadOffer LO set LO.statusId= ?2  where LO.statusId = ?3 and LO.leadId = ?1")
    void expireRestOfTheLeadOffers(int leadId, Integer expired, Integer offered);

}
