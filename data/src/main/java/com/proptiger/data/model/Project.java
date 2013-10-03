/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * 
 * @author mukand
 */
@ResourceMetaInfo(name = "Project")
@JsonFilter("fieldFilter")
public class Project implements BaseModel {
    public static enum NestedProperties {
        builderLabel(new String[]{"builder", "name"}),
        cityLabel(new String[]{"locality", "suburb", "city", "label"}),
        suburbLabel(new String[]{"locality", "suburb", "label"}),
        cityId(new String[]{"locality", "suburb", "city", "id"}),
        suburbId(new String[]{"locality", "suburb", "id"}),
        localityLabel(new String[]{"locality", "label"}),
        builderImageURL(new String[]{"builder", "imageURL"}),
        bedrooms(new String[]{"properties", "bedrooms"}),
        bathrooms(new String[]{"properties", "bathrooms"}),
        pricePerUnitArea(new String[]{"properties", "pricePerUnitArea"}),
        size(new String[]{"properties", "size"}),
        unitName(new String[]{"properties", "unitName"}),
        unitType(new String[]{"properties", "unitType"});

        private String[] fields;

        private NestedProperties(String[] fields) {
            this.fields = fields;
        }

        public String[] getFields() {
            return fields;
        }
    };
    
    @FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
    @Field(value = "PROJECT_ID")
    private int projectId;

    @FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    private int localityId;

    @ManyToOne
    @JoinColumn(name="LOCALITY_ID")
    private Locality locality;

    @FieldMetaInfo( displayName = "Builder Id",  description = "Builder Id")
    @Field(value = "BUILDER_ID")
    private int builderId;

    @ManyToOne
    @JoinColumn(name="BUILDER_ID")
    private Builder builder;

    @OneToMany(mappedBy="project")
    private List<Property> properties;
    
    @FieldMetaInfo( displayName = "Project Name",  description = "Project Name")
    @Field(value = "PROJECT_NAME")
    private String name;

    @FieldMetaInfo( displayName = "Project Types",  description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    private String unitTypes;

    @FieldMetaInfo( displayName = "Launch Date",  description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    private Date launchDate;

    @FieldMetaInfo( displayName = "Address",  description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    private String address;

    @FieldMetaInfo( displayName = "Computed Priority",  description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    private double computedPriority;

    @FieldMetaInfo( displayName = "Assigned Priority",  description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    private int assignedPriority;

    @FieldMetaInfo( displayName = "Assigned Locality Priority",  description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    private int assignedLocalityPriority;

    @FieldMetaInfo( displayName = "Assigned Suburb Priority",  description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    private int assignedSuburbPriority;

    @FieldMetaInfo( displayName = "Possession Date",  description = "Possession Date")
    @Field(value = "PROMISED_COMPLETION_DATE")
    private Date possessionDate;

    @FieldMetaInfo( displayName = "Submitted Date",  description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    private Date submittedDate;

    @FieldMetaInfo( displayName = "Image URL",  description = "Image URL")
    @Field(value = "PROJECT_SMALL_IMAGE")
    private String imageURL;

    @FieldMetaInfo( displayName = "Offer",  description = "Offer")
    @Field(value = "OFFER")
    private String offer;

    @FieldMetaInfo( displayName = "Offer Heading",  description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    private String offerHeading;

    @FieldMetaInfo( displayName = "Offer Description",  description = "Offer Description")
    @Field(value = "OFFER_DESC")
    private String offerDesc;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value = "PROJECT_URL")
    private String URL;

    @FieldMetaInfo( displayName = "Latitude",  description = "Latitude")
    @Field(value = "LATITUDE")
    private Double latitude;

    @FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
    @Field(value = "LONGITUDE")
    private Double longitude;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Min Price Per Unit Area",  description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minPricePerUnitArea;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Max Price Per Unit Area",  description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxPricePerUnitArea;

    @FieldMetaInfo( displayName = "Min Size",  description = "Min Size")
    @Field(value = "MINSIZE")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minSize;

    @FieldMetaInfo( displayName = "Max Size",  description = "Max Size")
    @Field(value = "MAXSIZE")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxSize;

    @FieldMetaInfo( displayName = "Min Price",  description = "Min Price")
    @Field(value = "MIN_BUDGET")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double minPrice;

    @FieldMetaInfo( displayName = "Max Price",  description = "Max Price")
    @Field(value = "MAX_BUDGET")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double maxPrice;

    @FieldMetaInfo( displayName = "Min Bedroooms",  description = "Min Bedroooms")
    private int minBedrooms;

    @FieldMetaInfo( displayName = "Max Bedroooms",  description = "Max Bedroooms")
    private int maxBedrooms;

    @FieldMetaInfo( displayName = "Project Status",  description = "Project Status")
    @Field(value = "PROJECT_STATUS")
    private String status;

    @Field(value = "IS_RESALE")
    private boolean isResale;

    @FieldMetaInfo( displayName = "Project Description",  description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    private String description;

    @FieldMetaInfo( displayName = "Total Units",  description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    private Integer totalUnits;

    @FieldMetaInfo( displayName = "size in acres",  description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    private Double sizeInAcres;

    @Field(value = "GEO")
    private List<String> geo;

    @Field(value="PROJECT_STATUS_BEDROOM")
    @JsonIgnore
    private String projectStatusBedroom;

    @Field(value="MEASURE")
    private String propertySizeMeasure;

    private Set<String> propertyUnitTypes;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public int getBuilderId() {
        return builderId;
    }

    public void setBuilderId(int builderId) {
        this.builderId = builderId;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitTypes() {
        return unitTypes;
    }

    public void setUnitTypes(String unitTypes) {
        this.unitTypes = unitTypes;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getComputedPriority() {
        return computedPriority;
    }

    public void setComputedPriority(double computedPriority) {
        this.computedPriority = computedPriority;
    }

    public int getAssignedPriority() {
        return assignedPriority;
    }

    public void setAssignedPriority(int assignedPriority) {
        this.assignedPriority = assignedPriority;
    }

    public int getAssignedLocalityPriority() {
        return assignedLocalityPriority;
    }

    public void setAssignedLocalityPriority(int assignedLocalityPriority) {
        this.assignedLocalityPriority = assignedLocalityPriority;
    }

    public int getAssignedSuburbPriority() {
        return assignedSuburbPriority;
    }

    public void setAssignedSuburbPriority(int assignedSuburbPriority) {
        this.assignedSuburbPriority = assignedSuburbPriority;
    }

    public Date getPossessionDate() {
        return possessionDate;
    }

    public void setPossessionDate(Date possessionDate) {
        this.possessionDate = possessionDate;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getOfferHeading() {
        return offerHeading;
    }

    public void setOfferHeading(String offerHeading) {
        this.offerHeading = offerHeading;
    }

    public String getOfferDesc() {
        return offerDesc;
    }

    public void setOfferDesc(String offerDesc) {
        this.offerDesc = offerDesc;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
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

    public Double getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    public void setMinPricePerUnitArea(Double minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public Double getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    public void setMaxPricePerUnitArea(Double maxPricePerUnitArea) {
        this.maxPricePerUnitArea = maxPricePerUnitArea;
    }

    public Double getMinSize() {
        return minSize;
    }

    public void setMinSize(Double minSize) {
        this.minSize = minSize;
    }

    public Double getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Double maxSize) {
        this.maxSize = maxSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isResale() {
        return isResale;
    }

    public void setResale(boolean isResale) {
        this.isResale = isResale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Double getSizeInAcres() {
        return sizeInAcres;
    }

    public void setSizeInAcres(Double sizeInAcres) {
        this.sizeInAcres = sizeInAcres;
    }

    public String getProjectStatusBedroom() {
        return projectStatusBedroom;
    }

    public void setProjectStatusBedroom(String projectStatusBedroom) {
        this.projectStatusBedroom = projectStatusBedroom;
    }

    public String getPropertySizeMeasure() {
        return propertySizeMeasure;
    }

    public void setPropertySizeMeasure(String propertySizeMeasure) {
        this.propertySizeMeasure = propertySizeMeasure;
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

    public List<String> getGeo() {
        return geo;
    }

    public void setGeo(List<String> geo) {
        this.geo = geo;
    }

    public Set<String> getPropertyUnitTypes() {
        return propertyUnitTypes;
    }

    public void setPropertyUnitTypes(Set<String> propertyUnitTypes) {
        this.propertyUnitTypes = propertyUnitTypes;
    }

    public int getMinBedrooms() {
        return minBedrooms;
    }

    public void setMinBedrooms(int minBedrooms) {
        this.minBedrooms = minBedrooms;
    }

    public int getMaxBedrooms() {
        return maxBedrooms;
    }

    public void setMaxBedrooms(int maxBedrooms) {
        this.maxBedrooms = maxBedrooms;
    }
}
