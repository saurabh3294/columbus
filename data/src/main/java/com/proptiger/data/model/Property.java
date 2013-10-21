package com.proptiger.data.model;

import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.util.DoubletoIntegerConverter;

@ResourceMetaInfo(name = "Property")
@JsonFilter("fieldFilter")
public class Property implements BaseModel {
    @FieldMetaInfo( displayName = "Property Id",  description = "Property Id")
    @Field(value="TYPE_ID")
    private int propertyId;

    @FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
    @Field(value="PROJECT_ID")
    private int projectId;

    @FieldMetaInfo( displayName = "Bedrooms",  description = "Number of bedrooms")
    @Field(value="BEDROOMS")
    private int bedrooms;
    
    @FieldMetaInfo( displayName = "Bathrooms",  description = "Number of bathrooms")
    @Field(value="BATHROOMS")
    private int bathrooms;
    
    @FieldMetaInfo( displayName = "Unit type",  description = "Unit type")
    @Field(value="UNIT_TYPE")
    private String unitType;
    
    @FieldMetaInfo( displayName = "Unit name",  description = "Unit name")
    @Field(value="UNIT_NAME")
    private String unitName;

    @FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Price per unit area",  description = "Price per unit area")
    @Field(value="PRICE_PER_UNIT_AREA")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)    
    private Double pricePerUnitArea;

    @FieldMetaInfo( displayName = "Size",  description = "Size")
    @Field(value="SIZE")
    @JsonSerialize(converter=DoubletoIntegerConverter.class)
    private Double size;

    @FieldMetaInfo( displayName = "Measure",  description = "Measure")
    @Field(value="MEASURE")
    private String measure;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value="PROPERTY_URL")
    private String URL;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    private Project project;

    @Transient
    private List<Image> images;

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
}
