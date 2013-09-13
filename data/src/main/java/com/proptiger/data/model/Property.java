package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@ResourceMetaInfo(name = "Property")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Property {
    @FieldMetaInfo(name = "id", displayName = "Id", dataType = DataType.LONG, description = "Property Id")
    @Field(value="TYPE_ID")
    @JsonProperty
    private long id;

    @FieldMetaInfo(name = "projectId", displayName = "Project Id", dataType = DataType.LONG, description = "Project Id")
    @Field(value="PROJECT_ID")
    @JsonProperty
    private long projectId;

    @FieldMetaInfo(name = "bedrooms", displayName = "Bedrooms", dataType = DataType.INTEGER, description = "Number of bedrooms")
    @Field(value="BEDROOMS")
    @JsonProperty
    private int bedrooms;
    
    @FieldMetaInfo(name = "bathrooms", displayName = "Bathrooms", dataType = DataType.INTEGER, description = "Number of bathrooms")
    @Field(value="BATHROOMS")
    @JsonProperty
    private int bathrooms;    
    
    @FieldMetaInfo(name = "unitType", displayName = "Unit type", dataType = DataType.STRING, description = "Unit type")
    @Field(value="UNIT_TYPE")
    @JsonProperty
    private String unitType;
    
    @FieldMetaInfo(name = "unitName", displayName = "Unit name", dataType = DataType.STRING, description = "Unit name")
    @Field(value="UNIT_NAME")
    @JsonProperty
    private String unitName;

    @FieldMetaInfo(name = "pricePerUnitArea", displayName = "Price per unit area", dataType = DataType.CURRENCY, description = "Price per unit area")
    @JsonProperty
    @Field(value="PRICE_PER_UNIT_AREA")
    private float pricePerUnitArea;

    @FieldMetaInfo(name = "size", displayName = "Size", dataType = DataType.FLOAT, description = "Size")
    @Field(value="SIZE")
    @JsonProperty
    private float size;

    @FieldMetaInfo(name = "measure", displayName = "Measure", dataType = DataType.STRING, description = "Measure")
    @Field(value="MEASURE")
    @JsonProperty
    private String measure;

    @FieldMetaInfo(name = "URL", displayName = "URL", dataType = DataType.STRING, description = "URL")
    @Field(value="PROPERTY_URL")
    @JsonProperty
    private String URL;

    @JsonUnwrapped
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
