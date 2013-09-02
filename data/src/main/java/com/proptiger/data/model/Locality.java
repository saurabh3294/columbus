package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOCALITY")
public class Locality {
	@Column(name = "LOCALITY_ID")
	@Id
	private long localityId;
	@Column(name = "SUBURB_ID")
	private long suburbID;
	@Column(name = "CITY_ID")
	private long cityID;
	@Column(name = "LABEL")
	private String label;
	@Column(name = "META_TITLE")
	private String title;
	@Column(name = "META_KEYWORDS")
	private String keywords;
	@Column(name = "META_DESCRIPTION")
	private String metaDescription;
	@Column(name = "URL")
	private String url;
	@Column(name = "ACTIVE")
	private int active;
	@Column(name = "DELETED_FLAG")
	private int deletedFlag;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "PRIORITY")
	private int priority;
	@Column(name = "LATITUDE")
	private long latitude;
	@Column(name = "LONGITUDE")
	private long longitude;
	
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
