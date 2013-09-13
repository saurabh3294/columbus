package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	@FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	@Id
	private long id;

	@FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")
	@Column(name = "SUBURB_ID")
	private long suburbId;

	private Suburb suburb;

	@FieldMetaInfo( displayName = "Label",  description = "Label")
	@Column(name = "LABEL")
	private String label;
	@FieldMetaInfo( displayName = "Title",  description = "Title")
	@Column(name = "META_TITLE")
	private String title;
	@FieldMetaInfo( displayName = "Keywords",  description = "Keywords")
	@Column(name = "META_KEYWORDS")
	private String keywords;
	@FieldMetaInfo( displayName = "Meta Description",  description = "Meta Description")
	@Column(name = "META_DESCRIPTION")
	private String metaDescription;
	@FieldMetaInfo( displayName = "Url",  description = "Url")
	@Column(name = "URL")
	private String url;
	@FieldMetaInfo( displayName = "Active",  description = "Active")
	@Column(name = "ACTIVE")
	private int active;
	@FieldMetaInfo( displayName = "Deleted Flag",  description = "Deleted Flag")
	@Column(name = "DELETED_FLAG")
	private int deletedFlag;
	@FieldMetaInfo( displayName = "Description",  description = "Description")
	@Column(name = "DESCRIPTION")
	private String description;
	@FieldMetaInfo( displayName = "Priority",  description = "Priority")
	@Column(name = "PRIORITY")
	private int priority;
	@FieldMetaInfo( displayName = "Latitude",  description = "Latitude")
	@Column(name = "LATITUDE")
	private long latitude;
	@FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
	@Column(name = "LONGITUDE")
	private long longitude;
	@FieldMetaInfo( displayName = "Wikimapia Id",  description = "Wikimapia Id")
	@Column(name = "wikimapia_id")
	private long wikimapiaID;

	
	public long getLocalityId() {
		return id;
	}

	public void setLocalityId(long localityId) {
		this.id = localityId;
	}

	public long getSuburbID() {
		return suburbId;
	}

	public void setSuburbID(long suburbID) {
		this.suburbId = suburbID;
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

    public Suburb getSuburb() {
        return suburb;
    }

    public void setSuburb(Suburb suburb) {
        this.suburb = suburb;
    }
	
	

}
