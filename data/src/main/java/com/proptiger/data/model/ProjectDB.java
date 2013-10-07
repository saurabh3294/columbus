/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name="RESI_PROJECT")
@ResourceMetaInfo(name = "Project")
public class ProjectDB implements BaseModel{
    @FieldMetaInfo(displayName="PROJECT ID", description="PROJECT ID")
    @Column(name="PROJECT_ID")
    @Id
    private int projectId ;
    
    @FieldMetaInfo(displayName="BUILDER ID", description="BUILDER ID")
    @Column(name="BUILDER_ID")
    private int builderId ;
    
    @FieldMetaInfo(displayName="CITY ID", description="CITY ID")
    @Column(name="CITY_ID")
    private int cityId ;
    
    @FieldMetaInfo(displayName="SUBURB ID", description="SUBURB ID")
    @Column(name="SUBURB_ID")
    private int suburbId ;
    
    @FieldMetaInfo(displayName="LOCALITY ID", description="LOCALITY ID")
    @Column(name="LOCALITY_ID")
    private int localityId ;
    
    @FieldMetaInfo(displayName="PROJECT NAME", description="PROJECT NAME")
    @Column(name="PROJECT_NAME")
    private String projectName ;
    
    @FieldMetaInfo(displayName="PROJECT DESCRIPTION", description="PROJECT DESCRIPTION")
    @Column(name="PROJECT_DESCRIPTION")
    private String projectDescription ;
    
    @FieldMetaInfo(displayName="PROJECT ADDRESS", description="PROJECT ADDRESS")
    @Column(name="PROJECT_ADDRESS")
    private String projectAddress ;
    
    @FieldMetaInfo(displayName="PROJECT TYPES", description="PROJECT TYPES")
    @Column(name="PROJECT_TYPES")
    private String projectTypes ;
    
    @FieldMetaInfo(displayName="BUILDER NAME", description="BUILDER NAME")
    @Column(name="BUILDER_NAME")
    private String builderName ;
    
    @FieldMetaInfo(displayName="PROJECT SMALL IMAGE", description="PROJECT SMALL IMAGE")
    @Column(name="PROJECT_SMALL_IMAGE")
    private String projectSmallImage ;
    
    @FieldMetaInfo(displayName="LOCATION DESC", description="LOCATION DESC")
    @Column(name="LOCATION_DESC")
    private String locationDesc ;
    
    @FieldMetaInfo(displayName="LATITUDE", description="LATITUDE")
    @Column(name="LATITUDE")
    private float latitude ;
    
    @FieldMetaInfo(displayName="LONGITUDE", description="LONGITUDE")
    @Column(name="LONGITUDE")
    private float longitude ;
    
    @FieldMetaInfo(displayName="META TITLE", description="META TITLE")
    @Column(name="META_TITLE")
    private String metaTitle ;
    
    @FieldMetaInfo(displayName="META KEYWORDS", description="META KEYWORDS")
    @Column(name="META_KEYWORDS")
    private String metaKeywords ;
    
    @FieldMetaInfo(displayName="META DESCRIPTION", description="META DESCRIPTION")
    @Column(name="META_DESCRIPTION")
    private String metaDescription ;
    
    @FieldMetaInfo(displayName="DISPLAY ORDER", description="DISPLAY ORDER")
    @Column(name="DISPLAY_ORDER")
    private int displayOrder ;
    
    @FieldMetaInfo(displayName="ACTIVE", description="ACTIVE")
    @Column(name="ACTIVE")
    private int active ;
    
    @FieldMetaInfo(displayName="PROJECT STATUS", description="PROJECT STATUS")
    @Column(name="PROJECT_STATUS")
    private String projectStatus ;
    
    @FieldMetaInfo(displayName="PROJECT URL", description="PROJECT URL")
    @Column(name="PROJECT_URL")
    private String projectUrl ;
    
    @FieldMetaInfo(displayName="FEATURED", description="FEATURED")
    @Column(name="FEATURED")
    private int featured ;
    
    @FieldMetaInfo(displayName="COMPLETION Date", description="COMPLETION Date")
    @Column(name="COMPLETION_DATE")
    private String completionDate ;
    
    @FieldMetaInfo(displayName="PRICE DISCLAIMER", description="PRICE DISCLAIMER")
    @Column(name="PRICE_DISCLAIMER")
    private String priceDisclaimer ;
    
    @FieldMetaInfo(displayName="OFFER HEADING", description="OFFER HEADING")
    @Column(name="OFFER_HEADING")
    private String offerHeading ;
    
    @FieldMetaInfo(displayName="OFFER DESC", description="OFFER DESC")
    @Column(name="OFFER_DESC")
    private String offerDesc ;
    
    @FieldMetaInfo(displayName="SUBMITTED Date", description="SUBMITTED Date")
    @Column(name="SUBMITTED_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date submittedDate ;
    
    @FieldMetaInfo(displayName="SUBMITTED BY", description="SUBMITTED BY")
    @Column(name="SUBMITTED_BY")
    private int submittedBy ;
    
    @FieldMetaInfo(displayName="MODIFIED Date", description="MODIFIED Date")
    @Column(name="MODIFIED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date modifiedDate ;
    
    @FieldMetaInfo(displayName="MODIFIED BY", description="MODIFIED BY")
    @Column(name="MODIFIED_BY")
    private int modifiedBy ;
    
    @FieldMetaInfo(displayName="PAYMENT PLAN", description="PAYMENT PLAN")
    @Column(name="PAYMENT_PLAN")
    private String paymentPlan ;
    
    @FieldMetaInfo(displayName="NO OF TOWERS", description="NO OF TOWERS")
    @Column(name="NO_OF_TOWERS")
    private int noOfTowers ;
    
    @FieldMetaInfo(displayName="NO OF FLATES", description="NO OF FLATES")
    @Column(name="NO_OF_FLATES")
    private int noOfFlates ;
    
    @FieldMetaInfo(displayName="LAUNCH Date", description="LAUNCH Date")
    @Column(name="LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date launchDate ;
    
    @FieldMetaInfo(displayName="OFFLINE TYPE", description="OFFLINE TYPE")
    @Column(name="OFFLINE_TYPE")
    private int offlineType ;
    
    /*@FieldMetaInfo(displayName="OFFER", description="OFFER")
    @Column(name="OFFER")
    private enum('none','so','nl','ne') OFFER ;*/
    
    @FieldMetaInfo(displayName="DISPLAY FLAG", description="DISPLAY FLAG")
    @Column(name="DISPLAY_FLAG")
    private int displayFlag ;
    
    @FieldMetaInfo(displayName="DISPLAY ORDER LOCALITY", description="DISPLAY ORDER LOCALITY")
    @Column(name="DISPLAY_ORDER_LOCALITY")
    private int displayOrderLocality ;
    
    @FieldMetaInfo(displayName="DISPLAY ORDER SUBURB", description="DISPLAY ORDER SUBURB")
    @Column(name="DISPLAY_ORDER_SUBURB")
    private int displayOrderSuburb ;
    
    @FieldMetaInfo(displayName="BANK LIST", description="BANK LIST")
    @Column(name="BANK_LIST")
    private String bankList ;
    
    @FieldMetaInfo(displayName="YOUTUBE VEDIO", description="YOUTUBE VEDIO")
    @Column(name="YOUTUBE_VEDIO")
    private String youtubeVedio ;
    
    @FieldMetaInfo(displayName="PRICE LIST", description="PRICE LIST")
    @Column(name="PRICE_LIST")
    private String priceList ;
    
    @FieldMetaInfo(displayName="APPLICATION FORM", description="APPLICATION FORM")
    @Column(name="APPLICATION_FORM")
    private String applicationForm ;
    
    @FieldMetaInfo(displayName="IMPORTANCE", description="IMPORTANCE")
    @Column(name="IMPORTANCE")
    private int importance ;
    
    @FieldMetaInfo(displayName="SHOULD DISPLAY PRICE", description="SHOULD DISPLAY PRICE")
    @Column(name="SHOULD_DISPLAY_PRICE")
    private int shouldDisplayPrice ;
    
    @FieldMetaInfo(displayName="PROMISED COMPLETION Date", description="PROMISED COMPLETION Date")
    @Column(name="PROMISED_COMPLETION_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date promisedCompletionDate ;
    
    @FieldMetaInfo(displayName="AVAILABILITY", description="AVAILABILITY")
    @Column(name="AVAILABILITY")
    private Integer availability ;
    
    @FieldMetaInfo(displayName="PROJECT TYPE ID", description="PROJECT TYPE ID")
    @Column(name="PROJECT_TYPE_ID")
    private int projectTypeId ;
    
    @FieldMetaInfo(displayName="TOWNSHIP", description="TOWNSHIP")
    @Column(name="TOWNSHIP")
    private String township ;
    
    /*@FieldMetaInfo(displayName="RESIDENTIAL", description="RESIDENTIAL")
    @Column(name="RESIDENTIAL")
    private enum('0','1') RESIDENTIAL ;*/
    
    @FieldMetaInfo(displayName="PRE LAUNCH Date", description="PRE LAUNCH Date")
    @Column(name="PRE_LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date preLaunchDate ;
    
    @FieldMetaInfo(displayName="PROJECT SIZE", description="PROJECT SIZE")
    @Column(name="PROJECT_SIZE")
    private double projectSize ;
    
    @FieldMetaInfo(displayName="LAST MODIFIED Date", description="LAST MODIFIED Date")
    @Column(name="LAST_MODIFIED_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastModifiedDate ;
    
    @FieldMetaInfo(displayName="FORCE RESALE", description="FORCE RESALE")
    @Column(name="FORCE_RESALE")
    private int forceResale ;
    
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

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
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

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
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

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(int modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public int getOfflineType() {
        return offlineType;
    }

    public void setOfflineType(int offlineType) {
        this.offlineType = offlineType;
    }

    public int getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
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

    public String getBankList() {
        return bankList;
    }

    public void setBankList(String bankList) {
        this.bankList = bankList;
    }

    public String getYoutubeVedio() {
        return youtubeVedio;
    }

    public void setYoutubeVedio(String youtubeVedio) {
        this.youtubeVedio = youtubeVedio;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(String applicationForm) {
        this.applicationForm = applicationForm;
    }

    public int getShouldDisplayPrice() {
        return shouldDisplayPrice;
    }

    public void setShouldDisplayPrice(int shouldDisplayPrice) {
        this.shouldDisplayPrice = shouldDisplayPrice;
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

    public double getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(double projectSize) {
        this.projectSize = projectSize;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getForceResale() {
        return forceResale;
    }

    public void setForceResale(int forceResale) {
        this.forceResale = forceResale;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }
}
