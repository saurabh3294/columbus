package com.proptiger.data.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.enums.DataType;
import com.proptiger.data.enums.EntityType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.Listing.OtherInfo;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.util.DoubletoIntegerConverter;
import com.proptiger.data.util.UtilityClass;

@JsonFilter("fieldFilter")
@Entity
@Table(name = "cms.resi_project_options")
@JsonInclude(Include.NON_NULL)
public class Property extends BaseModel {

    private static final long    serialVersionUID = -3350129763568409835L;

    @FieldMetaInfo(displayName = "Property Id", description = "Property Id")
    @Field(value = "TYPE_ID")
    @Column(name = "OPTIONS_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                  propertyId;

    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Field(value = "PROJECT_ID")
    @Column(name = "PROJECT_ID")
    private int                  projectId;

    @FieldMetaInfo(displayName = "Bedrooms", description = "Number of bedrooms")
    @Field(value = "BEDROOMS")
    @Column(name = "BEDROOMS")
    private int                  bedrooms;

    @FieldMetaInfo(displayName = "Bathrooms", description = "Number of bathrooms")
    @Field(value = "BATHROOMS")
    @Column(name = "BATHROOMS")
    private int                  bathrooms;

    @FieldMetaInfo(displayName = "Unit type", description = "Unit type")
    @Field(value = "UNIT_TYPE")
    @Column(name = "OPTION_TYPE")
    private String               unitType;

    @FieldMetaInfo(displayName = "Unit name", description = "Unit name")
    @Field(value = "UNIT_NAME")
    @Column(name = "OPTION_NAME")
    private String               unitName;

    @Transient
    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Price per unit area",
            description = "Price per unit area")
    @Field(value = "PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double               pricePerUnitArea;

    @FieldMetaInfo(displayName = "Size", description = "Size")
    @Field(value = "SIZE")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    @Column(name = "SIZE")
    private Double               size;

    @Transient
    @FieldMetaInfo(displayName = "Measure", description = "Measure")
    @Field(value = "MEASURE")
    private String               measure          = "sq ft";

    @FieldMetaInfo(displayName = "URL", description = "URL")
    @Field(value = "PROPERTY_URL")
    @Transient
    private String               URL;

    @FieldMetaInfo(displayName = "Locality Latitude", description = "Locality Latitude")
    @Field(value = "PROCESSED_LATITUDE")
    @Transient
    @JsonIgnore
    private Double               processedLatitude;

    @FieldMetaInfo(displayName = "Locality Longitude", description = "Locality Longitude")
    @Field(value = "PROCESSED_LONGITUDE")
    @Transient
    @JsonIgnore
    private Double               processedLongitude;

    @FieldMetaInfo(displayName = "Property Price", description = "Property Price")
    @Field(value = "BUDGET")
    @Transient
    private Double               budget;

    @FieldMetaInfo(displayName = "Project Id with Bedroom", description = "Project Id with Bedroom")
    @Field(value = "PROJECT_ID_BEDROOM")
    @JsonIgnore
    @Transient
    private String               projectIdBedroom;

    // TODO making it as lazy, since there would be two entry for a project id
    // with version Website and cms.
    // TODO should be handled properly rather than making LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", insertable = false, updatable = false)
    private Project              project;

    @Transient
    private List<Image>          images;

    @Transient
    @FieldMetaInfo(displayName = "Resale price per unit area", description = "Resale price per unit area")
    @Field(value = "RESALE_PRICE_PER_UNIT_AREA")
    private Double               resalePricePerUnitArea;

    @Transient
    @FieldMetaInfo(displayName = "Resale Price", description = "Resale Price")
    @Field(value = "RESALE_PRICE")
    private Double               resalePrice;

    @FieldMetaInfo(displayName = "Servant Room", description = "Servant Room")
    @Field("SERVANT_ROOM")
    @Column(name = "SERVANT_ROOM")
    private Integer              servantRoom;

    @FieldMetaInfo(displayName = "Pooja Room", description = "Pooja Room")
    @Field("POOJA_ROOM")
    @Column(name = "POOJA_ROOM")
    private Integer              poojaRoom;

    @Transient
    @Field("MIN_RESALE_OR_PRIMARY_PRICE")
    private Double               minResaleOrPrimaryPrice;

    @Transient
    @Field("MAX_RESALE_OR_PRIMARY_PRICE")
    private Double               maxResaleOrPrimaryPrice;

    @Transient
    @Field("PROJECT_NAME")
    private String               projectName;

    @Column(name = "OPTION_CATEGORY")
    @Enumerated(EnumType.STRING)
    private EntityType           optionCategory;

    @Column(name = "STUDY_ROOM")
    private int                  studyRoom;

    @Column(name = "BALCONY")
    private int                  balcony;

    @Column(name = "DISPLAY_CARPET_AREA")
    private int                  displayCarpetArea;

    @Column(name = "updated_by")
    private Integer              updatedBy;

    @Column(name = "created_at")
    private Date                 createdAt;

    @Column(name = "updated_at")
    private Date                 updatedAt;

    @Transient
    @Field("IMAGE_TYPE_COUNT")
    private Map<String, Integer> imageTypeCount;

    @Transient
    private List<Media>          media;

    @Transient
    private CouponCatalogue      couponCatalogue;

    @Transient
    @Field("PROPERTY_COUPON_AVAILABLE")
    private Boolean              isCouponAvailable;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    public void setPricePerUnitArea(Double pricePerUnitArea) {
        this.pricePerUnitArea = pricePerUnitArea;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Double getProcessedLatitue() {
        return processedLatitude;
    }

    public void setProcessedLatitude(Double processedLatitude) {
        this.processedLatitude = processedLatitude;
    }

    public Double getProcessedLongitude() {
        return processedLongitude;
    }

    public void setProcessedLongitude(Double processedLongitude) {
        this.processedLongitude = processedLongitude;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getProjectIdBedroom() {
        return projectIdBedroom;
    }

    public void setProjectIdBedroom(String projectIdBedroom) {
        this.projectIdBedroom = projectIdBedroom;
    }

    public Double getResalePricePerUnitArea() {
        return resalePricePerUnitArea;
    }

    public void setResalePricePerUnitArea(Double resalePricePerUnitArea) {
        this.resalePricePerUnitArea = resalePricePerUnitArea;
    }

    public Double getResalePrice() {
        return resalePrice;
    }

    public void setResalePrice(Double resalePrice) {
        this.resalePrice = resalePrice;
    }

    public Double getProcessedLatitude() {
        return processedLatitude;
    }

    public Integer getServantRoom() {
        return servantRoom;
    }

    public void setServantRoom(Integer servantRoom) {
        this.servantRoom = servantRoom;
    }

    public Integer getPoojaRoom() {
        return poojaRoom;
    }

    public void setPoojaRoom(Integer poojaRoom) {
        this.poojaRoom = poojaRoom;
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

    public Property populateResalePrice() {
        if (this.resalePricePerUnitArea != null && this.size != null) {
            this.resalePrice = this.resalePricePerUnitArea * this.size;
        }
        return this;
    }

    public Property populateBudget() {
        if (this.pricePerUnitArea != null && this.size != null) {
            this.budget = this.pricePerUnitArea * this.size;
        }
        return this;
    }

    public void populateMinResaleOrPrimaryPrice() {
        this.minResaleOrPrimaryPrice = UtilityClass.min(this.budget, this.resalePrice);
    }

    public void populateMaxResaleOrPrimaryPrice() {
        this.maxResaleOrPrimaryPrice = UtilityClass.max(this.budget, this.resalePrice);
    }

    public EntityType getOptionCategory() {
        return optionCategory;
    }

    public void setOptionCategory(EntityType optionCategory) {
        this.optionCategory = optionCategory;
    }

    public int getStudyRoom() {
        return studyRoom;
    }

    public void setStudyRoom(int studyRoom) {
        this.studyRoom = studyRoom;
    }

    public int getBalcony() {
        return balcony;
    }

    public void setBalcony(int balcony) {
        this.balcony = balcony;
    }

    public int getDisplayCarpetArea() {
        return displayCarpetArea;
    }

    public void setDisplayCarpetArea(int displayCarpetArea) {
        this.displayCarpetArea = displayCarpetArea;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    public static Property createUnverifiedProperty(Integer createdBy, OtherInfo otherInfo, String unitType) {
        Property toCreate = new Property();
        toCreate.setOptionCategory(EntityType.Unverified);
        toCreate.setSize(Double.valueOf(otherInfo.getSize()));
        toCreate.setProjectId(otherInfo.getProjectId());
        toCreate.setBathrooms(otherInfo.getBathrooms());
        toCreate.setBedrooms(otherInfo.getBedrooms());
        toCreate.setUnitName(otherInfo.getBedrooms() + "BHK"
                + (otherInfo.getBathrooms() > 0 ? "+" + otherInfo.getBathrooms() + "T" : ""));
        toCreate.setUnitType(unitType);
        toCreate.setStudyRoom(0);
        toCreate.setServantRoom(0);
        toCreate.setBalcony(0);
        toCreate.setPoojaRoom(0);
        toCreate.setDisplayCarpetArea(0);
        toCreate.setUpdatedBy(createdBy);
        return toCreate;
    }

    public void setImageTypeCount(Map<String, Integer> imageTypeCount) {
        this.imageTypeCount = imageTypeCount;
    }

    public CouponCatalogue getCouponCatalogue() {
        return couponCatalogue;
    }

    public void setCouponCatalogue(CouponCatalogue couponCatalogue) {
        this.couponCatalogue = couponCatalogue;
    }

    public Boolean isCouponAvailable() {
        return isCouponAvailable;
    }

    public void setCouponAvailable(Boolean isCouponAvailable) {
        this.isCouponAvailable = isCouponAvailable;
    }
}
