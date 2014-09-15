package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.TaskOfferedListingMapping;

/**
 * 
 * @author azi
 * 
 */
public interface TaskOfferedListingMappingDao extends JpaRepository<TaskOfferedListingMapping, Integer> {
    public List<TaskOfferedListingMapping> findByTaskId(int taskId);
}