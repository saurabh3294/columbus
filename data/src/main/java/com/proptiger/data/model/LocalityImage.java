package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.ResourceMetaInfo;

@Deprecated
@Entity
@Table(name="LOCALITY_IMAGE")
@ResourceMetaInfo
public class LocalityImage implements  BaseModel {
	@Id
	@Column(name="IMAGE_ID")
	private int imageId;
	
	@Column(name="LOCALITY_ID")
	private int localityId;
	
	@Column(name="CITY_ID")
	private int cityId;
	
	@Column(name="SUBURB_ID")
	private int suburbId;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="IMAGE_CATEGORY")
	private String imageCategory;
	
	@Column(name="IMAGE_DISPLAY_NAME")
	private String imageDisplayName;
	
	@Column(name="IMAGE_DESCRIPTION")
	private String imageDescription;
	
	@Column(name="MIGRATION_STATUS")
	private String migrationStatus;

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	/*public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}*/

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageCategory() {
		return imageCategory;
	}

	public void setImageCategory(String imageCategory) {
		this.imageCategory = imageCategory;
	}

	public String getImageDisplayName() {
		return imageDisplayName;
	}

	public void setImageDisplayName(String imageDisplayName) {
		this.imageDisplayName = imageDisplayName;
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public String getMigrationStatus() {
		return migrationStatus;
	}

	public void setMigrationStatus(String migrationStatus) {
		this.migrationStatus = migrationStatus;
	}

	public int getLocalityId() {
		return localityId;
	}

	public void setLocalityId(int localityId) {
		this.localityId = localityId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getSuburbId() {
		return suburbId;
	}

	public void setSuburbId(int suburbId) {
		this.suburbId = suburbId;
	}
	
}
