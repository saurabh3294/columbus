    package com.proptiger.data.model;

import java.util.List;
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

import org.apache.solr.client.solrj.beans.Field;

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
    @Field("LOCALITY_ID")
    private int localityId;

    @FieldMetaInfo(displayName = "Suburb Id", description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    @Field("SUBURB_ID")
    private int suburbId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "SUBURB_ID", insertable = false, updatable = false)
    private Suburb suburb;

    // XXX TODO - This is to be removed
    @Deprecated
    @Column(name = "CITY_ID")
    @Field("CITY_ID")
    private int cityId;

    @FieldMetaInfo(displayName = "Label", description = "Label")
    @Column(name = "LABEL")
    @Field("LOCALITY")
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
    @Field("LOCALITY_URL")
    private String url;

    @FieldMetaInfo(displayName = "Active", description = "Active")
    @Column(name = "ACTIVE")
    private boolean isActive;
    
    @FieldMetaInfo(displayName = "DELETED_FLAG", description = "DELETED_FLAG")
    @Column(name = "DELETED_FLAG")
    private boolean deletedFlag;
    
    @FieldMetaInfo(displayName = "Description", description = "Description")
    @Column(name = "DESCRIPTION")
    @Field("LOCALITY_DESCRIPTION")
    private String description;

    @FieldMetaInfo(displayName = "Priority", description = "Priority")
    @Column(name = "PRIORITY")
    @Field("LOCALITY_PRIORITY")
    private int priority;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Column(name = "LATITUDE")
    @Field("LATITUDE")
    private Double latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Column(name = "LONGITUDE")
    @Field("LONGITUDE")
    private Double longitude;

    @OneToMany(mappedBy = "locality")
    @JsonIgnore
    private Set<Enquiry> enquiry;
    
    @Transient
    private Map<String, Integer> projectStatusCount;
    @Transient
    private Map<String, Integer> amenityTypeCount;
    @Transient
    private List<String> imagesPath;
    @Transient
    private int imageCount;
    @Transient
    private long totalReviews;
    @Transient
    private double averageRating;
    @Transient
    private long totalRating;
    
    @Transient
    private int projectCount;
    
    @Transient
    private double maxRadius;
    
    @Transient
    private int totalImages = 0;
    
    @Transient
    private int reviewsCount = 0;
    
    @Transient
    private Double minResalePrice;
    
    @Transient
    private Double maxResalePrice;
    
    @Transient
    private Double avgResalePrice;
    
    @Transient
    private Double minPrice;
    
    @Transient
    private Double maxPrice;
    
    @Transient
    private Double avgPrice;
    
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

    @Deprecated
    public int getCityId() {
        return cityId;
    }

    @Deprecated
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
		return projectStatusCount;
	}

	public void setProjectStatusCount(Map<String, Integer> projectStatusCount) {
		this.projectStatusCount = projectStatusCount;
	}

	public int getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}

	public Map<String, Integer> getAmenityTypeCount() {
		return amenityTypeCount;
	}

	public void setAmenityTypeCount(Map<String, Integer> amenityTypeCount) {
		this.amenityTypeCount = amenityTypeCount;
	}

	public List<String> getImagesPath() {
		return imagesPath;
	}

	public void setImagesPath(List<String> imagesPath) {
		this.imagesPath = imagesPath;
	}

	public int getImageCount() {
		return imageCount;
	}

	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

	public long getTotalReviews() {
		return totalReviews;
	}

	public void setTotalReviews(long totalReviews) {
		this.totalReviews = totalReviews;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public long getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(long totalRating) {
		this.totalRating = totalRating;
	}

	public double getMaxRadius() {
		return maxRadius;
	}

	public void setMaxRadius(double maxRadius) {
		this.maxRadius = maxRadius;
	}

	public int getTotalImages() {
		return totalImages;
	}

	public void setTotalImages(int totalImages) {
		this.totalImages = totalImages;
	}

	public int getReviewsCount() {
		return reviewsCount;
	}

	public void setReviewsCount(int reviewsCount) {
		this.reviewsCount = reviewsCount;
	}

	public Double getMinResalePrice() {
		return minResalePrice;
	}

	public void setMinResalePrice(Double minResalePrice) {
		this.minResalePrice = minResalePrice;
	}

	public Double getMaxResalePrice() {
		return maxResalePrice;
	}

	public void setMaxResalePrice(Double maxResalePrice) {
		this.maxResalePrice = maxResalePrice;
	}

	public Double getAvgResalePrice() {
		return avgResalePrice;
	}

	public void setAvgResalePrice(Double avgResalePrice) {
		this.avgResalePrice = avgResalePrice;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}
}
