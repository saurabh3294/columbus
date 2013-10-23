package com.proptiger.data.internal.dto;

import java.util.Date;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * This is a data transfer object that will be created by service
 * layer after getting data from DAO layer.
 * 
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo
public class UserWishList {

	@FieldMetaInfo(displayName="Project Id", description="Project Id")
	private Integer projectId;
	
	@FieldMetaInfo(displayName="Project Name", description="Project Name")
	private String projectName;
	
	@FieldMetaInfo(displayName="Project URL", description="Project URL")
	private String projectUrl;
	
	@FieldMetaInfo(displayName="Type ID", description="Type ID")
	private Integer typeId;
	
	@FieldMetaInfo(displayName="No of bedrooms", description="No of bedrooms")
	private Integer bedrooms;
	
	@FieldMetaInfo(displayName="Wishlist ID", description="Wishlist ID")
	private Integer wishListId;
	
	@FieldMetaInfo(displayName="City Label", description="City Label")
	private String cityLabel;
	
	@FieldMetaInfo(displayName="Unit Name", description="Unit Name")
	private String unitName;
	
	@FieldMetaInfo(displayName="Builder Name", description="Builder Name")
	private String builderName;
	
	@FieldMetaInfo(displayName="Date Time", description="Date Time")
	private Date datetime;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectUrl() {
		return projectUrl;
	}

	public void setProjectUrl(String projectUrl) {
		this.projectUrl = projectUrl;
	}

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

	public Integer getWishListId() {
		return wishListId;
	}

	public void setWishListId(Integer wishListId) {
		this.wishListId = wishListId;
	}

	public String getCityLabel() {
		return cityLabel;
	}

	public void setCityLabel(String cityLabel) {
		this.cityLabel = cityLabel;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	
}
