package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.seller.CompanyUser;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public interface CompanyUserDao extends JpaRepository<CompanyUser, Integer> {
    public List<CompanyUser> findByCompanyId(Integer companyId);
    @Query("select CU.localitiesServiced from CompanyUser CU join CU.localitiesServiced where CU.userId = ?1")
    public List<Locality> findLocalitiesByUserId(int agentId);
}
