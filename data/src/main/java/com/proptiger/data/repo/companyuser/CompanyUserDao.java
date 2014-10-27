package com.proptiger.data.repo.companyuser;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.core.model.cms.Company;
import com.proptiger.data.enums.ActivationStatus;
import com.proptiger.data.model.companyuser.CompanyUser;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public interface CompanyUserDao extends JpaRepository<CompanyUser, Integer> {
    public List<CompanyUser> findByCompanyIdAndStatus(Integer companyId, ActivationStatus status);

    @Query("select CU from CompanyUser CU join fetch CU.companyCoverages CC join fetch CC.locality L where CU.userId = ?1")
    public CompanyUser findLocalitiesByUserId(int agentId);

    public CompanyUser findByUserId(Integer companyUserId);

    @Query("select C from CompanyUser CU join CU.company C where CU.userId in (?1)")
    public List<Company> findByAgentId(List<Integer> agentIds);
}
