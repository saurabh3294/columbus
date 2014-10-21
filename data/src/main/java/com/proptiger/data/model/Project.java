/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.proptiger.data.enums.DataType;
import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.ResidentialFlag;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * 
 * @author mukand
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "cms.resi_project")
@JsonFilter("fieldFilter")
public class Project extends BaseModel {
    private static final long serialVersionUID = -6635164496425100051L;

    public static enum NestedProperties {
        builderLabel(new String[] { "builder", "name" }), cityLabel(new String[] {
                "locality",
                "suburb",
                "city",
                "label" }), suburbLabel(new String[] { "locality", "suburb", "label" }), cityId(new String[] {
                "locality",
                "suburb",
                "city",
                "cityId",
                "id" }), suburbId(new String[] { "locality", "suburb", "suburbId", "id" }), localityLabel(new String[] {
                "locality",
                "label" }), builderImageURL(new String[] { "builder", "imageURL" }), bedrooms(new String[] {
                "properties",
                "bedrooms" }), bathrooms(new String[] { "properties", "bathrooms" }), pricePerUnitArea(new String[] {
                "properties",
                "pricePerUnitArea" }), size(new String[] { "properties", "size" }), unitName(new String[] {
                "properties",
                "unitName" }), unitType(new String[] { "properties", "unitType" });

        private String[] fields;

        private NestedProperties(String[] fields) {
            this.fields = fields;
        }

        public String[] getFields() {
            return fields;
        }
    };

    @JsonInclude(Include.NON_NULL)
    public static class Offer extends BaseModel {
        private static final long serialVersionUID = -3760823398693160737L;
        private String            offer;
        private String            offerHeading;
        private String            offerDesc;

        public String getOffer() {
            return offer;
        }

        public String getOfferHeading() {
            return offerHeading;
        }

        public String getOfferDesc() {
            return offerDesc;
        }
    }

    @Id
    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Field(value = "PROJECT_ID")
    @Column(name = "PROJECT_ID", insertable = false, updatable = false)
    private int                     projectId;

    @Transient
    private boolean                 authorized          = false;

    @Column(name = "VERSION")
    @Enumerated(EnumType.STRING)
    private DataVersion             version;

    @Deprecated
    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Field(value = "LOCALITY_ID")
    @Column(name = "LOCALITY_ID")
    private int                     localityId;

    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", insertable = false, updatable = false)
    private Locality                locality;

    @FieldMetaInfo(displayName = "Builder Id", description = "Builder Id")
    @Field(value = "BUILDER_ID")
    @Column(name = "BUILDER_ID")
    private int                     builderId;

    @ManyToOne
    @JoinColumn(name = "BUILDER_ID", insertable = false, updatable = false)
    private Builder                 builder;

    @Transient
    @OneToMany(mappedBy = "project")
    private List<Property>          properties;

    @FieldMetaInfo(displayName = "Project Name", description = "Project Name")
    @Field(value = "PROJECT_NAME")
    @Column(name = "PROJECT_NAME")
    private String                  name;

    @Transient
    @FieldMetaInfo(displayName = "Project Types", description = "Project Types")
    @Field(value = "PROJECT_TYPES")
    private String                  unitTypes;

    @FieldMetaInfo(displayName = "Launch Date", description = "Launch Date")
    @Field(value = "VALID_LAUNCH_DATE")
    @Column(name = "LAUNCH_DATE")
    private Date                    launchDate;

    @FieldMetaInfo(displayName = "Address", description = "Address")
    @Field(value = "PROJECT_ADDRESS")
    @Column(name = "PROJECT_ADDRESS")
    private String                  address;

    @Transient
    @FieldMetaInfo(displayName = "Computed Priority", description = "Computed Priority")
    @Field(value = "PROJECT_PRIORITY")
    private double                  computedPriority;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "Project enquiry count", description = "Project enquiry count")
    @Field(value = "PROJECT_ENQUIRY_COUNT")
    private Integer                 projectEnquiryCount;

    @FieldMetaInfo(displayName = "Assigned Priority", description = "Assigned Priority")
    @Field(value = "DISPLAY_ORDER")
    @Column(name = "DISPLAY_ORDER")
    private int                     assignedPriority;

    @FieldMetaInfo(displayName = "Assigned Locality Priority", description = "Assigned Locality Priority")
    @Field(value = "DISPLAY_ORDER_LOCALITY")
    @Column(name = "DISPLAY_ORDER_LOCALITY")
    private Integer                 assignedLocalityPriority;

    @FieldMetaInfo(displayName = "Assigned Suburb Priority", description = "Assigned Suburb Priority")
    @Field(value = "DISPLAY_ORDER_SUBURB")
    @Column(name = "DISPLAY_ORDER_SUBURB")
    private Integer                 assignedSuburbPriority;

    @FieldMetaInfo(displayName = "Possession Date", description = "Possession Date")
    @Field(value = "PROMISED_COMPLETION_DATE")
    @Column(name = "PROMISED_COMPLETION_DATE")
    private Date                    possessionDate;

    @Transient
    @FieldMetaInfo(displayName = "Submitted Date", description = "Submitted Date")
    @Field(value = "SUBMITTED_DATE")
    private Date                    submittedDate;

    // XXX - In order to make itnot null and avoid App crash
    @FieldMetaInfo(displayName = "Image URL", description = "Image URL")
    @Transient
    @Field("PROJECT_SMALL_IMAGE")
    private String                  imageURL            = "";

    @Transient
    @FieldMetaInfo(displayName = "Offer", description = "Offer")
    @Field(value = "OFFER")
    @Deprecated
    private String                  offer;

    @Transient
    @FieldMetaInfo(displayName = "Offer Heading", description = "Offer Heading")
    @Field(value = "OFFER_HEADING")
    @Deprecated
    private String                  offerHeading;

    @Transient
    @FieldMetaInfo(displayName = "Offer Description", description = "Offer Description")
    @Field(value = "OFFER_DESC")
    @Deprecated
    private String                  offerDesc;

    @FieldMetaInfo(displayName = "URL", description = "URL")
    @Field(value = "PROJECT_URL")
    @Column(name = "PROJECT_URL")
    private String                  URL;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Field(value = "LATITUDE")
    @Column(name = "LATITUDE")
    private Double                  latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Field(value = "LONGITUDE")
    @Column(name = "LONGITUDE")
    private Double                  longitude;

    @Transient
    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Min Price Per Unit Area",
            description = "Min Price Per Unit Area")
    @Field(value = "MIN_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  minPricePerUnitArea;

    @Transient
    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Max Price Per Unit Area",
            description = "Max Price Per Unit Area")
    @Field(value = "MAX_PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  maxPricePerUnitArea;

    @Transient
    @FieldMetaInfo(displayName = "Min Size", description = "Min Size")
    @Field(value = "MINSIZE")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  minSize;

    @Transient
    @FieldMetaInfo(displayName = "Max Size", description = "Max Size")
    @Field(value = "MAXSIZE")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  maxSize;

    @Transient
    @FieldMetaInfo(displayName = "Min Price", description = "Min Price")
    @Field(value = "MIN_BUDGET")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  minPrice;

    @Transient
    @FieldMetaInfo(displayName = "Max Price", description = "Max Price")
    @Field(value = "MAX_BUDGET")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double                  maxPrice;

    @Transient
    @FieldMetaInfo(displayName = "Min Bedroooms", description = "Min Bedroooms")
    private int                     minBedrooms;

    @Transient
    @FieldMetaInfo(displayName = "Max Bedroooms", description = "Max Bedroooms")
    private int                     maxBedrooms;

    @Column(name = "PROJECT_STATUS_ID")
    @JsonIgnore
    private int                     projectStatusId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "PROJECT_STATUS_ID", insertable = false, updatable = false)
    private ProjectStatusMaster     projectStatusMaster;

    @Transient
    @FieldMetaInfo(displayName = "PROJECT STATUS", description = "PROJECT STATUS")
    @Field(value = "PROJECT_STATUS")
    private String                  projectStatus;
    
    @Transient
    private boolean                 forceResale  = false;

    @Transient
    @Field(value = "IS_RESALE")
    private boolean                 isResale;

    @Transient
    @Field(value = "IS_PRIMARY")
    private boolean                 isPrimary;

    @Transient
    @Field(value = "IS_SOLD_OUT")
    private boolean                 isSoldOut;

    @FieldMetaInfo(displayName = "Project Description", description = "Project Description")
    @Field(value = "PROJECT_DESCRIPTION")
    @Column(name = "PROJECT_DESCRIPTION")
    private String                  description;

    @Transient
    @FieldMetaInfo(displayName = "Total Units", description = "Total Units")
    @Field(value = "TOTAL_UNITS")
    private Integer                 totalUnits;

    @FieldMetaInfo(displayName = "size in acres", description = "size in acres")
    @Field(value = "PROJECT_SIZE")
    @Column(name = "PROJECT_SIZE")
    private Double                  sizeInAcres;

    @Transient
    @Field(value = "GEO")
    private List<String>            geo;

    @Transient
    @Field(value = "PROJECT_STATUS_BEDROOM")
    @JsonIgnore
    private String                  projectStatusBedroom;

    @Transient
    @Field(value = "MEASURE")
    private String                  propertySizeMeasure = "sq ft";

    @Transient
    @Field(value = "PROJECT_DOMINANT_UNIT_TYPE")
    private String                  dominantUnitType;

    @Transient
    private Set<String>             propertyUnitTypes   = new HashSet<>();

    @Transient
    private List<Image>             images;

    @Transient
    private Image                   mainImage;

    @Transient
    @Field(value = "LOCALITY_LABEL_PRIORITY")
    private String                  localityLabelPriority;

    @Transient
    @Field(value = "SUBURB_LABEL_PRIORITY")
    private String                  suburbLabelPriority;

    @Transient
    @Field(value = "BUILDER_LABEL_PRIORITY")
    private String                  builderLabelPriority;

    @Transient
    private Set<Integer>            distinctBedrooms    = new HashSet<>();

    @Transient
    private Double                  minResalePrice;

    @Transient
    private Double                  maxResalePrice;

    @Transient
    private Double                  avgPriceRisePercentage;

    @Transient
    private Integer                 avgPriceRiseMonths;

    @FieldMetaInfo(displayName = "AVAILABILITY", description = "AVAILABILITY")
    @Column(name = "D_AVAILABILITY")
    @Field("AVAILABILITY")
    private Integer                 derivedAvailability;

    @FieldMetaInfo(displayName = "PRE LAUNCH Date", description = "PRE LAUNCH Date")
    @Column(name = "PRE_LAUNCH_DATE")
    @Field("PRE_LAUNCH_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date                    preLaunchDate;

    @FieldMetaInfo(displayName = "YOUTUBE VEDIO", description = "YOUTUBE VEDIO")
    @Column(name = "YOUTUBE_VIDEO")
    @JsonIgnore
    private String                  youtubeVideo;

    @Transient
    @FieldMetaInfo(displayName = "NO OF FLATS", description = "NO OF FLATS")
    @Field("PROJECT_SUPPLY")
    private Integer                 supply;

    @OneToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(insertable = false, updatable = false, name = "PROJECT_ID")
    @Transient
    @Deprecated
    private List<ProjectAmenity>    projectAmenity;

    @Transient
    @Field("NUMBER_OF_PROJECT_DISCUSSION")
    private Integer                 totalProjectDiscussion;

    @Transient
    private List<LandMark>          neighborhood;

    @JsonUnwrapped
    @Transient
    private ProjectSpecification    projectSpecification;

    @Transient
    private List<ProjectCMSAmenity> projectAmenities;

    @Field("PAYMENT_PLAN_URL")
    @Column(name = "APPLICATION_FORM")
    private String                  paymentPlanUrl;

    @Transient
    private List<VideoLinks>        videoUrls;

    @Transient
    private List<Bank>              loanProviderBanks;

    @Transient
    @Field("PROJECT_OFFER")
    private List<Offer>             offers;

    @Transient
    @Field("PROJECT_LAST_UPDATED_TIME")
    private Date                    lastUpdatedDate;

    @Transient
    @Field("geodist()")
    private Double                  geoDistance;

    @Transient
    @Field("PROJECT_IMAGES_COUNT")
    private Integer                 imagesCount;

    @Transient
    private Map<String, Integer>    imageCountByType;

    @Transient
    @Field("PROJECT_VIDEOS_COUNT")
    private Integer                 videosCount;

    @Transient
    @Field("PROJECT_AVG_PRICE_PER_UNIT_AREA")
    private Double                  avgPricePerUnitArea;

    @Transient
    @Field("MIN_RESALE_OR_PRIMARY_PRICE")
    private Double                  minResaleOrPrimaryPrice;

    @Transient
    @Field("RESALE_PRICE_PER_UNIT_AREA")
    private Double                  resalePricePerUnitArea;

    @Transient
    @Field("MAX_RESALE_OR_PRIMARY_PRICE")
    private Double                  maxResaleOrPrimaryPrice;

    @Transient
    @Field("PROJECT_PRICE_RISE_6MONTHS")
    private Double                  priceRise6Months;

    @Transient
    @JsonIgnore
    @FieldMetaInfo(displayName = "Project view count", description = "Project view count")
    @Field(value = "PROJECT_VIEW_COUNT")
    private Integer                 projectViewCount;

    @Transient
    @Field(value = "PROJECT_SAFETY_SCORE")
    private Double                  safetyScore;

    @Transient
    @Field(value = "PROJECT_LIVABILITY_SCORE")
    private Float                   livabilityScore;

    @Transient
    @Field(value = "PROJECT_LOCALITY_SCORE")
    private Float                   projectLocalityScore;

    @Transient
    @Field(value = "PROJECT_SOCIETY_SCORE")
    private Float                   projectSocietyScore;

    @Transient
    @Field(value = "PROJECT_SAFETY_RANK")
    private Integer                 projectSafetyRank;

    @Transient
    @Field(value = "PROJECT_LIVABILITY_RANK")
    private Integer                 projectLivabilityRank;

    @Transient
    private boolean                 has3DImages;

    @JsonIgnore
    @Column(name = "RESIDENTIAL_FLAG")
    @Enumerated(EnumType.STRING)
    private ResidentialFlag         residentialFlag;

    @Transient
    private List<Image>              landmarkImages;

    @Transient
    @Field("IMAGE_TYPE_COUNT")
    private Map<String, Integer>	 imageTypeCount;

    @Transient
    private Integer                 maxDiscount;

    @Transient
    private Integer                 couponsInventoryLeft;

    @Transient
    private Integer                 totalCouponsInventory;

    @Transient
    private Double                  minDiscountPrice;

    @Transient
    private Double                  maxDiscountPrice;

    @Transient
    private Double                  minResaleOrDiscountPrice;

    @Transient
    private Double                  maxResaleOrDiscountPrice;

    @Transient
    private Boolean                 isCouponAvailable;

    @Transient
    private Date                    maxCouponExpiryAt;

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

    public Integer getProjectEnquiryCount() {
        return projectEnquiryCount;
    }

    public void setProjectEnquiryCount(Integer projectEnquiryCount) {
        this.projectEnquiryCount = projectEnquiryCount;
    }

    public int getAssignedPriority() {
        return assignedPriority;
    }

    public void setAssignedPriority(int assignedPriority) {
        this.assignedPriority = assignedPriority;
    }

    public Integer getAssignedLocalityPriority() {
        return assignedLocalityPriority;
    }

    public void setAssignedLocalityPriority(Integer assignedLocalityPriority) {
        this.assignedLocalityPriority = assignedLocalityPriority;
    }

    public Integer getAssignedSuburbPriority() {
        return assignedSuburbPriority;
    }

    public void setAssignedSuburbPriority(Integer assignedSuburbPriority) {
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
        this.imageURL = Image.addImageHostUrl(imageURL);
    }

    @Deprecated
    public String getOffer() {
        return offer;
    }

    @Deprecated
    public void setOffer(String offer) {
        this.offer = offer;
    }

    @Deprecated
    public String getOfferHeading() {
        return offerHeading;
    }

    @Deprecated
    public void setOfferHeading(String offerHeading) {
        this.offerHeading = offerHeading;
    }

    @Deprecated
    public String getOfferDesc() {
        return offerDesc;
    }

    @Deprecated
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

    public int getProjectStatusId() {
        return projectStatusId;
    }

    public void setProjectStatusId(int projectStatusId) {
        this.projectStatusId = projectStatusId;
    }

    public ProjectStatusMaster getProjectStatusMaster() {
        return projectStatusMaster;
    }

    public void setProjectStatusMaster(ProjectStatusMaster projectStatusMaster) {
        this.projectStatusMaster = projectStatusMaster;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public boolean isForceResale() {
        return forceResale;
    }

    public void setForceResale(boolean forceResale) {
        this.forceResale = forceResale;
    }

    @PostLoad
    public void postLoad() {
        this.projectStatus = projectStatusMaster.getDisplayName();
    }

    public boolean isIsResale() {
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

    public void addPropertyUnitType(String propertyUnitType) {
        this.propertyUnitTypes.add(propertyUnitType);
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getLocalityLabelPriority() {
        return localityLabelPriority;
    }

    public void setLocalityLabelPriority(String localityLabelPriority) {
        this.localityLabelPriority = localityLabelPriority;
    }

    public String getSuburbLabelPriority() {
        return suburbLabelPriority;
    }

    public void setSuburbLabelPriority(String suburbLabelPriority) {
        this.suburbLabelPriority = suburbLabelPriority;
    }

    public String getBuilderLabelPriority() {
        return builderLabelPriority;
    }

    public void setBuilderLabelPriority(String builderLabelPriority) {
        this.builderLabelPriority = builderLabelPriority;
    }

    public Set<Integer> getDistinctBedrooms() {
        return distinctBedrooms;
    }

    public void setDistinctBedrooms(Set<Integer> bedrooms) {
        this.distinctBedrooms = bedrooms;
    }

    public void addBedrooms(int bedroom) {
        this.distinctBedrooms.add(bedroom);
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

    public boolean isResale() {
        return isResale;
    }

    public Date getPreLaunchDate() {
        return preLaunchDate;
    }

    public void setPreLaunchDate(Date preLaunchDate) {
        this.preLaunchDate = preLaunchDate;
    }

    public Integer getDerivedAvailability() {
        return derivedAvailability;
    }

    public void setDerivedAvailability(Integer derivedAvailability) {
        this.derivedAvailability = derivedAvailability;
    }

    public String getYoutubeVideo() {
        return youtubeVideo;
    }

    public void setYoutubeVideo(String youtubeVideo) {
        this.youtubeVideo = youtubeVideo;
    }

    public Integer getSupply() {
        return supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

    public List<ProjectAmenity> getProjectAmenity() {
        return projectAmenity;
    }

    public void setProjectAmenity(List<ProjectAmenity> projectAmenity) {
        this.projectAmenity = projectAmenity;
    }

    public Integer getTotalProjectDiscussion() {
        return totalProjectDiscussion;
    }

    public void setTotalProjectDiscussion(Integer totalProjectDiscussion) {
        this.totalProjectDiscussion = totalProjectDiscussion;
    }

    public List<LandMark> getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(List<LandMark> neighborhood) {
        this.neighborhood = neighborhood;
    }

    public ProjectSpecification getProjectSpecification() {
        return projectSpecification;
    }

    public void setProjectSpecification(ProjectSpecification projectSpecification) {
        this.projectSpecification = projectSpecification;
    }

    public List<ProjectCMSAmenity> getProjectAmenities() {
        return projectAmenities;
    }

    public void setProjectAmenities(List<ProjectCMSAmenity> projectAmenities) {
        this.projectAmenities = projectAmenities;
    }

    public String getPaymentPlanUrl() {
        return paymentPlanUrl;
    }

    public void setPaymentPlanUrl(String paymentPlanUrl) {
        this.paymentPlanUrl = paymentPlanUrl;
    }

    public List<VideoLinks> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<VideoLinks> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public List<Bank> getLoanProviderBanks() {
        return loanProviderBanks;
    }

    public void setLoanProviderBanks(List<Bank> loanProviderBanks) {
        this.loanProviderBanks = loanProviderBanks;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public void addOffers(String[] offers) {
        if (this.offers == null)
            this.offers = new ArrayList<>();

        Gson gson = new Gson();
        for (int i = 0; i < offers.length; i++) {
            this.offers.add(gson.fromJson(offers[i], Offer.class));
        }
    }

    public Double getGeoDistance() {
        return geoDistance;
    }

    public void setGeoDistance(Double geoDistance) {
        this.geoDistance = geoDistance;
    }

    public Integer getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(Integer projectImagesCount) {
        this.imagesCount = projectImagesCount;
    }

    public Integer getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(Integer projectVideosCount) {
        this.videosCount = projectVideosCount;
    }

    public Double getAvgPricePerUnitArea() {
        return avgPricePerUnitArea;
    }

    public void setAvgPricePerUnitArea(Double avgPricePerUnitArea) {
        this.avgPricePerUnitArea = avgPricePerUnitArea;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public Double getMinResaleOrPrimaryPrice() {
        return minResaleOrPrimaryPrice;
    }

    public void setMinResaleOrPrimaryPrice(Double minResaleOrPrimaryPrice) {
        this.minResaleOrPrimaryPrice = minResaleOrPrimaryPrice;
    }

    public Double getMaxResaleOrPrimaryPrice() {
        return maxResaleOrPrimaryPrice;
    }

    public void setMaxResaleOrPrimaryPrice(Double maxResaleOrPrimaryPrice) {
        this.maxResaleOrPrimaryPrice = maxResaleOrPrimaryPrice;
    }

    public String getDominantUnitType() {
        return dominantUnitType;
    }

    public void setDominantUnitType(String dominantUnitType) {
        this.dominantUnitType = dominantUnitType;
    }

    public Double getPriceRise6Months() {
        return priceRise6Months;
    }

    public void setPriceRise6Months(Double priceRise6Months) {
        this.priceRise6Months = priceRise6Months;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public Integer getProjectViewCount() {
        return projectViewCount;
    }

    public void setProjectViewCount(Integer projectViewCount) {
        this.projectViewCount = projectViewCount;
    }

    public Double getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(Double safetyScore) {
        this.safetyScore = safetyScore;
    }

    public Float getLivabilityScore() {
        return livabilityScore;
    }

    public void setLivabilityScore(Float livabilityScore) {
        this.livabilityScore = livabilityScore;
    }

    public Map<String, Integer> getImageCountByType() {
        return imageCountByType;
    }

    public void setImageCountByType(Map<String, Integer> imageCountByType) {
        this.imageCountByType = imageCountByType;
    }

    public Image getMainImage() {
        return mainImage;
    }

    public void setMainImage(Image mainImage) {
        this.mainImage = mainImage;
    }

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }

    public Float getProjectLocalityScore() {
        return projectLocalityScore;
    }

    public void setProjectLocalityScore(Float projectLocalityScore) {
        this.projectLocalityScore = projectLocalityScore;
    }

    public Float getProjectSocietyScore() {
        return projectSocietyScore;
    }

    public void setProjectSocietyScore(Float projectSocietyScore) {
        this.projectSocietyScore = projectSocietyScore;
    }

    public Integer getProjectSafetyRank() {
        return projectSafetyRank;
    }

    public void setProjectSafetyRank(Integer projectSafetyRank) {
        this.projectSafetyRank = projectSafetyRank;
    }

    public Integer getProjectLivabilityRank() {
        return projectLivabilityRank;
    }

    public void setProjectLivabilityRank(Integer projectLivabilityRank) {
        this.projectLivabilityRank = projectLivabilityRank;
    }

    public void setImageTypeCount(Map<String, Integer> imageTypeCount) {
        this.imageTypeCount = imageTypeCount;
    }

    public boolean isHas3DImages() {
        return has3DImages;
    }

    public void setHas3DImages(boolean has3dImages) {
        has3DImages = has3dImages;
    }

    public ResidentialFlag getResidentialFlag() {
        return residentialFlag;
    }

    public void setResidentialFlag(ResidentialFlag residentialFlag) {
        this.residentialFlag = residentialFlag;
    }
	
    public List<Image> getLandmarkImages() {
        return landmarkImages;
    }

    public void setLandmarkImages(List<Image> landmarkImages) {
        this.landmarkImages = landmarkImages;
    }

    public Integer getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Integer maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getCouponsInventoryLeft() {
        return couponsInventoryLeft;
    }

    public void setCouponsInventoryLeft(Integer couponsInventoryLeft) {
        this.couponsInventoryLeft = couponsInventoryLeft;
    }

    public Integer getTotalCouponsInventory() {
        return totalCouponsInventory;
    }

    public void setTotalCouponsInventory(Integer totalCouponsInventory) {
        this.totalCouponsInventory = totalCouponsInventory;
    }

    public Double getMinDiscountPrice() {
        return minDiscountPrice;
    }

    public void setMinDiscountPrice(Double minDiscountPrice) {
        this.minDiscountPrice = minDiscountPrice;
    }

    public Double getMaxDiscountPrice() {
        return maxDiscountPrice;
    }

    public void setMaxDiscountPrice(Double maxDiscountPrice) {
        this.maxDiscountPrice = maxDiscountPrice;
    }

    public Double getMinResaleOrDiscountPrice() {
        return minResaleOrDiscountPrice;
    }

    public void setMinResaleOrDiscountPrice(Double minResaleOrDiscountPrice) {
        this.minResaleOrDiscountPrice = minResaleOrDiscountPrice;
    }

    public Double getMaxResaleOrDiscountPrice() {
        return maxResaleOrDiscountPrice;
    }

    public void setMaxResaleOrDiscountPrice(Double maxResaleOrDiscountPrice) {
        this.maxResaleOrDiscountPrice = maxResaleOrDiscountPrice;
    }

    public Boolean isCouponAvailable() {
        return isCouponAvailable;
    }

    public void setCouponAvailable(Boolean isCouponAvailable) {
        this.isCouponAvailable = isCouponAvailable;
    }

    public Map<String, Integer> getImageTypeCount() {
        return imageTypeCount;
    }

    public Boolean getIsCouponAvailable() {
        return isCouponAvailable;
    }

    public void setIsCouponAvailable(Boolean isCouponAvailable) {
        this.isCouponAvailable = isCouponAvailable;
    }

    public Date getMaxCouponExpiryAt() {
        return maxCouponExpiryAt;
    }

    public void setMaxCouponExpiryAt(Date maxCouponExpiryAt) {
        this.maxCouponExpiryAt = maxCouponExpiryAt;
    }

    public Double getResalePricePerUnitArea() {
        return resalePricePerUnitArea;
    }

    public void setResalePricePerUnitArea(Double resalePricePerUnitArea) {
        this.resalePricePerUnitArea = resalePricePerUnitArea;
    }
}
