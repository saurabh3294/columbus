package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Company;

/**
 * 
 * @author azi
 * 
 */

public interface CompanyDao extends JpaRepository<Company, Integer> {
    @Query("SELECT C from Company C inner join CompanyCoverage CC where CC.localityId IN ?1")
    public List<Company> findBrokersForLocality(List<Integer> localityIds);
}
