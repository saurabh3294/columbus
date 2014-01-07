package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.RuleProjectMapping;

/**
 * @author Rajeev Pandey
 *
 */
public interface RuleProjectMappingDao extends JpaRepository<RuleProjectMapping, Integer>{

	public List<RuleProjectMapping> findByRuleIdIn(List<Integer> ids);
}
