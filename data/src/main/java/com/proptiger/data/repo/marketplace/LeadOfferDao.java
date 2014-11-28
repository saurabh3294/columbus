package com.proptiger.data.repo.marketplace;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.model.cms.Listing;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOffer.CountListingObject;

public interface LeadOfferDao extends JpaRepository<LeadOffer, Integer>, LeadOfferCustomDao {
    public List<LeadOffer> findByLeadId(int leadId);

    public LeadOffer findByLeadIdAndAgentId(int leadId, int agentId);

    public List<LeadOffer> findByLeadIdAndStatusId(int leadId, int statusId);

    @Query(nativeQuery = true, value = "select * from marketplace.lead_offers where id = ?1 for update")
    public LeadOffer getLock(int ledOfferId);

    @Query("select max(LO.cycleId) from LeadOffer LO where LO.leadId = ?1 and LO.phaseId = ?2")
    Integer getMaxCycleIdByLeadIdAndPhaseId(int leadId, int phaseId);

    @Query("select count(LO) from LeadOffer LO where LO.leadId = ?1 and LO.phaseId = ?2")
    long getCountByLeadIdAndPhaseId(int leadId, int phaseId);

    @Query("select MAX(LO.phaseId) from LeadOffer LO where LO.leadId = ?1")
    Integer getMaxPhaseIdByLeadId(int leadId);

    @Query("select count(LO.id) from LeadOffer LO where LO.agentId = ?1 and LO.statusId = ?2")
    long getCountByAgentIdAndStatusId(int userId, int statusId);

    @Query("select LO from LeadOffer LO join fetch LO.masterLeadOfferStatus MLOS join fetch LO.lead L where LO.id = ?1")
    public LeadOffer getById(int leadOfferId);

    @Query(
            value = "SELECT DISTINCT LO FROM LeadOffer LO JOIN FETCH LO.lead L JOIN FETCH L.requirements LR WHERE LO.id = ?1")
    LeadOffer getByIdWithRequirements(int id);

    @Query("select LO from LeadOffer LO join fetch LO.masterLeadOfferStatus where LO.leadId = ?1 order by LO.statusId")
    public List<LeadOffer> getByLeadId(int leadId);

    // XXX Hard coding for Banglore for faster retrieval
    @Query("select LI from LeadOffer LO join LO.matchingListings LI left join fetch LI.projectSupply left join fetch LI.currentListingPrice join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.projectStatusMaster join fetch LIPP.builder join fetch LIPP.locality LIPPL join fetch LIPPL.suburb LIPPLS join fetch LIPPLS.city where LO.id = ?1 and LI.property.project.locality.suburb.cityId = 2 and LI.status = 'Active' and LIPP.version='Website' and LI.sellerId = ?2 group by LI")
    public List<Listing> getMatchingListings(int leadOfferId, int userId);

    @Query("select distinct(LO) from LeadOffer LO join LO.masterLeadOfferStatus LOM where LO.leadId = ?1 and LOM.claimedFlag = ?2 and LOM.openFlag = ?3")
    public List<LeadOffer> getByLeadIdAndOpenFlagAndClaimedFlag(int leadId, boolean openFlag, boolean claimedFlag);

    @Modifying
    @Transactional
    @Query("update LeadOffer LO set LO.statusId = ?3 where LO.statusId = 2 and LO.leadId = ?1")
    void updateLeadOfferStatus(int leadId, int prevStatusId, int newStatusId);

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

    // XXX Hard coding for Banglore for faster retrieval
    @Query("SELECT NEW com.proptiger.data.model.marketplace.LeadOffer$CountListingObject(LO.id,count(LI)) from LeadOffer LO join LO.matchingListings LI where LO.id in (?1) and LI.property.project.locality.suburb.cityId = 2 and LI.status = 'Active' and LI.property.project.version='Website' and LI.isDeleted = false group by LO")
    List<CountListingObject> getMatchingListingCount(List<Integer> leadOfferIds);

    @Modifying
    @Transactional
    @Query("update LeadOffer LO set LO.statusId = ?3 where LO.leadId in (?1) and LO.statusId = ?2")
    void updateStatusByLeadIdInAndStatus(List<Integer> leadIdList, int oldStatus, int newStatus);
}