package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name="cms.resi_project_amenities")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class ProjectCMSAmenity extends BaseModel {
	@Id
	@FieldMetaInfo(displayName = "Id", description = "Id")
	@Column(name="ID")
	private long id;
	
	@FieldMetaInfo(displayName = "Project Id", description = "Project Id")
	@Column(name="PROJECT_ID")
	@JsonIgnore
	private int projectId;
	
	@FieldMetaInfo(displayName = "Amenity Display Name", description = "Amenity Display Name")
	@Column(name="AMENITY_DISPLAY_NAME")
	private String amenityDisplayName;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name="AMENITY_ID", insertable=false, updatable=false)
	private AmenityMaster amenityMaster;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getAmenityDisplayName() {
		return amenityDisplayName;
	}

	public void setAmenityDisplayName(String amenityDisplayName) {
		this.amenityDisplayName = amenityDisplayName;
	}

	public AmenityMaster getAmenityMaster() {
		return amenityMaster;
	}

	public void setAmenityMaster(AmenityMaster amenityMaster) {
		this.amenityMaster = amenityMaster;
	}

}
