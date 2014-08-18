package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.CompanyUser;

/**
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public interface CompanyUserDao extends JpaRepository<CompanyUser, Integer> {
    public List<CompanyUser> findByCompanyId(Integer companyId);
}
