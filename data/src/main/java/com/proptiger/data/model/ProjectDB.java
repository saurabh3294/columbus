/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.image.Image;

/**
 * 
 * @author mukand
 */
@Entity
@Table(name = "RESI_PROJECT")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@Deprecated
public class ProjectDB extends BaseModel {

    private static final long serialVersionUID  = -1689938553944928175L;

    @FieldMetaInfo(displayName = "PROJECT ID", description = "PROJECT ID")
    @Column(name = "PROJECT_ID")
    @Id
    private int               projectId;

    @FieldMetaInfo(displayName = "BUILDER ID", description = "BUILDER ID")
    @Column(name = "BUILDER_ID")
    private int               builderId;

    @Transient
    @FieldMetaInfo(displayName = "CITY ID", description = "CITY ID")
    @Field(value = "CITY_ID")
    private int               cityId;
    
    @Transient
    @FieldMetaInfo(displayName = "SUBURB ID", description = "SUBURB ID")
    @Field(value = "SUBURB_ID")
    private int               suburbId;

    @FieldMetaInfo(displayName = "LOCALITY ID", description = "LOCALITY ID")
    @Column(name = "LOCALITY_ID")
    private int               localityId;

    @FieldMetaInfo(displayName = "PROJECT NAME", description = "PROJECT NAME")
    @Column(name = "PROJECT_NAME")
    private String            projectName;

    @FieldMetaInfo(displayName = "PROJECT DESCRIPTION", description = "PROJECT DESCRIPTION")
    @Column(name = "PROJECT_DESCRIPTION")
    private String            projectDescription;

    @FieldMetaInfo(displayName = "PROJECT ADDRESS", description = "PROJECT ADDRESS")
    @Column(name = "PROJECT_ADDRESS")
    private String            projectAddress;

    @Transient
    @FieldMetaInfo(displayName = "PROJECT TYPES", description = "PROJECT TYPES")
    @Field(value = "PROJECT_TYPES")
    private String            projectTypes;

    @FieldMetaInfo(displayName = "BUILDER NAME", description = "BUILDER NAME")
    @Field(value = "BUILDER_NAME")
    private String            builderName;

    @FieldMetaInfo(displayName = "PROJECT SMALL IMAGE", description = "PROJECT SMALL IMAGE")
    @Column(name = "PROJECT_SMALL_IMAGE")
    private String            projectSmallImage;

    @FieldMetaInfo(displayName = "LATITUDE", description = "LATITUDE")
    @Column(name = "LATITUDE")
    private float             latitude;

    @FieldMetaInfo(displayName = "LONGITUDE", description = "LONGITUDE")
    @Column(name = "LONGITUDE")
    private float             longitude;

    @FieldMetaInfo(displayName = "DISPLAY ORDER", description = "DISPLAY ORDER")
    @Column(name = "DISPLAY_ORDER")
    private int               displayOrder;

    @Transient
    @FieldMetaInfo(displayName = "PROJECT STATUS", description = "PROJECT STATUS")
    @Field(value = "PROJECT_STATUS")
    private String            projectStatus;

    @FieldMetaInfo(displayName = "PROJECT URL", description = "PROJECT URL")
    @Column(name = "PROJECT_URL")
    private String            projectUrl;

    @FieldMetaInfo(displayName = "PRICE DISCLAIMER", description = "PRICE DISCLAIMER")
    @Column(name = "PRICE_DISCLAIMER")
    private String            priceDisclaimer;

    @Transient
    @FieldMetaInfo(displayName = "OFFER HEADING", description = "OFFER HEADING")
    @Field(value = "OFFER_HEADING")
    private String            offerHeading;

    @Transient
    @FieldMetaInfo(displayName = "OFFER DESC", description = "OFFER DESC")
    @Field(value = "OFFER_DESC")
    private String            offerDesc;

    @Transient
    @FieldMetaInfo(displayName = "SUBMITTED Date", description = "SUBMITTED Date")
    @Field(value =  "SUBMITTED_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date              submittedDate;

    @FieldMetaInfo(displayName = "PAYMENT PLAN", description = "PAYMENT PLAN")
    @Column(name = "APPLICATION_FORM")
    private String            paymentPlan;

    @FieldMetaInfo(displayName = "NO OF TOWERS", description = "NO OF TOWERS")
    @Column(name = "NO_OF_TOWERS")
    private int               noOfTowers;

    @Transient
    @FieldMetaInfo(displayName = "NO OF FLATS", description = "NO OF FLATS")
    @Field("PROJECT_SUPPLY")
    private int               noOfFlates;

    @FieldMetaInfo(displayName = "LAUNCH Date", description = "LAUNCH Date")
    @Column(name = "LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date              launchDate;

    @FieldMetaInfo(displayName = "DISPLAY ORDER LOCALITY", description = "DISPLAY ORDER LOCALITY")
    @Column(name = "DISPLAY_ORDER_LOCALITY")
    private int               displayOrderLocality;

    @FieldMetaInfo(displayName = "DISPLAY ORDER SUBURB", description = "DISPLAY ORDER SUBURB")
    @Column(name = "DISPLAY_ORDER_SUBURB")
    private int               displayOrderSuburb;

    @FieldMetaInfo(displayName = "YOUTUBE VEDIO", description = "YOUTUBE VEDIO")
    @Column(name = "YOUTUBE_VEDIO")
    private String            youtubeVedio;

    @FieldMetaInfo(displayName = "APPLICATION FORM", description = "APPLICATION FORM")
    @Column(name = "APPLICATION_FORM")
    private String            applicationForm;

    @FieldMetaInfo(displayName = "PROMISED COMPLETION Date", description = "PROMISED COMPLETION Date")
    @Column(name = "PROMISED_COMPLETION_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date              promisedCompletionDate;

    @FieldMetaInfo(displayName = "AVAILABILITY", description = "AVAILABILITY")
    @Column(name = "D_AVAILABILITY")
    private Integer           availability;

    @FieldMetaInfo(displayName = "PROJECT TYPE ID", description = "PROJECT TYPE ID")
    @Column(name = "PROJECT_TYPE_ID")
    private int               projectTypeId;

    @FieldMetaInfo(displayName = "PRE LAUNCH Date", description = "PRE LAUNCH Date")
    @Column(name = "PRE_LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date              preLaunchDate;

    @FieldMetaInfo(displayName = "PROJECT SIZE", description = "PROJECT SIZE")
    @Column(name = "PROJECT_SIZE")
    private Double            projectSize;

    @FieldMetaInfo(displayName = "LAST MODIFIED Date", description = "LAST MODIFIED Date")
    @Column(name = "D_LAST_PRICE_UPDATION_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date              lastModifiedDate;

    @Transient
    private List<Image>       images;

    @Transient
    private Double            minPricePerUnitArea;

    @Transient
    private Double            maxPricePerUnitArea;

    @Transient
    @FieldMetaInfo(displayName = "Min Resale Price", description = "Min Resale Price")
    private Double            minResalePrice;

    @Transient
    @FieldMetaInfo(displayName = "Max Resale Price", description = "Max Resale Price")
    private Double            maxResalePrice;

    @Transient
    private Double            avgPriceRisePercentage;

    @Transient
    private Integer           avgPriceRiseMonths;

    @Transient
    private Set<String>       propertyUnitTypes = new HashSet<String>();

    @Transient
    private Set<Integer>      distinctBedrooms  = new HashSet<Integer>();

    @Transient
    private String            imageURL;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getBuilderId() {
        return builderId;
    }

    public void setBuilderId(int builderId) {
        this.builderId = builderId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getSuburbId() {
        return suburbId;
    }

    public void setSuburbId(int suburbId) {
        this.suburbId = suburbId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public String getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypes(String projectTypes) {
        this.projectTypes = projectTypes;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public String getProjectSmallImage() {
        return projectSmallImage;
    }

    public void setProjectSmallImage(String projectSmallImage) {
        this.projectSmallImage = projectSmallImage;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getPriceDisclaimer() {
        return priceDisclaimer;
    }

    public void setPriceDisclaimer(String priceDisclaimer) {
        this.priceDisclaimer = priceDisclaimer;
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

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    public String getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(String paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    public int getNoOfTowers() {
        return noOfTowers;
    }

    public void setNoOfTowers(int noOfTowers) {
        this.noOfTowers = noOfTowers;
    }

    public int getNoOfFlates() {
        return noOfFlates;
    }

    public void setNoOfFlates(int noOfFlates) {
        this.noOfFlates = noOfFlates;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public int getDisplayOrderLocality() {
        return displayOrderLocality;
    }

    public void setDisplayOrderLocality(int displayOrderLocality) {
        this.displayOrderLocality = displayOrderLocality;
    }

    public int getDisplayOrderSuburb() {
        return displayOrderSuburb;
    }

    public void setDisplayOrderSuburb(int displayOrderSuburb) {
        this.displayOrderSuburb = displayOrderSuburb;
    }

    public String getYoutubeVedio() {
        return youtubeVedio;
    }

    public void setYoutubeVedio(String youtubeVedio) {
        this.youtubeVedio = youtubeVedio;
    }

    public String getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(String applicationForm) {
        this.applicationForm = applicationForm;
    }

    public Date getPromisedCompletionDate() {
        return promisedCompletionDate;
    }

    public void setPromisedCompletionDate(Date promisedCompletionDate) {
        this.promisedCompletionDate = promisedCompletionDate;
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public Date getPreLaunchDate() {
        return preLaunchDate;
    }

    public void setPreLaunchDate(Date preLaunchDate) {
        this.preLaunchDate = preLaunchDate;
    }

    public Double getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(Double projectSize) {
        this.projectSize = projectSize;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public Set<String> getPropertyUnitTypes() {
        return propertyUnitTypes;
    }

    public void setPropertyUnitTypes(Set<String> propertyUnitTypes) {
        this.propertyUnitTypes = propertyUnitTypes;
    }

    public void addPropertyUnitTypes(String propertyUnitType) {
        this.propertyUnitTypes.add(propertyUnitType);
    }

    public Set<Integer> getDistinctBedrooms() {
        return distinctBedrooms;
    }

    public void setDistinctBedrooms(Set<Integer> distinctBedrooms) {
        this.distinctBedrooms = distinctBedrooms;
    }

    public void addDistinctBedrooms(int bedroom) {
        this.distinctBedrooms.add(bedroom);
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageUrl) {
        this.imageURL = imageUrl;
    }
}
