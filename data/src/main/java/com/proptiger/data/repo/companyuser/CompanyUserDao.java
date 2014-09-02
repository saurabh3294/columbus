package com.proptiger.data.repo.companyuser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.companyuser.CompanyUser;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public interface CompanyUserDao extends JpaRepository<CompanyUser, Integer> {
    public List<CompanyUser> findByCompanyId(Integer companyId);

    @Query("select CU.localities from CompanyUser CU join CU.localities where CU.userId = ?1")
    public List<Locality> findLocalitiesByUserId(int agentId);

    public CompanyUser findByUserId(Integer companyUserId);
}
