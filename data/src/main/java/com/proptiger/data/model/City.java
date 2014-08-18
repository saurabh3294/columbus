package com.proptiger.data.model;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.image.Image;

/**
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "cms.city")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class City extends BaseModel {
    private static final long    serialVersionUID = -4768005314447247259L;

    @Id
    @FieldMetaInfo(displayName = "City Id", description = "City Id")
    @Column(name = "CITY_ID")
    @Field("CITY_ID")
    private Integer              id;

    @Transient
    private boolean              authorized       = false;

    @FieldMetaInfo(displayName = "Label", description = "City label")
    @Column(name = "LABEL")
    @Field("CITY")
    private String               label;

    @FieldMetaInfo(displayName = "North east latitude", description = "North east latitude")
    @Column(name = "NORTH_EAST_LATITUDE")
    @Field(value = "NORTH_EAST_LATITUDE")
    private Double               northEastLatitude;

    @FieldMetaInfo(displayName = "North east longitude", description = "North east longitude")
    @Column(name = "NORTH_EAST_LONGITUDE")
    @Field(value = "NORTH_EAST_LONGITUDE")
    private Double               northEastLongitude;

    @FieldMetaInfo(displayName = "South west latitude", description = "South west latitude")
    @Column(name = "SOUTH_WEST_LATITUDE")
    @Field(value = "SOUTH_WEST_LATITUDE")
    private Double               southWestLatitude;

    @FieldMetaInfo(displayName = "South west longitude", description = "South west latitude")
    @Column(name = "SOUTH_WEST_LONGITUDE")
    @Field(value = "SOUTH_WEST_LONGITUDE")
    private Double               southWestLongitude;

    @FieldMetaInfo(displayName = "Center latitude", description = "Center latitude")
    @Column(name = "CENTER_LATITUDE")
    @Field(value = "CENTER_LATITUDE")
    private Double               centerLatitude;

    @FieldMetaInfo(displayName = "Center latitude", description = "Center latitude")
    @Column(name = "CENTER_LONGITUDE")
    @Field(value = "CENTER_LONGITUDE")
    private Double               centerLongitude;

    @Column(name = "DISPLAY_PRIORITY")
    @Field(value = "DISPLAY_PRIORITY")
    @FieldMetaInfo(displayName = "Display Priority", description = "Display Priority")
    private Integer              displayPriority;

    @Column(name = "DISPLAY_ORDER")
    @Field(value = "CITY_DISPLAY_ORDER")
    @FieldMetaInfo(displayName = "Display Order", description = "Display Order")
    private Integer              displayOrder;

    @Column(name = "URL")
    @Field("CITY_URL")
    @FieldMetaInfo(displayName = "URL", description = "URL")
    private String               url;

    @Column(name = "DESCRIPTION")
    @Field("DESCRIPTION")
    @FieldMetaInfo(displayName = "Description", description = "Description")
    private String               description;

    @Transient
    @Field("CITY_PRICE_PER_UNIT_AREA")
    private Double               avgPricePerUnitArea;

    @Transient
    @Field("CITY_PRICE_RISE")
    private Double               avgPriceRisePercentage;

    @Transient
    @Field("CITY_PRICE_RISE_TIME")
    private Integer              avgPriceRiseMonths;

    @Column(name = "MIN_ZOOM_LEVEL")
    @Field("CITY_MIN_ZOOM_LEVEL")
    private Integer              minZoomLevel;

    @Column(name = "MAX_ZOOM_LEVEL")
    @Field("CITY_MAX_ZOOM_LEVEL")
    private Integer              maxZoomLevel;

    @Transient
    private Long                 projectCount;

    @Transient
    @Field("CITY_DOMINANT_UNIT_TYPE")
    private String               dominantUnitType;

    @Transient
    private Map<Integer, Double> avgBHKPricePerUnitArea;

    @Transient
    private List<LandMark>       amenities;

    @Transient
    @Field("CITY_OVERVIEW_URL")
    private String               overviewUrl;

    @Transient
    private Map<String, Long>    projectStatusCount;

    @Transient
    private List<Image>          images;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "City enquiry count", description = "City enquiry count")
    @Field(value = "CITY_ENQUIRY_COUNT")
    private Integer              cityEnquiryCount;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "City view count", description = "City view count")
    @Field(value = "CITY_VIEW_COUNT")
    private Integer              cityViewCount;

    @Transient
    private List<Locality>       localities;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getNorthEastLatitude() {
        return northEastLatitude;
    }

    public void setNorthEastLatitude(Double northEastLatitude) {
        this.northEastLatitude = northEastLatitude;
    }

    public Double getNorthEastLongitude() {
        return northEastLongitude;
    }

    public void setNorthEastLongitude(Double northEastLongitude) {
        this.northEastLongitude = northEastLongitude;
    }

    public Double getSouthWestLatitude() {
        return southWestLatitude;
    }

    public void setSouthWestLatitude(Double southWestLatitude) {
        this.southWestLatitude = southWestLatitude;
    }

    public Double getSouthWestLongitude() {
        return southWestLongitude;
    }

    public void setSouthWestLongitude(Double southWestLongitude) {
        this.southWestLongitude = southWestLongitude;
    }

    public Double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public Double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public Integer getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(Integer displayPriority) {
        this.displayPriority = displayPriority;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
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

    public Integer getMinZoomLevel() {
        return minZoomLevel;
    }

    public void setMinZoomLevel(Integer minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    public Integer getMaxZoomLevel() {
        return maxZoomLevel;
    }

    public void setMaxZoomLevel(Integer maxZoomLevel) {
        this.maxZoomLevel = maxZoomLevel;
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

    public Double getAvgPricePerUnitArea() {
        return avgPricePerUnitArea;
    }

    public void setAvgPricePerUnitArea(Double avgPricePerUnitArea) {
        this.avgPricePerUnitArea = avgPricePerUnitArea;
    }

    public String getDominantUnitType() {
        return dominantUnitType;
    }

    public void setDominantUnitType(String dominantUnitType) {
        this.dominantUnitType = dominantUnitType;
    }

    public List<LandMark> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<LandMark> amenities) {
        this.amenities = amenities;
    }

    public String getOverviewUrl() {
        return overviewUrl;
    }

    public void setOverviewUrl(String cityOverviewUrl) {
        this.overviewUrl = cityOverviewUrl;
    }

    public Map<String, Long> getProjectStatusCount() {
        return projectStatusCount;
    }

    public void setProjectStatusCount(Map<String, Long> projectStatusCount) {
        this.projectStatusCount = projectStatusCount;
    }

    public Map<Integer, Double> getAvgBHKPricePerUnitArea() {
        return avgBHKPricePerUnitArea;
    }

    public void setAvgBHKPricePerUnitArea(Map<Integer, Double> avgBHKPricePerUnitArea) {
        this.avgBHKPricePerUnitArea = avgBHKPricePerUnitArea;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public Integer getCityEnquiryCount() {
        return cityEnquiryCount;
    }

    public void setCityEnquiryCount(Integer cityEnquiryCount) {
        this.cityEnquiryCount = cityEnquiryCount;
    }

    public Integer getCityViewCount() {
        return cityViewCount;
    }

    public void setCityViewCount(Integer cityViewCount) {
        this.cityViewCount = cityViewCount;
    }

    public List<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(List<Locality> localities) {
        this.localities = localities;
    }
}
