package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name="cms.amenities_master")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class AmenityMaster extends BaseModel{
	
private static final long serialVersionUID = -4171453248397452560L;
	
	@Id
	@Column(name="AMENITY_Id")
	@FieldMetaInfo(displayName = "Amenity Id", description = "Amenity Id")
	private int amenityId;
	
	@Column(name="AMENITY_NAME")
	@FieldMetaInfo(displayName = "Amenity Name", description = "Amenity Name")
	private String amenityName;
	
	@FieldMetaInfo(displayName = "Abbreviation", description = "Abbreviation")
	@Column(name="ABBREVATION")
	private String abbreviation;

	public int getAmenityId() {
		return amenityId;
	}

	public void setAmenityId(int amenityId) {
		this.amenityId = amenityId;
	}

	public String getAmenityName() {
		return amenityName;
	}

	public void setAmenityName(String amenityName) {
		this.amenityName = amenityName;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
}
