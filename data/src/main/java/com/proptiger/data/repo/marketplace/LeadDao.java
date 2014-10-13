/**
 * 
 */
package com.proptiger.data.repo.marketplace;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.marketplace.Lead;

/**
 * @author Anubhav
 * 
 */

public interface LeadDao extends JpaRepository<Lead, Integer> {
    @Query("select L from Lead L where L.clientId = ?1 and L.mergedLeadId is null order by L.id desc")
    public List<Lead> findByClientId(int Id);

    @Query("select L from Lead L  where L.cityId = ?1 and L.clientId = ?2 and L.mergedLeadId is null order by L.id desc")
    public List<Lead> getLeads(int cityId, int id);

    @Query("SELECT L FROM Lead L LEFT JOIN L.leadOffers LO WHERE LO.id IS NULL AND L.mergedLeadId IS NULL AND L.createdAt > ?1")
    public List<Lead> getMergedLeadsWithoutOfferCreatedSince(Date createdSince);

    @Query("SELECT DISTINCT L FROM Lead L INNER JOIN L.leadOffers LO WHERE L.mergedLeadId IS NULL AND LO.createdAt BETWEEN ?1 AND ?2 AND LO.statusId = ?3")
    public List<Lead> getMergedLeadsByOfferredAtBetweenAndOfferStatusId(Date startDate, Date endDate, int offerStatusId);

    @Query("select L from Lead L where L.id in (?1)")
    public List<Lead> getLeads(List<Integer> leadIds);

    @Query(nativeQuery = true, value = "select * from marketplace.leads where id = ?1 for update")
    public Lead getLock(int id);

    @Query("select L from Lead L join fetch L.leadOffers LO join fetch LO.masterLeadOfferStatus MLOS where L.mergedLeadId is null and (LO.expireFlag = 0 or (L.requestBrokerPhaseId is not null and L.requestBrokerPhaseId > 0)) and LO.expireTime < NOW()")
    public List<Lead> getMergedLeadsWithOfferExpired();

    @Query("select L from Lead L join fetch L.requirements where L.id = ?1")
    public Lead findRequirementsByLeadId(int leadId);

    @Modifying
    @Transactional
    @Query("update Lead L set L.requestBrokerPhaseId = 0 where L.id in (?1)")
    public void updateLeadRequestBrokerPhaseId(List<Integer> leadIdList);

}
