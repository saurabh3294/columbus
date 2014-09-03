package com.proptiger.data.repo.marketplace;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.Listing;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOffer.CountListingObject;
import com.proptiger.data.model.marketplace.LeadOfferedListing;

public interface LeadOfferDao extends JpaRepository<LeadOffer, Integer>, LeadOfferCustomDao {
    @Query("select count(LO) from LeadOffer LO join LO.masterLeadOfferStatus MLOS where LO.leadId = ?1 and MLOS.claimedFlag = 1")
    long getCountClaimed(Integer leadId);

    @Query("select LO from LeadOffer LO join LO.lead L where L.cityId = ?1 and L.clientId = ?2 order by LO.id desc")
    public List<LeadOffer> getLeadOffers(int cityId, int clientId);

    @Query("select LO from LeadOffer LO where LO.leadId = ?1 order by LO.statusId")
    public List<LeadOffer> getLeadOffers(int leadId);

    @Query("select LO from LeadOffer LO join fetch LO.lead L join fetch LO.masterLeadOfferStatus MLOS where LO.agentId = ?1")
    public List<LeadOffer> getLeadOffersForAgent(int agentId);

    @Query("select LOL from LeadOfferedListing LOL join fetch LOL.listing LI join fetch LI.currentListingPrice join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.builder join fetch LIPP.locality where LIPP.version='Website' and LOL.leadOfferId in (?1)")
    public List<LeadOfferedListing> getLeadOfferedListings(List<Integer> leadOfferIds);

    @Query("select LO from LeadOffer LO join fetch LO.masterLeadOfferStatus MLOS where LO.id = ?1 and LO.agentId = ?2")
    public LeadOffer findByIdAndAgentId(int leadOfferId, Integer userIdentifier);

    @Query("select LI from LeadOffer LO join LO.matchingListings LI join fetch LI.currentListingPrice join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.builder join fetch LIPP.locality where LO.id = ?1 and LO.lead.cityId = LI.property.project.locality.suburb.cityId and LI.status = 'Active' and LIPP.version='Website' group by LI")
    public List<Listing> getMatchingListings(int leadOfferId);
    

    @Query("select LO from LeadOffer LO join fetch LO.lead L where LO.id = ?1")
    public LeadOffer findById(int leadOfferId);

    public List<LeadOffer> findByLeadId(int leadId);

    @Modifying
    @Transactional
    @Query("update LeadOffer LO set LO.statusId = 2 where LO.statusId = 1 and LO.leadId = ?1")
    void expireRestOfTheLeadOffers(int leadId);

    @Query("select MAX(LOL.id) from LeadOfferedListing LOL where LOL.leadOfferId in (?1) group by LOL.leadOfferId")
    public List<Integer> findMaxListingByLeadOfferIdGroupbyLeadOfferId(List<Integer> leadOfferIds);

    @Query(
            nativeQuery = true,
            value = "select lo.* from marketplace.lead_offers lo inner join marketplace.lead_tasks lt on lo.next_task_id = lt.id left join marketplace.notifications n on lt.id = n.object_id and n.notification_type_id = ?3 where n.id is null and lt.scheduled_for between ?1 and ?2")
    public List<LeadOffer> getOffersWithTaskScheduledBetweenAndWithoutNotification(
            Date startTime,
            Date endTime,
            int notificationTypeId);

    @Query(
            nativeQuery = true,
            value = "select lo.* from marketplace.lead_offers lo inner join marketplace.lead_tasks lt on lo.next_task_id = lt.id inner join marketplace.master_lead_task_status_mappings ltsm on lt.lead_task_status_id = ltsm.id left join marketplace.notifications n on lt.id = n.object_id and n.notification_type_id = ?3 where n.id is null and lt.scheduled_for between ?1 and ?2 and ltsm.master_task_id in (?4)")
    public List<LeadOffer> getOffersWithTaskScheduledBetweenAndWithoutNotification(
            Date startTime,
            Date endTime,
            int notificationTypeId,
            List<Integer> masterTaskIds);

    @Query(nativeQuery = true, value = "select * from marketplace.lead_offers where id = ?1 for update")
    public LeadOffer getLock(int ledOfferId);

    @Query("SELECT NEW com.proptiger.data.model.marketplace.LeadOffer$CountListingObject(LO.id,count(LI)) from LeadOffer LO join LO.matchingListings LI join  LI.property LIP  join  LIP.project LIPP  join LIPP.builder  join  LIPP.locality where LO.id in (?1) and LO.lead.cityId = LI.property.project.locality.suburb.cityId and LI.status = 'Active' and LIPP.version='Website' group by LO")
    List<CountListingObject> getMatchingListingCount(List<Integer> leadOfferIds);
}
