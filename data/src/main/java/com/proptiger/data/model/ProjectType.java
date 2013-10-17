package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.portfolio.UnitType;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name="RESI_PROJECT_TYPES")
@ResourceMetaInfo(name = "ProjectTypes")
public class ProjectType {

	@Id
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "TYPE_ID")
	private Integer typeId;
	
	@FieldMetaInfo(displayName = "Project Id", description = "Project Id")
	@Column(name = "PROJECT_ID")
	private Integer projectId;
	
	@FieldMetaInfo(displayName = "Unit Name", description = "Unit Name")
	@Column(name = "UNIT_NAME")
	private String unitName;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Type Id", description = "Type Id")
	@Column(name = "UNIT_TYPE")
	@Enumerated(EnumType.STRING)
	private UnitType unitType;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "SIZE")
	private Double size;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Type Id", description = "Type Id")
	@Column(name = "MEASURE")
	private String measure;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "PRICE_PER_UNIT_AREA")
	private Double pricePerUnitArea;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "PRICE_PER_UNIT_AREA_DP")
	private Double pricePerUnitAreaDP;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "PRICE_PER_UNIT_AREA_FP")
	private Double pricePerUnitAreaFP;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Type Id", description = "Type Id")
	@Column(name = "STATUS")
	private String status;
	
	@FieldMetaInfo(displayName = "Bedrooms", description = "Bedrooms")
	@Column(name = "BEDROOMS")
	private Integer bedrooms;

	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "BATHROOMS")
	private int bathrooms;

	
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

	/**
	 * @return the projectId
	 */
	public Integer getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the unitType
	 */
	public UnitType getUnitType() {
		return unitType;
	}

	/**
	 * @param unitType the unitType to set
	 */
	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	/**
	 * @return the size
	 */
	public Double getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Double size) {
		this.size = size;
	}

	/**
	 * @return the measure
	 */
	public String getMeasure() {
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(String measure) {
		this.measure = measure;
	}

	/**
	 * @return the pricePerUnitArea
	 */
	public Double getPricePerUnitArea() {
		return pricePerUnitArea;
	}

	/**
	 * @param pricePerUnitArea the pricePerUnitArea to set
	 */
	public void setPricePerUnitArea(Double pricePerUnitArea) {
		this.pricePerUnitArea = pricePerUnitArea;
	}

	/**
	 * @return the pricePerUnitAreaDP
	 */
	public Double getPricePerUnitAreaDP() {
		return pricePerUnitAreaDP;
	}

	/**
	 * @param pricePerUnitAreaDP the pricePerUnitAreaDP to set
	 */
	public void setPricePerUnitAreaDP(Double pricePerUnitAreaDP) {
		this.pricePerUnitAreaDP = pricePerUnitAreaDP;
	}

	/**
	 * @return the pricePerUnitAreaFP
	 */
	public Double getPricePerUnitAreaFP() {
		return pricePerUnitAreaFP;
	}

	/**
	 * @param pricePerUnitAreaFP the pricePerUnitAreaFP to set
	 */
	public void setPricePerUnitAreaFP(Double pricePerUnitAreaFP) {
		this.pricePerUnitAreaFP = pricePerUnitAreaFP;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the bathrooms
	 */
	public int getBathrooms() {
		return bathrooms;
	}

	/**
	 * @param bathrooms the bathrooms to set
	 */
	public void setBathrooms(int bathrooms) {
		this.bathrooms = bathrooms;
	}
	
	
	
	
}