package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.RuleLocalityMapping;

/**
 * @author Rajeev Pandey
 *
 */
public interface RuleLocalityMappingDao extends JpaRepository<RuleLocalityMapping, Integer>{
	public List<RuleLocalityMapping> findByRuleIdIn(List<Integer> ruleIds);
}
