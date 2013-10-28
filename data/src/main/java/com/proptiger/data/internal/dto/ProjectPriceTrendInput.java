package com.proptiger.data.internal.dto;

/**
 * This class will serve as a input to fetch project price trend
 * @author Rajeev Pandey
 *
 */
public class ProjectPriceTrendInput {

	private Integer projectId;
	private Integer typeId;
	private String name;
	
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
