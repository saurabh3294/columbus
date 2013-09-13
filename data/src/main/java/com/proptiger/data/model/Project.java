/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * 
 * @author mukand
 */
@ResourceMetaInfo(name = "Project")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Project {
    @FieldMetaInfo(name = "id", displayName = "Id", dataType = DataType.LONG, description = "Project Id")
    @Field(value = "PROJECT_ID")
    @JsonProperty
    private long id;

    @FieldMetaInfo(name = "localityId", displayName = "Locality Id", dataType = DataType.LONG, description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    @JsonProperty
    private long localityId;

    @JsonProperty
    private Locality locality;

    @FieldMetaInfo(name = "builderId", displayName = "Builder Id", dataType = DataType.LONG, description = "Builder Id")
    @Field(value = "BUILDER_ID")
    @JsonProperty
    private long builderId;

    @JsonProperty
    private Builder builder;

    @FieldMetaInfo(name = "name", displayName = "Project Name", dataType = DataType.STRING, description = "Project Name")
    @Field(value = "PROJECT_NAME")
    @JsonProperty
    private String name;

    @FieldMetaInfo(name = "unitTypes", displayName = "Project Types", dataType = DataType.STRING, description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    @JsonProperty
    private String unitTypes;

    @FieldMetaInfo(name = "launchDate", displayName = "Launch Date", dataType = DataType.DATE, description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    @JsonProperty
    private Date launchDate;

    @FieldMetaInfo(name = "address", displayName = "Address", dataType = DataType.STRING, description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    @JsonProperty
    private String address;

    @FieldMetaInfo(name = "computedPriority", displayName = "Computed Priority", dataType = DataType.FLOAT, description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    @JsonProperty
    private float computedPriority;

    @FieldMetaInfo(name = "assignedPriority", displayName = "Assigned Priority", dataType = DataType.INTEGER, description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    @JsonProperty
    private float assignedPriority;

    @FieldMetaInfo(name = "assignedLocalityPriority", displayName = "Assigned Locality Priority", dataType = DataType.INTEGER, description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    @JsonProperty
    private float assignedLocalityPriority;

    @FieldMetaInfo(name = "assignedSuburbPriority", displayName = "Assigned Suburb Priority", dataType = DataType.INTEGER, description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    @JsonProperty
    private float assignedSuburbPriority;

    @FieldMetaInfo(name = "completionDate", displayName = "Completion Date", dataType = DataType.DATE, description = "Completion Date")
    @Field(value = "COMPLETION_DATE")
    @JsonProperty
    private String completionDate;

    @FieldMetaInfo(name = "submittedDate", displayName = "Submitted Date", dataType = DataType.DATE, description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    @JsonProperty
    private Date submittedDate;

    @FieldMetaInfo(name = "imageURL", displayName = "Image URL", dataType = DataType.STRING, description = "Image URL")
    @Field(value = "PROJECT_SMALL_IMAGE")
    @JsonProperty
    private String imageURL;

    @FieldMetaInfo(name = "offer", displayName = "Offer", dataType = DataType.STRING, description = "Offer")
    @Field(value = "OFFER")
    @JsonProperty(value = "offer")
    private String offer;

    @FieldMetaInfo(name = "offer_heading", displayName = "Offer Heading", dataType = DataType.STRING, description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    @JsonProperty(value = "offer_heading")
    private String offerHeading;

    @FieldMetaInfo(name = "offer_desc", displayName = "Offer Description", dataType = DataType.STRING, description = "Offer Description")
    @Field(value = "OFFER_DESC")
    @JsonProperty(value = "offer_desc")
    private String offerDesc;

    @FieldMetaInfo(name = "URL", displayName = "URL", dataType = DataType.STRING, description = "URL")
    @Field(value = "PROJECT_URL")
    @JsonProperty
    private String URL;

    @FieldMetaInfo(name = "latitude", displayName = "Latitude", dataType = DataType.FLOAT, description = "Latitude")
    @Field(value = "LATITUDE")
    @JsonProperty(value = "latitude")
    private float latitude;

    @FieldMetaInfo(name = "longitude", displayName = "Longitude", dataType = DataType.FLOAT, description = "Longitude")
    @Field(value = "LONGITUDE")
    @JsonProperty(value = "longitude")
    private float longitude;

    @FieldMetaInfo(name = "minPricePerUnitArea", displayName = "Min Price Per Unit Area", dataType = DataType.FLOAT, description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    @JsonProperty
    private float minPricePerUnitArea;

    @FieldMetaInfo(name = "maxPricePerUnitArea", displayName = "Max Price Per Unit Area", dataType = DataType.FLOAT, description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    @JsonProperty
    private float maxPricePerUnitArea;

    @FieldMetaInfo(name = "minSize", displayName = "Min Size", dataType = DataType.FLOAT, description = "Min Size")
    @Field(value = "MINSIZE")
    @JsonProperty
    private float minSize;

    @FieldMetaInfo(name = "maxSize", displayName = "Max Size", dataType = DataType.FLOAT, description = "Max Size")
    @Field(value = "MAXSIZE")
    @JsonProperty
    private float maxSize;

    @FieldMetaInfo(name = "status", displayName = "Project Status", dataType = DataType.STRING, description = "Project Status")
    @Field(value = "PROJECT_STATUS")
    @JsonProperty
    private String status;

    @Field(value = "IS_RESALE")
    private boolean isResale;

    @FieldMetaInfo(name = "description", displayName = "Project Description", dataType = DataType.STRING, description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    @JsonProperty
    private String description;

    @FieldMetaInfo(name = "totalUnits", displayName = "Total Units", dataType = DataType.INTEGER, description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    @JsonProperty
    private int totalUnits;

    @FieldMetaInfo(name = "sizeInAcres", displayName = "size in acres", dataType = DataType.FLOAT, description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    @JsonProperty
    private float sizeInAcres;

    @Field(value="PROJECT_STATUS_BEDROOM")
    private String projectStatusBedroom;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public long getBuilderId() {
        return builderId;
    }

    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
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

    public float getComputedPriority() {
        return computedPriority;
    }

    public void setComputedPriority(float computedPriority) {
        this.computedPriority = computedPriority;
    }

    public float getAssignedPriority() {
        return assignedPriority;
    }

    public void setAssignedPriority(float assignedPriority) {
        this.assignedPriority = assignedPriority;
    }

    public float getAssignedLocalityPriority() {
        return assignedLocalityPriority;
    }

    public void setAssignedLocalityPriority(float assignedLocalityPriority) {
        this.assignedLocalityPriority = assignedLocalityPriority;
    }

    public float getAssignedSuburbPriority() {
        return assignedSuburbPriority;
    }

    public void setAssignedSuburbPriority(float assignedSuburbPriority) {
        this.assignedSuburbPriority = assignedSuburbPriority;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    public void setMinPricePerUnitArea(float minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public float getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    public void setMaxPricePerUnitArea(float maxPricePerUnitArea) {
        this.maxPricePerUnitArea = maxPricePerUnitArea;
    }

    public float getMinSize() {
        return minSize;
    }

    public void setMinSize(float minSize) {
        this.minSize = minSize;
    }

    public float getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(float maxSize) {
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

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public float getSizeInAcres() {
        return sizeInAcres;
    }

    public void setSizeInAcres(float sizeInAcres) {
        this.sizeInAcres = sizeInAcres;
    }
}
