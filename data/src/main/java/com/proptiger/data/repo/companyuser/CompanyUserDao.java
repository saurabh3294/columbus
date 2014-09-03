package com.proptiger.data.repo.companyuser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.companyuser.CompanyUser;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public interface CompanyUserDao extends JpaRepository<CompanyUser, Integer> {
    public List<CompanyUser> findByCompanyId(Integer companyId);

    @Query("select CU from CompanyUser CU join fetch CU.companyCoverages CC join fetch CC.locality L where CU.userId = ?1")
    public CompanyUser findLocalitiesByUserId(int agentId);

    public CompanyUser findByUserId(Integer companyUserId);
}
