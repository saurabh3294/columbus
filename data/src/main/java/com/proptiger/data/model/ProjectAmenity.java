package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity(name = "AMENITIES")
@ResourceMetaInfo
public class ProjectAmenity implements BaseModel {

	@Column(name = "ID")
	@Id
	@JsonIgnore
	private Long id;
	
	@FieldMetaInfo(displayName = "Project Id", description = "Project Id")
	@Column(name = "PROJECT_ID")
	@JsonIgnore
	private long projectId;
	
	@FieldMetaInfo(displayName = "Amenity Name", description = "Amenity Name")
	@Column(name = "AMENITY_NAME")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
