    package com.proptiger.data.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Locality entity class
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "LOCALITY")
@ResourceMetaInfo
//@JsonFilter("fieldFilter")
public class Locality implements BaseModel {
    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "LOCALITY_ID")
    @Id
    private int localityId;

    @FieldMetaInfo(displayName = "Suburb Id", description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    private int suburbId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "SUBURB_ID", insertable = false, updatable = false)
    private Suburb suburb;

    // XXX TODO - This is to be removed
    @Column(name = "CITY_ID")
    private int cityId;

    @FieldMetaInfo(displayName = "Label", description = "Label")
    @Column(name = "LABEL")
    private String label;

    @FieldMetaInfo(displayName = "Title", description = "Title")
    @Column(name = "META_TITLE")
    private String title;

    @FieldMetaInfo(displayName = "Keywords", description = "Keywords")
    @Column(name = "META_KEYWORDS")
    private String keywords;

    @FieldMetaInfo(displayName = "Meta Description", description = "Meta Description")
    @Column(name = "META_DESCRIPTION")
    private String metaDescription;

    @FieldMetaInfo(displayName = "Url", description = "Url")
    @Column(name = "URL")
    private String url;

    @FieldMetaInfo(displayName = "Active", description = "Active")
    @Column(name = "ACTIVE")
    private boolean isActive;
    
    @FieldMetaInfo(displayName = "DELETED_FLAG", description = "DELETED_FLAG")
    @Column(name = "DELETED_FLAG")
    private boolean deletedFlag;
    
    @FieldMetaInfo(displayName = "Description", description = "Description")
    @Column(name = "DESCRIPTION")
    private String description;

    @FieldMetaInfo(displayName = "Priority", description = "Priority")
    @Column(name = "PRIORITY")
    private int priority;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Column(name = "LATITUDE")
    private Double latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Column(name = "LONGITUDE")
    private Double longitude;

    @OneToMany(mappedBy = "locality")
    @JsonIgnore
    private Set<Enquiry> enquiry = new HashSet<Enquiry>();
    
    @Transient
    Map<String, Integer> derivedProjectStatusCount;
    
    @Transient
    private int projectCount;
    
    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public int getSuburbId() {
        return suburbId;
    }

    public void setSuburbId(int suburbId) {
        this.suburbId = suburbId;
    }

    public Suburb getSuburb() {
        return suburb;
    }

    public void setSuburb(Suburb suburb) {
        this.suburb = suburb;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
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

    public Set<Enquiry> getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(Set<Enquiry> enquiry) {
        this.enquiry = enquiry;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

	public Map<String, Integer> getProjectStatusCount() {
		return derivedProjectStatusCount;
	}

	public void setProjectStatusCount(Map<String, Integer> projectStatusCount) {
		this.derivedProjectStatusCount = projectStatusCount;
	}

	public int getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}
}
