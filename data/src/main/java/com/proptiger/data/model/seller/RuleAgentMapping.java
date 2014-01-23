package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Mapping of rule id from ProjectAssignmentRule with Agent.
 * 
 * @author Rajeev Pandey
 */
@Entity
@Table(name = "cms.rule_agent_mappings")
@JsonFilter("fieldFilter")
public class RuleAgentMapping {
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "rule_id")
	private Integer ruleId;
	
	@Column(name = "agent_id")
	private Integer agentId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

}
