package com.proptiger.data.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name="cms.project_secondary_price")
@ResourceMetaInfo
@Deprecated
public class ProjectSecondaryPrice extends BaseModel{
	private static final long serialVersionUID = -9024664436649885563L;

	@Id
	@FieldMetaInfo(displayName = "Id", description = "Id")
	@Column(name="ID")
	private int id;
	
	@Column(name="PROJECT_ID")
	@FieldMetaInfo(displayName = "Project Id", description = "Project Id")
	private int projectId;
	
	@Column(name="UNIT_TYPE")
	@FieldMetaInfo(displayName = "Unit Type", description = "Unit Type")
	private String unitType;
	
	@Column(name="MIN_PRICE")
	@FieldMetaInfo(displayName = "Min Price", description = "Min Price")
	private int minPrice;
	
	@Column(name="MAX_PRICE")
	@FieldMetaInfo(displayName = "Max Price", description = "Max Price")
	private int maxPrice;
	
	@FieldMetaInfo(displayName = "Effective Date", description = "Effective Date")
	@Column(name="EFFECTIVE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	

}
