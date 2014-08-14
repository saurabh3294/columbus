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

public interface LeadDao extends JpaRepository<Lead, Integer>, LeadCustomDao {
    @Query("select L from Lead L where L.clientId = ?1 and L.mergedLeadId is null order by L.id desc")
    public List<Lead> findByClientId(int Id);

    @Query("select L from Lead L  where L.cityId = ?1 and L.clientId = ?2 and L.mergedLeadId is null order by L.id desc")
    public List<Lead> getLeads(int cityId, int id);

    public List<Lead> findByNextActionTimeLessThan(Date actionTime);
}
