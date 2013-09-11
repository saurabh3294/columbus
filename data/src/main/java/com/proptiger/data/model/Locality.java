package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality entity class
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "LOCALITY")
@ResourceMetaInfo(name = "Locality")
public class Locality {
	@FieldMetaInfo(name = "localityId", displayName = "Locality Id", dataType = DataType.STRING, description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	@Id
	private long localityId;
	@FieldMetaInfo(name = "suburbID", displayName = "Suburb Id", dataType = DataType.STRING, description = "Suburb Id")
	@Column(name = "SUBURB_ID")
	private long suburbID;
	@FieldMetaInfo(name = "cityID", displayName = "City ID", dataType = DataType.STRING, description = "City ID")
	@Column(name = "CITY_ID")
	private long cityID;
	@FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "Label")
	@Column(name = "LABEL")
	private String label;
	@FieldMetaInfo(name = "title", displayName = "Title", dataType = DataType.STRING, description = "Title")
	@Column(name = "META_TITLE")
	private String title;
	@FieldMetaInfo(name = "keywords", displayName = "Keywords", dataType = DataType.STRING, description = "Keywords")
	@Column(name = "META_KEYWORDS")
	private String keywords;
	@FieldMetaInfo(name = "metaDescription", displayName = "Meta Description", dataType = DataType.STRING, description = "Meta Description")
	@Column(name = "META_DESCRIPTION")
	private String metaDescription;
	@FieldMetaInfo(name = "url", displayName = "Url", dataType = DataType.STRING, description = "Url")
	@Column(name = "URL")
	private String url;
	@FieldMetaInfo(name = "active", displayName = "Active", dataType = DataType.INTEGER, description = "Active")
	@Column(name = "ACTIVE")
	private int active;
	@FieldMetaInfo(name = "deletedFlag", displayName = "Deleted Flag", dataType = DataType.INTEGER, description = "Deleted Flag")
	@Column(name = "DELETED_FLAG")
	private int deletedFlag;
	@FieldMetaInfo(name = "description", displayName = "Description", dataType = DataType.STRING, description = "Description")
	@Column(name = "DESCRIPTION")
	private String description;
	@FieldMetaInfo(name = "priority", displayName = "Priority", dataType = DataType.INTEGER, description = "Priority")
	@Column(name = "PRIORITY")
	private int priority;
	@FieldMetaInfo(name = "latitude", displayName = "Latitude", dataType = DataType.LONG, description = "Latitude")
	@Column(name = "LATITUDE")
	private long latitude;
	@FieldMetaInfo(name = "longitude", displayName = "Longitude", dataType = DataType.LONG, description = "Longitude")
	@Column(name = "LONGITUDE")
	private long longitude;
	@FieldMetaInfo(name = "wikimapiaID", displayName = "Wikimapia Id", dataType = DataType.LONG, description = "Wikimapia Id")
	@Column(name = "wikimapia_id")
	private long wikimapiaID;

	
	public long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(long localityId) {
		this.localityId = localityId;
	}

	public long getSuburbID() {
		return suburbID;
	}

	public void setSuburbID(long suburbID) {
		this.suburbID = suburbID;
	}

	public long getCityID() {
		return cityID;
	}

	public void setCityID(long cityID) {
		this.cityID = cityID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getDeletedFlag() {
		return deletedFlag;
	}

	public void setDeletedFlag(int deletedFlag) {
		this.deletedFlag = deletedFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public long getWikimapiaID() {
		return wikimapiaID;
	}

	public void setWikimapiaID(long wikimapiaID) {
		this.wikimapiaID = wikimapiaID;
	}
	
	

}
