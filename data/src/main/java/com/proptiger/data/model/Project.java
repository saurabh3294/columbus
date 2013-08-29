/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;


/**
 *
 * @author mukand
 */
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class Project {
    @Field(value="id")
    @JsonProperty(value="ID")
    private String id;
    
    @Field(value="PROJECT_ID")
    @JsonProperty(value="PROJECT_ID")
    private long projectId;
    
    @Field(value="LOCALITY_ID")
    @JsonProperty(value="LOCALITY_ID")
    private long localityId;
    
    @Field(value="SUBURB_ID")
    @JsonProperty(value="SUBURB_ID")
    private long suburbId;
    
    @Field(value="CITY_ID")
    @JsonProperty(value="CITY_ID")
    private long cityId;
    
    @Field(value="BUILDER_ID")
    @JsonProperty(value="BUILDER_ID")
    private long builderId;
    
    @Field(value="PROJECT_NAME")
    @JsonProperty(value="PROJECT_NAME")
    private String projectName;
    
    @Field(value="PROJECT_TYPES")
    @JsonProperty(value="PROJECT_TYPES")
    private String projectTypes;
    
    @Field(value="BUILDER_NAME")
    @JsonProperty(value="BUILDER_NAME")
    private String builderName;
    
    @Field(value="LOCALITY")
    @JsonProperty(value="LOCALITY")
    private String locality;
    
    @Field(value="BUILDER_IMAGE")
    @JsonProperty(value="BUILDER_IMAGE")
    private String builderImage;
    
    @Field(value="VALID_LAUNCH_DATE")
    @JsonProperty(value="VALID_LAUNCH_DATE")
    private Date validLaunchDate;
    
    @Field(value="BUILDER_IMAGE_SMALL")
    @JsonProperty(value="BUILDER_IMAGE_SMALL")
    private String builderImageSmall;
    
    @Field(value="CITY")
    @JsonProperty(value="CITY")
    private String city;
    
    @Field(value="PROJECT_ADDRESS")
    @JsonProperty(value="PROJECT_ADDRESS")
    private String projectAddress;
    
    @Field(value="PROJECT_PRIORITY")
    @JsonProperty(value="PROJECT_PRIORITY")
    private float projectPriority;
    
    @Field(value="DISPLAY_FLAG")
    @JsonProperty(value="DISPLAY_FLAG")
    private float displayflag;
    
    @Field(value="DISPLAY_ORDER")
    @JsonProperty(value="DISPLAY_ORDER")
    private float displayOrder;
    
    @Field(value="DISPLAY_ORDER_LOCALITY")
    @JsonProperty(value="DISPLAY_ORDER_LOCALITY")
    private float displayOrderLocality;
    
    @Field(value="DISPLAY_ORDER_SUBURB")
    @JsonProperty(value="DISPLAY_ORDER_SUBURB")
    private float displayOrderSuburb;
    
    @Field(value="COMPLETION_DATE")
    @JsonProperty(value="COMPLETION_DATE")
    private String completionDate;
    
    @Field(value="SUBMITTED_DATE")
    @JsonProperty(value="SUBMITTED_DATE")
    private Date submittedDate;
    
    @Field(value="LAUNCH_DATE")
    @JsonProperty(value="LAUNCH_DATE")
    private Date launchDate;
    
    @Field(value="PROMISED_COMPLETION_DATE")
    @JsonProperty(value="PROMISED_COMPLETION_DATE")
    private Date promisedCompletionDate;
    
    @Field(value="IMPORTANCE")
    @JsonProperty(value="IMPORTANCE")
    private long importance;
    
    @Field(value="SUBURB")
    @JsonProperty(value="SUBURB")
    private String suburb;
    
    @Field(value="PROJECT_SMALL_IMAGE")
    @JsonProperty(value="PROJECT_SMALL_IMAGE")
    private String projectSmallImage;
    
    @Field(value="OFFER")
    @JsonProperty(value="OFFER")
    private String offer;
    
    @Field(value="OFFER_HEADING")
    @JsonProperty(value="OFFER_HEADING")
    private String offerHeading;
    
    @Field(value="OFFER_DESC")
    @JsonProperty(value="OFFER_DESC")
    private String offerDesc;
    
    @Field(value="PROJECT_URL")
    @JsonProperty(value="PROJECT_URL")
    private String projectUrl;
    
    @Field(value="LATITUDE")
    @JsonProperty(value="LATITUDE")
    private float latitude;
    
    @Field(value="LONGITUDE")
    @JsonProperty(value="LONGITUDE")
    private float longitude;
    
    @Field(value="HAS_GEO")
    @JsonProperty(value="HAS_GEO")
    private int hasGeo;
    
    @Field(value="HAS_SIZE")
    @JsonProperty(value="HAS_SIZE")
    private int hasSize;
    
    @Field(value="HAS_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="HAS_PRICE_PER_UNIT_AREA")
    private int hasPricePerUnitArea;
    
    @Field(value="HAS_BUDGET")
    @JsonProperty(value="HAS_BUDGET")
    private int hasBudget;
    
    @Field(value="ALL_BEDROOMS")
    @JsonProperty(value="ALL_BEDROOMS")
    private String allBedrooms;
    
    @Field(value="MIN_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="MIN_PRICE_PER_UNIT_AREA")
    private float minPricePerUnitArea;
    
    @Field(value="MAX_PRICE_PER_UNIT_AREA")
    @JsonProperty(value="MAX_PRICE_PER_UNIT_AREA")
    private float maxPricePerUnitArea;
    
    @Field(value="MINSIZE")
    @JsonProperty(value="MINSIZE")
    private float minSize;
    
    @Field(value="MAXSIZE")
    @JsonProperty(value="MAXSIZE")
    private float maxSize;
    
    @Field(value="MIN_BUDGET")
    @JsonProperty(value="MIN_BUDGET")
    private float minBudget;
    
    @Field(value="MAX_BUDGET")
    @JsonProperty(value="MAX_BUDGET")
    private float maxBudget;
    
    @Field(value="MINPRICE")
    @JsonProperty(value="MINPRICE")
    private String minPrice;
    
    @Field(value="MAXPRICE")
    @JsonProperty(value="MAXPRICE")
    private String maxPrice;
    
    @Field(value="MEASURE")
    @JsonProperty(value="MEASURE")
    private String measure;
    
    @Field(value="PROJECT_STATUS")
    @JsonProperty(value="PROJECT_STATUS")
    private String projectStatus;
    
    @Field(value="IS_RESALE")
    @JsonProperty(value="IS_RESALE")
    private boolean isResale;
    
    @Field(value="SPECIFICATION")
    @JsonProperty(value="SPECIFICATION")
    private String specification;
    
    @Field(value="PROJECT_DESCRIPTION")
    @JsonProperty(value="PROJECT_DESCRIPTION")
    private String projectDescription;
    
    @Field(value="PLAN_IMAGES")
    @JsonProperty(value="PLAN_IMAGES")
    private String planImages;
    
    @Field(value="FLOOR_PLAN_IMAGES")
    @JsonProperty(value="FLOOR_PLAN_IMAGES")
    private String floorPlanImages;
    
    @Field(value="CONSTRUCTION_IMAGES")
    @JsonProperty(value="CONSTRUCTION_IMAGES")
    private String constructionImages;
    
    @Field(value="AMENITIES")
    @JsonProperty(value="AMENITIES")
    private String amenities;
    
    @Field(value="TOTAL_UNITS")
    @JsonProperty(value="TOTAL_UNITS")
    private int totalUnits;
    
    @Field(value="PROJECT_SIZE")
    @JsonProperty(value="PROJECT_SIZE")
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
}
