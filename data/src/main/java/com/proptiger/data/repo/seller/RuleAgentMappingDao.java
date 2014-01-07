package com.proptiger.data.repo.seller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.RuleAgentMapping;

/**
 * @author Rajeev Pandey
 *
 */
public interface RuleAgentMappingDao extends JpaRepository<RuleAgentMapping, Integer>{
	public List<RuleAgentMapping> findByAgentId(Integer agentId); 
}
