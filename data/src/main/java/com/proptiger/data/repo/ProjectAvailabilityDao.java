package com.proptiger.data.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectAvailability;

/**
 * DAO for project availability model
 * 
 * @author azi
 * 
 */
public interface ProjectAvailabilityDao extends PagingAndSortingRepository<ProjectAvailability, Integer> {
    @Query(
            nativeQuery = true,
            value = "select sum(availability) from (select substring_index(group_concat(availability order by effective_month desc), ',', 1) availability from cms.project_availabilities where project_supply_id in (?1) group by project_supply_id) t")
    public Double getSumCurrentAvailabilityFromSupplyIds(Set<Integer> supplyIds);
}
