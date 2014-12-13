/**
 * 
 */
package com.proptiger.data.repo.marketplace;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.Lead;

/**
 * @author Anubhav
 * 
 */

public interface LeadDao extends JpaRepository<Lead, Integer> {
    public List<Lead> findByIdIn(List<Integer> ids);

    public List<Lead> findByCityIdAndClientIdAndMergedLeadIdIsNullOrderByIdDesc(int cityId, int clientId);

    @Query(nativeQuery = true, value = "select * from marketplace.leads where id = ?1 for update")
    public Lead getLock(int id);

    @Query("SELECT L FROM Lead L LEFT JOIN L.leadOffers LO WHERE LO.id IS NULL AND L.mergedLeadId IS NULL AND L.createdAt > ?1")
    public List<Lead> getMergedLeadsWithoutOfferCreatedSince(Date createdSince);

    @Query("SELECT DISTINCT L FROM Lead L INNER JOIN L.leadOffers LO WHERE L.mergedLeadId IS NULL AND LO.createdAt BETWEEN ?1 AND ?2 AND LO.statusId = ?3")
    public List<Lead> getMergedLeadsByOfferredAtBetweenAndOfferStatusId(Date startDate, Date endDate, int offerStatusId);

    @Query("select L from Lead L join fetch L.leadOffers LO join fetch LO.masterLeadOfferStatus MLOS where L.mergedLeadId is null and LO.statusId = ?2 and LO.createdAt < ?1")
    public List<Lead> getMergedLeadsByOfferCreatedAtLessThanAndOfferStatusId(Date offerCreatedAt, int offerStatusId);

    @Query("select L from Lead L join fetch L.requirements where L.id = ?1")
    public Lead getByIdWithRequirements(int id);
}