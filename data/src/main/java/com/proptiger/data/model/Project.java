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
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Project {
	@FieldMetaInfo(name = "Id", displayName = "Id", dataType = DataType.STRING, description = "Project Id")
    @Field(value="id")
    @JsonProperty(value="id")
    private String id;
    
	@FieldMetaInfo(name = "project_id", displayName = "Project Id", dataType = DataType.LONG, description = "Project Id")
    @Field(value="PROJECT_ID")
    @JsonProperty(value="project_id")
    private long projectId;
    
	@FieldMetaInfo(name = "localityId", displayName = "Locality Id", dataType = DataType.LONG, description = "Locality Id")
    @Field(value="LOCALITY_ID")
    @JsonProperty(value="locality_id")
    private long localityId;
    
	@FieldMetaInfo(name = "suburb_id", displayName = "Suburb Id", dataType = DataType.LONG, description = "Suburb Id")
    @Field(value="SUBURB_ID")
    @JsonProperty(value="suburb_id")
    private long suburbId;
    
	@FieldMetaInfo(name = "city_id", displayName = "City Id", dataType = DataType.LONG, description = "City Id")
    @Field(value="CITY_ID")
    @JsonProperty(value="city_id")
    private long cityId;
    
	@FieldMetaInfo(name = "builder_id", displayName = "Builder Id", dataType = DataType.LONG, description = "Builder Id")
    @Field(value="BUILDER_ID")
    @JsonProperty(value="builder_id")
    private long builderId;
    
	@FieldMetaInfo(name = "project_name", displayName = "Project Name", dataType = DataType.STRING, description = "Project Name")
    @Field(value="PROJECT_NAME")
    @JsonProperty(value="project_name")
    private String projectName;
    
	@FieldMetaInfo(name = "project_types", displayName = "Project Types", dataType = DataType.STRING, description = "Project Types")
    @Field(value="PROJECT_TYPES")
    @JsonProperty(value="project_types")
    private String projectTypes;
    
	@FieldMetaInfo(name = "builder_name", displayName = "Builder Name", dataType = DataType.STRING, description = "Builder Name")
    @Field(value="BUILDER_NAME")
    @JsonProperty(value="builder_name")
    private String builderName;
    
	@FieldMetaInfo(name = "locality", displayName = "Locality", dataType = DataType.STRING, description = "Locality")
    @Field(value="LOCALITY")
    @JsonProperty(value="locality")
    private String locality;
    
	@FieldMetaInfo(name = "builder_image", displayName = "Builder Image", dataType = DataType.STRING, description = "Builder Image")
    @Field(value="BUILDER_IMAGE")
    @JsonProperty(value="builder_image")
    private String builderImage;
    
	@FieldMetaInfo(name = "valid_launch_date", displayName = "Valid Launch Date", dataType = DataType.DATE, description = "Valid Launch Date")
    @Field(value="VALID_LAUNCH_DATE")
    @JsonProperty(value="valid_launch_date")
    private Date validLaunchDate;
    
	@FieldMetaInfo(name = "builder_image_small", displayName = "Builder Image Small", dataType = DataType.STRING, description = "Builder Image Small")
    @Field(value="BUILDER_IMAGE_SMALL")
    @JsonProperty(value="builder_image_small")
    private String builderImageSmall;
    
	@FieldMetaInfo(name = "city", displayName = "City", dataType = DataType.STRING, description = "City")
    @Field(value="CITY")
    @JsonProperty(value="city")
    private String city;
    
	@FieldMetaInfo(name = "project_address", displayName = "Project Address", dataType = DataType.STRING, description = "Project Address")
    @Field(value="PROJECT_ADDRESS")
    @JsonProperty(value="project_address")
    private String projectAddress;
    
	@FieldMetaInfo(name = "project_priority", displayName = "Project Priority", dataType = DataType.FLOAT, description = "Project Priority")
    @Field(value="PROJECT_PRIORITY")
    @JsonProperty(value="project_priority")
    private float projectPriority;
    
	@FieldMetaInfo(name = "display_flag", displayName = "Display Flag", dataType = DataType.FLOAT, description = "Display Flag")
    @Field(value="DISPLAY_FLAG")
    @JsonProperty(value="display_flag")
    private float displayflag;
    
	@FieldMetaInfo(name = "display_order", displayName = "Display Order", dataType = DataType.FLOAT, description = "Display Order")
    @Field(value="DISPLAY_ORDER")
    @JsonProperty(value="display_order")
    private float displayOrder;
    
	@FieldMetaInfo(name = "localityId", displayName = "Locality Id", dataType = DataType.FLOAT, description = "Locality Id")
    @Field(value="DISPLAY_ORDER_LOCALITY")
    @JsonProperty(value="display_order_locality")
    private float displayOrderLocality;
    
	@FieldMetaInfo(name = "display_order_suburb", displayName = "Display Order Suburb", dataType = DataType.STRING, description = "Display Order Suburb")
    @Field(value="DISPLAY_ORDER_SUBURB")
    @JsonProperty(value="display_order_suburb")
    private float displayOrderSuburb;
    
	@FieldMetaInfo(name = "completion_date", displayName = "Completion Date", dataType = DataType.STRING, description = "Completion Date")
    @Field(value="COMPLETION_DATE")
    @JsonProperty(value="completion_date")
    private String completionDate;
    
	@FieldMetaInfo(name = "submitted_date", displayName = "Submitted Date", dataType = DataType.DATE, description = "Submitted Date")
    @Field(value="SUBMITTED_DATE")
    @JsonProperty(value="submitted_date")
    private Date submittedDate;
    
	@FieldMetaInfo(name = "launch_date", displayName = "Launch Date", dataType = DataType.DATE, description = "Launch Date")
    @Field(value="LAUNCH_DATE")
    @JsonProperty(value="launch_date")
    private Date launchDate;
    
	@FieldMetaInfo(name = "promised_completion_date", displayName = "Promised Completion Date", dataType = DataType.DATE, description = "Promised Completion Date")
    @Field(value="PROMISED_COMPLETION_DATE")
    @JsonProperty(value="promised_completion_date")
    private Date promisedCompletionDate;
    
	@FieldMetaInfo(name = "importance", displayName = "Importance", dataType = DataType.STRING, description = "Importance")
    @Field(value="IMPORTANCE")
    @JsonProperty(value="importance")
    private long importance;
    
	@FieldMetaInfo(name = "suburb", displayName = "Suburb", dataType = DataType.STRING, description = "Suburb")
    @Field(value="SUBURB")
    @JsonProperty(value="suburb")
    private String suburb;
    
	@FieldMetaInfo(name = "project_small_image", displayName = "Project Small Image", dataType = DataType.STRING, description = "Project Small Image")
    @Field(value="PROJECT_SMALL_IMAGE")
    @JsonProperty(value="project_small_image")
    private String projectSmallImage;
    
	@FieldMetaInfo(name = "offer", displayName = "Offer", dataType = DataType.STRING, description = "Offer")
    @Field(value="OFFER")
    @JsonProperty(value="offer")
    private String offer;
    
	@FieldMetaInfo(name = "offer_heading", displayName = "Offer Heading", dataType = DataType.STRING, description = "Offer Heading")
    @Field(value="OFFER_HEADING")
    @JsonProperty(value="offer_heading")
    private String offerHeading;
    
	@FieldMetaInfo(name = "offer_desc", displayName = "Offer Description", dataType = DataType.STRING, description = "Offer Description")
    @Field(value="OFFER_DESC")
    @JsonProperty(value="offer_desc")
    private String offerDesc;
    
	@FieldMetaInfo(name = "project_url", displayName = "Project Url", dataType = DataType.STRING, description = "Project Url")
    @Field(value="PROJECT_URL")
    @JsonProperty(value="project_url")
    private String projectUrl;
    
	@FieldMetaInfo(name = "latitude", displayName = "Latitude", dataType = DataType.FLOAT, description = "Latitude")
    @Field(value="LATITUDE")
    @JsonProperty(value="latitude")
    private float latitude;
	
	@FieldMetaInfo(name = "longitude", displayName = "Longitude", dataType = DataType.FLOAT, description = "Longitude")
    @Field(value="LONGITUDE")
    @JsonProperty(value="longitude")
    private float longitude;
    
	@FieldMetaInfo(name = "geo", displayName = "Geo", dataType = DataType.STRING, description = "Geo")
    @Field(value="GEO")
    @JsonProperty(value="geo")
    private String geo;
    
	@FieldMetaInfo(name = "has_geo", displayName = "has_geo", dataType = DataType.INTEGER, description = "Has Geo")
    @Field(value="HAS_GEO")
    @JsonProperty(value="has_geo")
    private int hasGeo;
    
	@FieldMetaInfo(name = "has_size", displayName = "Has Size", dataType = DataType.INTEGER, description = "Has Size")
    @Field(value="HAS_SIZE")
    @JsonProperty(value="has_size")
    private int hasSize;

	@FieldMetaInfo(name = "has_price_per_unit_area", displayName = "Has Price Per Unit Area", dataType = DataType.STRING, description = "Has Price Per Unit Area")
    @Field(value="HAS_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="has_price_per_unit_area")
    private int hasPricePerUnitArea;
    
	@FieldMetaInfo(name = "has_budget", displayName = "Has Budget", dataType = DataType.INTEGER, description = "Has Budget")
    @Field(value="HAS_BUDGET")
    @JsonProperty(value="has_budget")
    private int hasBudget;
    
	@FieldMetaInfo(name = "all_bedrooms", displayName = "All Bedrooms", dataType = DataType.STRING, description = "All Bedrooms")
    @Field(value="ALL_BEDROOMS")
    @JsonProperty(value="all_bedrooms")
    private String allBedrooms;
    
	@FieldMetaInfo(name = "min_price_per_unit_area", displayName = "Min Price Per Unit Area", dataType = DataType.CURRENCY, description = "Min Price Per Unit Area")
    @Field(value="MIN_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="min_price_per_unit_area")
    private float minPricePerUnitArea;
    
	@FieldMetaInfo(name = "max_price_per_unit_area", displayName = "Max Price Per Unit Area", dataType = DataType.CURRENCY, description = "Max Price Per Unit Area")
    @Field(value="MAX_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="max_price_per_unit_area")
    private float maxPricePerUnitArea;
    
	@FieldMetaInfo(name = "minsize", displayName = "Min Size", dataType = DataType.FLOAT, description = "Min Size")
    @Field(value="MINSIZE")
    @JsonProperty(value="minsize")
    private float minSize;
    
	@FieldMetaInfo(name = "maxsize", displayName = "Max Size", dataType = DataType.FLOAT, description = "Max Size")
    @Field(value="MAXSIZE")
    @JsonProperty(value="maxsize")
    private float maxSize;
    
	@FieldMetaInfo(name = "min_budget", displayName = "Min Budget", dataType = DataType.CURRENCY, description = "Min Budget")
    @Field(value="MIN_BUDGET")
    @JsonProperty(value="min_budget")
    private float minBudget;
    
	@FieldMetaInfo(name = "max_budget", displayName = "Max Budget", dataType = DataType.CURRENCY, description = "Max Budget")
    @Field(value="MAX_BUDGET")
    @JsonProperty(value="max_budget")
    private float maxBudget;
    
	@FieldMetaInfo(name = "minprice", displayName = "Min Price", dataType = DataType.CURRENCY, description = "Min Price")
    @Field(value="MINPRICE")
    @JsonProperty(value="minprice")
    private String minPrice;
    
	@FieldMetaInfo(name = "maxprice", displayName = "Max Price", dataType = DataType.CURRENCY, description = "Max Price")
    @Field(value="MAXPRICE")
    @JsonProperty(value="maxprice")
    private String maxPrice;
    
	@FieldMetaInfo(name = "measure", displayName = "Measure", dataType = DataType.STRING, description = "Measure")
    @Field(value="MEASURE")
    @JsonProperty(value="measure")
    private String measure;
    
	@FieldMetaInfo(name = "project_status", displayName = "Project Status", dataType = DataType.STRING, description = "Project Status")
    @Field(value="PROJECT_STATUS")
    @JsonProperty(value="project_status")
    private String projectStatus;
    
	@FieldMetaInfo(name = "is_resale", displayName = "Is Resale", dataType = DataType.BOOLEAN, description = "Is Resale")
    @Field(value="IS_RESALE")
    @JsonProperty(value="is_resale")
    private boolean isResale;
    
	@FieldMetaInfo(name = "specification", displayName = "Specification", dataType = DataType.STRING, description = "Specification")
    @Field(value="SPECIFICATION")
    @JsonProperty(value="specification")
    private String specification;
    
	@FieldMetaInfo(name = "project_description", displayName = "Project Description", dataType = DataType.STRING, description = "Project Description")
    @Field(value="PROJECT_DESCRIPTION")
    @JsonProperty(value="project_description")
    private String projectDescription;
    
	@FieldMetaInfo(name = "plan_images", displayName = "Plan Images", dataType = DataType.STRING, description = "Plan Images")
    @Field(value="PLAN_IMAGES")
    @JsonProperty(value="plan_images")
    private String planImages;
    
	@FieldMetaInfo(name = "floor_plan_images", displayName = "Floor Plan Images", dataType = DataType.STRING, description = "Floor Plan Images")
    @Field(value="FLOOR_PLAN_IMAGES")
    @JsonProperty(value="floor_plan_images")
    private String floorPlanImages;
    
	@FieldMetaInfo(name = "construction_images", displayName = "Construction Images", dataType = DataType.STRING, description = "Construction Images")
    @Field(value="CONSTRUCTION_IMAGES")
    @JsonProperty(value="construction_images")
    private String constructionImages;

	@FieldMetaInfo(name = "amenities", displayName = "Amenities", dataType = DataType.STRING, description = "Amenities")
    @Field(value="AMENITIES")
    @JsonProperty(value="amenities")
    private String amenities;
    
	@FieldMetaInfo(name = "total_units", displayName = "Total Units", dataType = DataType.INTEGER, description = "Total Units")
    @Field(value="TOTAL_UNITS")
    @JsonProperty(value="total_units")
    private int totalUnits;
    
	@FieldMetaInfo(name = "project_size", displayName = "Project Size", dataType = DataType.FLOAT, description = "Project Size")
    @Field(value="PROJECT_SIZE")
    @JsonProperty(value="project_size")
    private float projectSize;
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the projectId
     */
    public long getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the localityId
     */
    public long getLocalityId() {
        return localityId;
    }

    /**
     * @param localityId the localityId to set
     */
    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    /**
     * @return the suburbId
     */
    public long getSuburbId() {
        return suburbId;
    }

    /**
     * @param suburbId the suburbId to set
     */
    public void setSuburbId(long suburbId) {
        this.suburbId = suburbId;
    }

    /**
     * @return the cityId
     */
    public long getCityId() {
        return cityId;
    }

    /**
     * @param cityId the cityId to set
     */
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    /**
     * @return the builderId
     */
    public long getBuilderId() {
        return builderId;
    }

    /**
     * @param builderId the builderId to set
     */
    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the projectTypes
     */
    public String getProjectTypes() {
        return projectTypes;
    }

    /**
     * @param projectTypes the projectTypes to set
     */
    public void setProjectTypes(String projectTypes) {
        this.projectTypes = projectTypes;
    }

    /**
     * @return the builderName
     */
    public String getBuilderName() {
        return builderName;
    }

    /**
     * @param builderName the builderName to set
     */
    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    /**
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @param locality the locality to set
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * @return the builderImage
     */
    public String getBuilderImage() {
        return builderImage;
    }

    /**
     * @param builderImage the builderImage to set
     */
    public void setBuilderImage(String builderImage) {
        this.builderImage = builderImage;
    }

    /**
     * @return the validLaunchDate
     */
    public Date getValidLaunchDate() {
        return validLaunchDate;
    }

    /**
     * @param validLaunchDate the validLaunchDate to set
     */
    public void setValidLaunchDate(Date validLaunchDate) {
        this.validLaunchDate = validLaunchDate;
    }

    /**
     * @return the builderImageSmall
     */
    public String getBuilderImageSmall() {
        return builderImageSmall;
    }

    /**
     * @param builderImageSmall the builderImageSmall to set
     */
    public void setBuilderImageSmall(String builderImageSmall) {
        this.builderImageSmall = builderImageSmall;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the projectAddress
     */
    public String getProjectAddress() {
        return projectAddress;
    }

    /**
     * @param projectAddress the projectAddress to set
     */
    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    /**
     * @return the projectPriority
     */
    public float getProjectPriority() {
        return projectPriority;
    }

    /**
     * @param projectPriority the projectPriority to set
     */
    public void setProjectPriority(float projectPriority) {
        this.projectPriority = projectPriority;
    }

    /**
     * @return the displayflag
     */
    public float getDisplayflag() {
        return displayflag;
    }

    /**
     * @param displayflag the displayflag to set
     */
    public void setDisplayflag(float displayflag) {
        this.displayflag = displayflag;
    }

    /**
     * @return the displayOrder
     */
    public float getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder the displayOrder to set
     */
    public void setDisplayOrder(float displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return the displayOrderLocality
     */
    public float getDisplayOrderLocality() {
        return displayOrderLocality;
    }

    /**
     * @param displayOrderLocality the displayOrderLocality to set
     */
    public void setDisplayOrderLocality(float displayOrderLocality) {
        this.displayOrderLocality = displayOrderLocality;
    }

    /**
     * @return the displayOrderSuburb
     */
    public float getDisplayOrderSuburb() {
        return displayOrderSuburb;
    }

    /**
     * @param displayOrderSuburb the displayOrderSuburb to set
     */
    public void setDisplayOrderSuburb(float displayOrderSuburb) {
        this.displayOrderSuburb = displayOrderSuburb;
    }

    /**
     * @return the completionDate
     */
    public String getCompletionDate() {
        return completionDate;
    }

    /**
     * @param completionDate the completionDate to set
     */
    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * @return the submittedDate
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * @param submittedDate the submittedDate to set
     */
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * @return the launchDate
     */
    public Date getLaunchDate() {
        return launchDate;
    }

    /**
     * @param launchDate the launchDate to set
     */
    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    /**
     * @return the promisedCompletionDate
     */
    public Date getPromisedCompletionDate() {
        return promisedCompletionDate;
    }

    /**
     * @param promisedCompletionDate the promisedCompletionDate to set
     */
    public void setPromisedCompletionDate(Date promisedCompletionDate) {
        this.promisedCompletionDate = promisedCompletionDate;
    }

    /**
     * @return the importance
     */
    public long getImportance() {
        return importance;
    }

    /**
     * @param importance the importance to set
     */
    public void setImportance(long importance) {
        this.importance = importance;
    }

    /**
     * @return the suburb
     */
    public String getSuburb() {
        return suburb;
    }

    /**
     * @param suburb the suburb to set
     */
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    /**
     * @return the projectSmallImage
     */
    public String getProjectSmallImage() {
        return projectSmallImage;
    }

    /**
     * @param projectSmallImage the projectSmallImage to set
     */
    public void setProjectSmallImage(String projectSmallImage) {
        this.projectSmallImage = projectSmallImage;
    }

    /**
     * @return the offer
     */
    public String getOffer() {
        return offer;
    }

    /**
     * @param offer the offer to set
     */
    public void setOffer(String offer) {
        this.offer = offer;
    }

    /**
     * @return the offerHeading
     */
    public String getOfferHeading() {
        return offerHeading;
    }

    /**
     * @param offerHeading the offerHeading to set
     */
    public void setOfferHeading(String offerHeading) {
        this.offerHeading = offerHeading;
    }

    /**
     * @return the offerDesc
     */
    public String getOfferDesc() {
        return offerDesc;
    }

    /**
     * @param offerDesc the offerDesc to set
     */
    public void setOfferDesc(String offerDesc) {
        this.offerDesc = offerDesc;
    }

    /**
     * @return the projectUrl
     */
    public String getProjectUrl() {
        return projectUrl;
    }

    /**
     * @param projectUrl the projectUrl to set
     */
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    /**
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the hasGeo
     */
    public int getHasGeo() {
        return hasGeo;
    }

    /**
     * @param hasGeo the hasGeo to set
     */
    public void setHasGeo(int hasGeo) {
        this.hasGeo = hasGeo;
    }

    /**
     * @return the hasSize
     */
    public int getHasSize() {
        return hasSize;
    }

    /**
     * @param hasSize the hasSize to set
     */
    public void setHasSize(int hasSize) {
        this.hasSize = hasSize;
    }

    /**
     * @return the hasPricePerUnitArea
     */
    public int getHasPricePerUnitArea() {
        return hasPricePerUnitArea;
    }

    /**
     * @param hasPricePerUnitArea the hasPricePerUnitArea to set
     */
    public void setHasPricePerUnitArea(int hasPricePerUnitArea) {
        this.hasPricePerUnitArea = hasPricePerUnitArea;
    }

    /**
     * @return the hasBudget
     */
    public int getHasBudget() {
        return hasBudget;
    }

    /**
     * @param hasBudget the hasBudget to set
     */
    public void setHasBudget(int hasBudget) {
        this.hasBudget = hasBudget;
    }

    /**
     * @return the allBedrooms
     */
    public String getAllBedrooms() {
        return allBedrooms;
    }

    /**
     * @param allBedrooms the allBedrooms to set
     */
    public void setAllBedrooms(String allBedrooms) {
        this.allBedrooms = allBedrooms;
    }

    /**
     * @return the minPricePerUnitArea
     */
    public float getMinPricePerUnitArea() {
        return minPricePerUnitArea;
    }

    /**
     * @param minPricePerUnitArea the minPricePerUnitArea to set
     */
    public void setMinPricePerUnitArea(float minPricePerUnitArea) {
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    /**
     * @return the maxPricePerUnitArea
     */
    public float getMaxPricePerUnitArea() {
        return maxPricePerUnitArea;
    }

    /**
     * @param maxPricePerUnitArea the maxPricePerUnitArea to set
     */
    public void setMaxPricePerUnitArea(float maxPricePerUnitArea) {
        this.maxPricePerUnitArea = maxPricePerUnitArea;
    }

    /**
     * @return the minSize
     */
    public float getMinSize() {
        return minSize;
    }

    /**
     * @param minSize the minSize to set
     */
    public void setMinSize(float minSize) {
        this.minSize = minSize;
    }

    /**
     * @return the maxSize
     */
    public float getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * @return the minBudget
     */
    public float getMinBudget() {
        return minBudget;
    }

    /**
     * @param minBudget the minBudget to set
     */
    public void setMinBudget(float minBudget) {
        this.minBudget = minBudget;
    }

    /**
     * @return the maxBudget
     */
    public float getMaxBudget() {
        return maxBudget;
    }

    /**
     * @param maxBudget the maxBudget to set
     */
    public void setMaxBudget(float maxBudget) {
        this.maxBudget = maxBudget;
    }

    /**
     * @return the minPrice
     */
    public String getMinPrice() {
        return minPrice;
    }

    /**
     * @param minPrice the minPrice to set
     */
    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * @return the maxPrice
     */
    public String getMaxPrice() {
        return maxPrice;
    }

    /**
     * @param maxPrice the maxPrice to set
     */
    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * @return the measure
     */
    public String getMeasure() {
        return measure;
    }

    /**
     * @param measure the measure to set
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * @return the projectStatus
     */
    public String getProjectStatus() {
        return projectStatus;
    }

    /**
     * @param projectStatus the projectStatus to set
     */
    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    /**
     * @return the isResale
     */
    public boolean isIsResale() {
        return isResale;
    }

    /**
     * @param isResale the isResale to set
     */
    public void setIsResale(boolean isResale) {
        this.isResale = isResale;
    }

    /**
     * @return the specification
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * @param specification the specification to set
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * @return the projectDescription
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * @param projectDescription the projectDescription to set
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * @return the planImages
     */
    public String getPlanImages() {
        return planImages;
    }

    /**
     * @param planImages the planImages to set
     */
    public void setPlanImages(String planImages) {
        this.planImages = planImages;
    }

    /**
     * @return the floorPlanImages
     */
    public String getFloorPlanImages() {
        return floorPlanImages;
    }

    /**
     * @param floorPlanImages the floorPlanImages to set
     */
    public void setFloorPlanImages(String floorPlanImages) {
        this.floorPlanImages = floorPlanImages;
    }

    /**
     * @return the constructionImages
     */
    public String getConstructionImages() {
        return constructionImages;
    }

    /**
     * @param constructionImages the constructionImages to set
     */
    public void setConstructionImages(String constructionImages) {
        this.constructionImages = constructionImages;
    }

    /**
     * @return the amenities
     */
    public String getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    /**
     * @return the totalUnits
     */
    public int getTotalUnits() {
        return totalUnits;
    }

    /**
     * @param totalUnits the totalUnits to set
     */
    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    /**
     * @return the projectSize
     */
    public float getProjectSize() {
        return projectSize;
    }

    /**
     * @param projectSize the projectSize to set
     */
    public void setProjectSize(float projectSize) {
        this.projectSize = projectSize;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }
}
