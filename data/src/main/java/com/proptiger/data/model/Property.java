package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class Property extends Project{
@Field(value="id")
    @JsonProperty(value="id")
    private String id;
    
    @Field(value="TYPE_ID")
    @JsonProperty(value="type_id")
    private long typeId;
    
    @Field(value="BEDROOMS")
    @JsonProperty(value="bedrooms")
    private long bedrooms;
    
    @Field(value="BATHROOMS")
    @JsonProperty(value="bathrooms")
    private long bathrooms;
    
    @Field(value="UNIT_TYPE")
    @JsonProperty(value="unit_type")
    private String unitType;
    
    @Field(value="UNIT_NAME")
    @JsonProperty(value="unit_name")
    private String unitName;
    
    @Field(value="PROJECT_TYPE")
    @JsonProperty(value="project_type")
    private String projectType;
    
    @Field(value="PRICE_PER_UNIT_AREA")
    @JsonProperty(value="price_per_unit_area")
    private float pricePerUnitArea;
    
    @Field(value="SIZE")
    @JsonProperty(value="size")
    private float size;
    
    @Field(value="BUDGET")
    @JsonProperty(value="budget")
    private float budget;
    
    @Field(value="PRICE")
    @JsonProperty(value="price")
    private String price;
    
    @Field(value="PROPERTY_URL")
    @JsonProperty(value="property_url")
    private String propertyUrl;
    
    @Field(value="NORTH_EAST_LATITUDE")
    @JsonProperty(value="north_east_latitude")
    private float northEastLatitude;
    
    @Field(value="NORTH_EAST_LONGITUDE")
    @JsonProperty(value="north_east_longitude")
    private float northEastLongitude;
    
    @Field(value="SOUTH_WEST_LATITUDE")
    @JsonProperty(value="south_west_latitude")
    private float southWestLatitude;
    
    @Field(value="SOUTH_WEST_LONGITUDE")
    @JsonProperty(value="south_west_longitude")
    private float southWestLongitude;
    
    @Field(value="CENTER_LATITUDE")
    @JsonProperty(value="center_latitude")
    private float centerLatitude;
    
    @Field(value="CENTER_LONGITUDE")
    @JsonProperty(value="center_longitude")
    private float centerLongitude;
    
    @Field(value="LOCALITY_LABEL_PRIORITY")
    @JsonProperty(value="locality_label_priority")
    private String localityLabelPriority;
    
    @Field(value="BUILDER_LABEL_PRIORITY")
    @JsonProperty(value="builder_label_priority")
    private String builderLabelPriority;
    
    @Field(value="SUBURB_LABEL_PRIORITY")
    @JsonProperty(value="suburb_label_priority")
    private String suburbLabelPriority;
    
    @Field(value="PROJECT_STATUS_BEDROOM")
    @JsonProperty(value="project_status_bedroom")
    private String projectStatusBedroom;
    
    //private Project project;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName the unitName to set
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the typeId
     */
    public Long getTypeId() {
        return typeId;
    }

    /**
     * @return the bedrooms
     */
    public Long getBedrooms() {
        return bedrooms;
    }

    /**
     * @return the bathrooms
     */
    public long getBathrooms() {
        return bathrooms;
    }

    /**
     * @return the unitType
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * @return the projectType
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * @return the pricePerUnitArea
     */
    public Float getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    /**
     * @return the size
     */
    public Float getSize() {
        return size;
    }

    /**
     * @return the budget
     */
    public Float getBudget() {
        return budget;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the propertyUrl
     */
    public String getPropertyUrl() {
        return propertyUrl;
    }

    /**
     * @return the northEastLatitude
     */
    public Float getNorthEastLatitude() {
        return northEastLatitude;
    }

    /**
     * @return the northEastLongitude
     */
    public Float getNorthEastLongitude() {
        return northEastLongitude;
    }

    /**
     * @return the southWestLatitude
     */
    public Float getSouthWestLatitude() {
        return southWestLatitude;
    }

    /**
     * @return the southWestLongitude
     */
    public Float getSouthWestLongitude() {
        return southWestLongitude;
    }

    /**
     * @return the centerLatitude
     */
    public Float getCenterLatitude() {
        return centerLatitude;
    }

    /**
     * @return the centerLongitude
     */
    public Float getCenterLongitude() {
        return centerLongitude;
    }

    /**
     * @return the localityLabelPriority
     */
    public String getLocalityLabelPriority() {
        return localityLabelPriority;
    }

    /**
     * @return the builderLabelPriority
     */
    public String getBuilderLabelPriority() {
        return builderLabelPriority;
    }

    /**
     * @return the suburbLabelPriority
     */
    public String getSuburbLabelPriority() {
        return suburbLabelPriority;
    }

    /**
     * @return the projectStatusbedroom
     */
    public String getProjectStatusbedroom() {
        return projectStatusBedroom;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    /**
     * @param bedrooms the bedrooms to set
     */
    public void setBedrooms(long bedrooms) {
        this.bedrooms = bedrooms;
    }

    /**
     * @param bathrooms the bathrooms to set
     */
    public void setBathrooms(long bathrooms) {
        this.bathrooms = bathrooms;
    }

    /**
     * @param unitType the unitType to set
     */
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    /**
     * @param projectType the projectType to set
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /**
     * @param pricePerUnitArea the pricePerUnitArea to set
     */
    public void setPricePerUnitArea(float pricePerUnitArea) {
        this.pricePerUnitArea = pricePerUnitArea;
    }

    /**
     * @param size the size to set
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * @param budget the budget to set
     */
    public void setBudget(float budget) {
        this.budget = budget;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @param propertyUrl the propertyUrl to set
     */
    public void setPropertyUrl(String propertyUrl) {
        this.propertyUrl = propertyUrl;
    }

    /**
     * @param northEastLatitude the northEastLatitude to set
     */
    public void setNorthEastLatitude(float northEastLatitude) {
        this.northEastLatitude = northEastLatitude;
    }

    /**
     * @param northEastLongitude the northEastLongitude to set
     */
    public void setNorthEastLongitude(float northEastLongitude) {
        this.northEastLongitude = northEastLongitude;
    }

    /**
     * @param southWestLatitude the southWestLatitude to set
     */
    public void setSouthWestLatitude(float southWestLatitude) {
        this.southWestLatitude = southWestLatitude;
    }

    /**
     * @param southWestLongitude the southWestLongitude to set
     */
    public void setSouthWestLongitude(float southWestLongitude) {
        this.southWestLongitude = southWestLongitude;
    }

    /**
     * @param centerLatitude the centerLatitude to set
     */
    public void setCenterLatitude(float centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    /**
     * @param centerLongitude the centerLongitude to set
     */
    public void setCenterLongitude(float centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    /**
     * @param localityLabelPriority the localityLabelPriority to set
     */
    public void setLocalityLabelPriority(String localityLabelPriority) {
        this.localityLabelPriority = localityLabelPriority;
    }

    /**
     * @param builderLabelPriority the builderLabelPriority to set
     */
    public void setBuilderLabelPriority(String builderLabelPriority) {
        this.builderLabelPriority = builderLabelPriority;
    }

    /**
     * @param projectStatusbedroom the projectStatusbedroom to set
     */
    public void setProjectStatusbedroom(String projectStatusbedroom) {
        this.projectStatusBedroom = projectStatusbedroom;
    }

    
}
