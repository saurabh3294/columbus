package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.project_assignment_rules")
@JsonFilter("fieldFilter")
public class ProjectAssignmentRule implements BaseModel{
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "rule_name")
	private String ruleName;

	@Column(name = "broker_id")
	private Integer brokerId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	
}
