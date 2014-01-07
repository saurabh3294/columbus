package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.project_assignment_rules")
public class ProjectAssignmentRule implements BaseModel{
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "rule_name")
	private String ruleName;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "broker_id", referencedColumnName="id")
	private Broker broker;
	
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
