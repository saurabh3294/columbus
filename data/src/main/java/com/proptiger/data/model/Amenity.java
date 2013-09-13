package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity(name = "AMENITIES")
@ResourceMetaInfo(name = "Amenities")
public class Amenity {

	@Column(name = "ID")
	@Id
	private Long id;
	
	@FieldMetaInfo(displayName = "Project Id", description = "Project Id")
	@Column(name = "PROJECT_ID")
	private long projectId;
	
	@FieldMetaInfo(displayName = "Amenity Name", description = "Amenity Name")
	@Column(name = "AMENITY_NAME")
	private String amenityName;

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

	public String getAmenityName() {
		return amenityName;
	}

	public void setAmenityName(String amenityName) {
		this.amenityName = amenityName;
	}
	
}