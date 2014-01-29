package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cms.project_banks")
public class ProjectBanks implements BaseModel{
	private static final long serialVersionUID = 2630104707187003972L;

	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "PROJECT_ID")
	private Integer projectId;
	@Column(name = "BANK_ID")
	private Integer bankId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}
	
}
