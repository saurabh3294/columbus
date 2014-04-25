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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.data.model.image.Image;

/**
 * Locality entity class
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "LOCALITY")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Locality extends BaseModel {

    private static final long               serialVersionUID = -4849147096449590891L;

    public static int                       MAX_PRIORITY     = 100;

    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "LOCALITY_ID")
    @Id
    @Field("LOCALITY_ID")
    private int                             localityId;

    @FieldMetaInfo(displayName = "Suburb Id", description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    @Field("SUBURB_ID")
    private int                             suburbId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBURB_ID", insertable = false, updatable = false)
    private Suburb                          suburb;

    // XXX TODO - This is to be removed
    @Deprecated
    @Column(name = "CITY_ID")
    @Field("CITY_ID")
    private int                             cityId;

    @FieldMetaInfo(displayName = "Label", description = "Label")
    @Column(name = "LABEL")
    @Field("LOCALITY")
    private String                          label;

    @FieldMetaInfo(displayName = "Title", description = "Title")
    @Column(name = "META_TITLE")
    private String                          title;

    @FieldMetaInfo(displayName = "Keywords", description = "Keywords")
    @Column(name = "META_KEYWORDS")
    private String                          keywords;

    @FieldMetaInfo(displayName = "Meta Description", description = "Meta Description")
    @Column(name = "META_DESCRIPTION")
    private String                          metaDescription;

    @FieldMetaInfo(displayName = "Url", description = "Url")
    @Column(name = "URL")
    @Field("LOCALITY_URL")
    private String                          url;

    @FieldMetaInfo(displayName = "Active", description = "Active")
    @Column(name = "ACTIVE")
    @JsonIgnore
    private boolean                         isActive;

    @FieldMetaInfo(displayName = "Description", description = "Description")
    @Column(name = "DESCRIPTION")
    @Field("LOCALITY_DESCRIPTION")
    private String                          description;

    @FieldMetaInfo(displayName = "Priority", description = "Priority")
    @Column(name = "PRIORITY")
    @Field("LOCALITY_PRIORITY")
    private int                             priority;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Column(name = "LATITUDE")
    @Field("LOCALITY_LATITUDE")
    private Double                          latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Column(name = "LONGITUDE")
    @Field("LOCALITY_LONGITUDE")
    private Double                          longitude;

    @Field("LOCALITY_OVERVIEW_URL")
    @Transient
    private String                          overviewUrl;

    @OneToMany(mappedBy = "locality")
    @JsonIgnore
    private Set<Enquiry>                    enquiry;

    @OneToMany(mappedBy = "locality")
    @JsonIgnore
    private Set<LocalityRatings>            localityRatings;

    @Transient
    private Map<String, Integer>            projectStatusCount;

    @Transient
    private Map<String, Integer>            amenityTypeCount;

    @Transient
    private List<Image>                     images;

    @Transient
    private Integer                         imageCount;

    @Transient
    private Long                            totalReviews;

    @Transient
    private Double                          averageRating;

    @Transient
    private Long                            ratingsCount;

    @Transient
    private Integer                         projectCount;

    @Transient
    @Field("LOCALITY_MAX_RADIUS")
    private Double                          maxRadius;

    @Transient
    private Integer                         totalImages;

    @Transient
    private Double                          minResalePrice;

    @Transient
    private Double                          maxResalePrice;

    @Transient
    private Double                          minPrice;

    @Transient
    private Double                          maxPrice;

    @Field("LOCALITY_PRICE_RISE")
    @Transient
    private Double                          avgPriceRisePercentage;

    @Field("LOCALITY_PRICE_RISE_TIME")
    @Transient
    private Integer                         avgPriceRiseMonths;

    @Transient
    @Field("LOCALITY_PRICE_PER_UNIT_AREA")
    private Double                          avgPricePerUnitArea;

    @Transient
    @Field("LOCALITY_DOMINANT_UNIT_TYPE")
    private String                          dominantUnitType;

    @Transient
    private Map<Integer, Double>            avgBHKPricePerUnitArea;

    @Transient
    private Map<Double, Long>               numberOfUsersByRating;

    @Transient
    private LocalityAverageRatingByCategory avgRatingsByCategory;

    @Transient
    @JsonIgnore
    @Field("HAS_GEO")
    private int                             hasGeo;

    @Transient
    @JsonIgnore
    @Field("LOCALITY_PRICE_APPRECIATION_RATE")
    private Double                          priceAppreciationRate;

    @Transient
    @Field("geodist()")
    private Double                          geoDistance;
    
    @Transient
    @Field("LOCALITY_PRICE_RISE_6MONTHS")
    private Double                          priceRise6Months;

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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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

    public Set<Enquiry> getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(Set<Enquiry> enquiry) {
        this.enquiry = enquiry;
    }

    public Map<String, Integer> getProjectStatusCount() {
        return projectStatusCount;
    }

    public void setProjectStatusCount(Map<String, Integer> projectStatusCount) {
        this.projectStatusCount = projectStatusCount;
    }

    public Map<String, Integer> getAmenityTypeCount() {
        return amenityTypeCount;
    }

    public void setAmenityTypeCount(Map<String, Integer> amenityTypeCount) {
        this.amenityTypeCount = amenityTypeCount;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Long totalRating) {
        this.ratingsCount = totalRating;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Double getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(Double maxRadius) {
        this.maxRadius = maxRadius;
    }

    public Integer getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(Integer totalImages) {
        this.totalImages = totalImages;
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

    public Double getAvgPricePerUnitArea() {
        return avgPricePerUnitArea;
    }

    public void setAvgPricePerUnitArea(Double avgPricePerUnitArea) {
        this.avgPricePerUnitArea = avgPricePerUnitArea;
    }

    public Double getAvgPriceRisePercentage() {
        return avgPriceRisePercentage;
    }

    public void setAvgPriceRisePercentage(Double avgPriceRisePercentage) {
        this.avgPriceRisePercentage = avgPriceRisePercentage;
    }

    public Integer getAvgPriceRiseMonths() {
        return avgPriceRiseMonths;
    }

    public void setAvgPriceRiseMonths(Integer avgPriceRiseMonths) {
        this.avgPriceRiseMonths = avgPriceRiseMonths;
    }

    public Set<LocalityRatings> getLocalityRatings() {
        return localityRatings;
    }

    public void setLocalityRatings(Set<LocalityRatings> localityReviews) {
        this.localityRatings = localityReviews;
    }

    public String getDominantUnitType() {
        return dominantUnitType;
    }

    public void setDominantUnitType(String dominantUnitType) {
        this.dominantUnitType = dominantUnitType;
    }

    public Map<Integer, Double> getAvgBHKPricePerUnitArea() {
        return avgBHKPricePerUnitArea;
    }

    public void setAvgBHKPriceUnitArea(Map<Integer, Double> avgBHKPrice) {
        this.avgBHKPricePerUnitArea = avgBHKPrice;
    }

    public Map<Double, Long> getNumberOfUsersByRating() {
        return numberOfUsersByRating;
    }

    public void setNumberOfUsersByRating(Map<Double, Long> numberOfUsersByRating) {
        this.numberOfUsersByRating = numberOfUsersByRating;
    }

    public LocalityAverageRatingByCategory getAvgRatingsByCategory() {
        return avgRatingsByCategory;
    }

    public void setAvgRatingsByCategory(LocalityAverageRatingByCategory avgRatingsByCategory) {
        this.avgRatingsByCategory = avgRatingsByCategory;
    }

    public String getOverviewUrl() {
        return overviewUrl;
    }

    public void setOverviewUrl(String localityOverviewUrl) {
        this.overviewUrl = localityOverviewUrl;
    }

    public Double getGeoDistance() {
        return geoDistance;
    }

    public void setGeoDistance(Double geoDistance) {
        this.geoDistance = geoDistance;
    }

    public Double getPriceRise6Months() {
        return priceRise6Months;
    }

    public void setPriceRise6Months(Double priceRise6Months) {
        this.priceRise6Months = priceRise6Months;
    }
}
