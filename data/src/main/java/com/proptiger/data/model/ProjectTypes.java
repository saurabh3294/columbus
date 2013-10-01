package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name="RESI_PROJECT_TYPES")
@ResourceMetaInfo(name = "ProjectTypes")
public class ProjectTypes {

	@Id
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "TYPE_ID")
	private Integer typeId;
	
	@FieldMetaInfo(displayName = "Bedrooms", description = "Bedrooms")
	@Column(name = "BEDROOMS")
	private Integer bedrooms;
	
	@FieldMetaInfo(displayName = "Unit Name", description = "Unit Name")
	@Column(name = "UNIT_NAME")
	private String unitName;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	
	
	
}
