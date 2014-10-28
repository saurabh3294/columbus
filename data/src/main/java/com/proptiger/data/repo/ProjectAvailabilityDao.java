package com.proptiger.data.repo;

import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.cms.ProjectAvailability;

/**
 * DAO for project availability model
 * 
 * @author azi
 * 
 */
public interface ProjectAvailabilityDao extends PagingAndSortingRepository<ProjectAvailability, Integer> {
    Integer getSumCurrentAvailabilityFromSupplyIds(Set<Integer> supplyIds, String endMonth);
}