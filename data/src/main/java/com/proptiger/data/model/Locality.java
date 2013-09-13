package com.proptiger.data.model;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "LOCALITY")
@ResourceMetaInfo(name = "Locality")
public class Locality {
	@FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
	@Column(name = "LOCALITY_ID")
	@Id
	private long localityId;
    
	@FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")    
	@Column(name = "SUBURB_ID")
	private long suburbId;
    
    @ManyToOne
    @JoinColumn(name="SUBURB_ID", insertable = false, updatable = false)
	private Suburb suburb; 
   
	@Column(name = "CITY_ID")
	private long cityId;
        
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
	@Column(name = "wikimapia_id", nullable = true)
	private Long wikimapiaID;
        // These two column are not present in the table. They are used
        // in custom queries.
                
        @OneToMany(mappedBy = "locality", targetEntity = Enquiry.class)
        private Set<Enquiry> enquiry = new HashSet<Enquiry>();
        
        
        public Set<Enquiry> getEnquiry(){
            return this.enquiry;
        }
        
        public void setEnquiry(Set<Enquiry> enquiry){
            this.enquiry = enquiry;
        }
        
        public void addEnquiry(Enquiry enquiry){
            enquiry.setLocality(this);
            getEnquiry().add(enquiry);
        }
	
        public void removeEnquiry(Enquiry enquiry){
            getEnquiry().remove(enquiry);
        }
        
	public long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(long localityId) {
		this.localityId = localityId;
	}

	public long getSuburbId() {
		return suburbId;
	}

	public void setSuburbId(long suburbId) {
		this.suburbId = suburbId;
	}

	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
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
