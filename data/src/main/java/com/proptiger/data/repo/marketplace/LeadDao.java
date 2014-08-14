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
 * @author mandeep
 * 
 */

public interface LeadDao extends JpaRepository<Lead, Integer>, LeadCustomDao {
    @Query("select L from Lead L where L.clientId = ?1 order by L.id desc")
    public List<Lead> findByClientId(int Id);

    @Query("select L from Lead L join L.leadOffers LL where L.cityId = ?1 and LL.statusId not in (7,8,9) and L.clientId = ?2 order by L.id desc")
    public List<Lead> getDuplicateLead(int cityId, int clientId);

    @Query("select L from Lead L  where L.cityId = ?1 and L.clientId = ?2 order by L.id desc")
    public List<Lead> checkDuplicateLead(int cityId, int id);

    @Query("select L from Lead L join L.leadOffers LL where L.cityId = ?1 and L.clientId = ?2 order by L.id desc")
    public List<Lead> checkLeadOfferEntry(int cityId, int clientId);

    public List<Lead> findByNextActionTimeLessThan(Date actionTime);
}
