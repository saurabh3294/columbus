package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.ProjectAssignmentRule;

/**
 * @author Rajeev Pandey
 *
 */
public interface ProjectAssignmentRuleDao extends JpaRepository<ProjectAssignmentRule, Integer>{
	public List<ProjectAssignmentRule> findByBrokerId(Integer brokerId);
}
