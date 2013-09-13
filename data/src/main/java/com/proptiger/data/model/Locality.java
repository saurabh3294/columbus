package com.proptiger.data.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "LOCALITY")
/*@NamedQueries({
    @NamedQuery(name="Locality.findEnquiry",
        query = "select -1 as locality_id, '' as label, count(*), 'total' from LOCALITY AS L"
        + " JOIN ENQUIRY AS E ON (E.LOCALITY_ID=L.LOCALITY_ID)")
})*/
public class Locality {
	@Column(name = "LOCALITY_ID")
	@Id
	private long localityId;
        
	@Column(name = "SUBURB_ID")
	private long suburbId;
        
	@Column(name = "CITY_ID")
	private long cityId;
        
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
	@Column(name = "wikimapia_id", nullable = true)
	private Long wikimapiaID;
        // These two column are not present in the table. They are used
        // in custom queries.
        /*@Column(name = "ENQUIRY_COUNT")
        private int enquiryCount;
        @Column(name = "QUERY_TYPE")
        private String queryType;*/
        
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

    /*public int getEnquiryCount() {
        return enquiryCount;
    }

    public void setEnquiryCount(int enquiryCount) {
        this.enquiryCount = enquiryCount;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }*/
	
	

}
