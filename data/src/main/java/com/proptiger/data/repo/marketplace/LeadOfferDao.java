package com.proptiger.data.repo.marketplace;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;

public interface LeadOfferDao extends JpaRepository<LeadOffer , Integer>, LeadOfferCustomDao {
    @Query("select count(LO) from LeadOffer LO where LO.leadId = ?1")
    int getCountClaimed(int lead_id);    

    @Query("select LO from LeadOffer LO join LO.lead L where L.cityId = ?1 and LO.statusId not in (7,8,9) and L.clientId = ?2 and L.mergedLeadId is null order by LO.id desc")
    public List<LeadOffer> getOpenLeadOffers(int cityId , int clientId);

    @Query("select LO from LeadOffer LO join LO.lead L where L.cityId = ?1 and L.clientId = ?2 order by LO.id desc")
    public List<LeadOffer> getLeadOffers(int cityId , int clientId);

    @Query("select LO from LeadOffer LO where LO.leadId = ?1 order by LO.statusId")
    public List<LeadOffer> getLeadOffers(int leadId);

    @Query("select LO,L from LeadOffer LO join LO.lead L where LO.agentId = ?1")
    List<LeadOffer> getAllLeadOffers(int agentId);

    @Query("select LI from LeadOffer LO.listings LI where LI.id in (?1)")
    List<Listing> getListings(List<Integer> leadOfferIds);
        
}
