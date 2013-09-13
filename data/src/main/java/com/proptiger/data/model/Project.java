/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * 
 * @author mukand
 */
@ResourceMetaInfo(name = "Project")
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Project {
    @FieldMetaInfo( displayName = "Id",  description = "Project Id")
    @Field(value = "PROJECT_ID")
    private long id;

    @FieldMetaInfo( displayName = "Locality Id",  description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    private long localityId;

    private Locality locality;

    @FieldMetaInfo( displayName = "Builder Id",  description = "Builder Id")
    @Field(value = "BUILDER_ID")
    private long builderId;

    private Builder builder;

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
    private float computedPriority;

    @FieldMetaInfo( displayName = "Assigned Priority",  description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    private float assignedPriority;

    @FieldMetaInfo( displayName = "Assigned Locality Priority",  description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    private float assignedLocalityPriority;

    @FieldMetaInfo( displayName = "Assigned Suburb Priority",  description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    private float assignedSuburbPriority;

    @FieldMetaInfo( displayName = "Completion Date",  description = "Completion Date")
    @Field(value = "COMPLETION_DATE")
    private String completionDate;

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
    private float latitude;

    @FieldMetaInfo( displayName = "Longitude",  description = "Longitude")
    @Field(value = "LONGITUDE")
    private float longitude;

    @FieldMetaInfo( displayName = "Min Price Per Unit Area",  description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    private float minPricePerUnitArea;

    @FieldMetaInfo( displayName = "Max Price Per Unit Area",  description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    private float maxPricePerUnitArea;

    @FieldMetaInfo( displayName = "Min Size",  description = "Min Size")
    @Field(value = "MINSIZE")
    private float minSize;

    @FieldMetaInfo( displayName = "Max Size",  description = "Max Size")
    @Field(value = "MAXSIZE")
    private float maxSize;

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
    private int totalUnits;

    @FieldMetaInfo( displayName = "size in acres",  description = "size in acres")
    @Field(value = "PROJECT_SIZE")
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
