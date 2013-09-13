package com.proptiger.data.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@ResourceMetaInfo(name = "Property")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Property {
    @FieldMetaInfo( displayName = "Id",  description = "Property Id")
    @Field(value="TYPE_ID")
    private long id;

    @FieldMetaInfo( displayName = "Project Id",  description = "Project Id")
    @Field(value="PROJECT_ID")
    private long projectId;

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

    @FieldMetaInfo( displayName = "Price per unit area",  description = "Price per unit area")
    @Field(value="PRICE_PER_UNIT_AREA")
    private float pricePerUnitArea;

    @FieldMetaInfo( displayName = "Size",  description = "Size")
    @Field(value="SIZE")
    private float size;

    @FieldMetaInfo( displayName = "Measure",  description = "Measure")
    @Field(value="MEASURE")
    private String measure;

    @FieldMetaInfo( displayName = "URL",  description = "URL")
    @Field(value="PROPERTY_URL")
    private String URL;

    @JsonUnwrapped
    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    private Project project;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public float getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    public void setPricePerUnitArea(float pricePerUnitArea) {
        this.pricePerUnitArea = pricePerUnitArea;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
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

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
