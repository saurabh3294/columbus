package com.proptiger.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.enums.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.util.DoubletoIntegerConverter;
import com.proptiger.data.util.UtilityClass;

@JsonFilter("fieldFilter")
@Entity
@Table(name = "cms.resi_project_options")
@JsonInclude(Include.NON_NULL)
public class Property extends BaseModel {

    private static final long serialVersionUID = -3350129763568409835L;

    
    @FieldMetaInfo(displayName = "Property Id", description = "Property Id")
    @Field(value = "TYPE_ID")
    @Column(name = "OPTIONS_ID")
    @Id
    private int               propertyId;

    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Field(value = "PROJECT_ID")
    @Column(name = "PROJECT_ID")
    private int               projectId;

    @FieldMetaInfo(displayName = "Bedrooms", description = "Number of bedrooms")
    @Field(value = "BEDROOMS")
    @Column(name = "BEDROOMS")
    private int               bedrooms;

    @FieldMetaInfo(displayName = "Bathrooms", description = "Number of bathrooms")
    @Field(value = "BATHROOMS")
    @Column(name = "BATHROOMS")
    private int               bathrooms;

    @FieldMetaInfo(displayName = "Unit type", description = "Unit type")
    @Field(value = "UNIT_TYPE")
    @Column(name = "OPTION_TYPE")
    private String            unitType;

    @FieldMetaInfo(displayName = "Unit name", description = "Unit name")
    @Field(value = "UNIT_NAME")
    @Column(name = "OPTION_NAME")
    private String            unitName;

    @FieldMetaInfo(
            dataType = DataType.CURRENCY,
            displayName = "Price per unit area",
            description = "Price per unit area")
    @Field(value = "PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    private Double            pricePerUnitArea;

    @FieldMetaInfo(displayName = "Size", description = "Size")
    @Field(value = "SIZE")
    @JsonSerialize(converter = DoubletoIntegerConverter.class)
    @JsonInclude(Include.NON_EMPTY)
    @Column(name = "SIZE")
    private Double            size;

    @FieldMetaInfo(displayName = "Measure", description = "Measure")
    @Field(value = "MEASURE")
    @Column(name = "MEASURE")
    private String            measure = "sqft";

    @FieldMetaInfo(displayName = "URL", description = "URL")
    @Field(value = "PROPERTY_URL")
    @Transient
    private String            URL;

    @FieldMetaInfo(displayName = "Locality Latitude", description = "Locality Latitude")
    @Field(value = "PROCESSED_LATITUDE")
    @Transient
    @JsonIgnore
    private Double            processedLatitude;

    @FieldMetaInfo(displayName = "Locality Longitude", description = "Locality Longitude")
    @Field(value = "PROCESSED_LONGITUDE")
    @Transient
    @JsonIgnore
    private Double            processedLongitude;

    @FieldMetaInfo(displayName = "Property Price", description = "Property Price")
    @Field(value = "BUDGET")
    @Transient
    private Double            budget;

    @FieldMetaInfo(displayName = "Project Id with Bedroom", description = "Project Id with Bedroom")
    @Field(value = "PROJECT_ID_BEDROOM")
    @JsonIgnore
    @Transient
    private String            projectIdBedroom;

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    @Transient
    private Project           project;

    @Transient
    private List<Image>       images;

    @Transient
    @FieldMetaInfo(displayName = "Resale price per unit area", description = "Resale price per unit area")
    @Field(value = "RESALE_PRICE_PER_UNIT_AREA")
    private Double            resalePricePerUnitArea;

    @Transient
    @FieldMetaInfo(displayName = "Resale Price", description = "Resale Price")
    @Field(value = "RESALE_PRICE")
    private Double            resalePrice;

    @Transient
    @FieldMetaInfo(displayName = "Servant Room", description = "Servant Room")
    @Field("SERVANT_ROOM")
    private Integer           servantRoom;

    @Transient
    @FieldMetaInfo(displayName = "Pooja Room", description = "Pooja Room")
    @Field("POOJA_ROOM")
    private Integer           poojaRoom;

    @Transient
    @Field("MIN_RESALE_OR_PRIMARY_PRICE")
    private Double            minResaleOrPrimaryPrice;

    @Transient
    @Field("MAX_RESALE_OR_PRIMARY_PRICE")
    private Double            maxResaleOrPrimaryPrice;

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
}
