package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class Property {
    @Field(value="id")
    @JsonProperty(value="ID")
    private String id;
    
    @Field(value="TYPE_ID")
    @JsonProperty(value="TYPE_ID")
    private Long typeId;
    
    @Field(value="BEDROOMS")
    @JsonProperty(value="BEDROOMS")
    private Long bedrooms;
    
    @Field(value="BATHROOMS")
    @JsonProperty(value="BATHROOMS")
    private long bathrooms;
    
    @Field(value="UNIT_TYPE")
    @JsonProperty(value="UNIT_TYPE")
    private String unitType;
    
    @Field(value="UNIT_NAME")
    @JsonProperty(value="UNIT_NAME")
    private String unitName;
    
    @Field(value="PROJECT_TYPE")
    @JsonProperty(value="PROJECT_TYPE")
    private String projectType;
    
    @Field(value="PRICE_PER_UNIT_AREA")
    @JsonProperty(value="PRICE_PER_UNIT_AREA")
    private Float pricePerUnitArea;
    
    @Field(value="SIZE")
    @JsonProperty(value="SIZE")
    private Float size;
    
    @Field(value="BUDGET")
    @JsonProperty(value="BUDGET")
    private Float budget;
    
    @Field(value="PRICE")
    @JsonProperty(value="PRICE")
    private String price;
    
    @Field(value="PROPERTY_URL")
    @JsonProperty(value="PROPERTY_URL")
    private String propertyUrl;
    
    @Field(value="NORTH_EAST_LATITUDE")
    @JsonProperty(value="NORTH_EAST_LATITUDE")
    private Float northEastLatitude;
    
    @Field(value="NORTH_EAST_LONGITUDE")
    @JsonProperty(value="NORTH_EAST_LONGITUDE")
    private Float northEastLongitude;
    
    @Field(value="SOUTH_WEST_LATITUDE")
    @JsonProperty(value="SOUTH_WEST_LATITUDE")
    private Float southWestLatitude;
    
    @Field(value="SOUTH_WEST_LONGITUDE")
    @JsonProperty(value="SOUTH_WEST_LONGITUDE")
    private Float southWestLongitude;
    
    @Field(value="CENTER_LATITUDE")
    @JsonProperty(value="CENTER_LATITUDE")
    private Float centerLatitude;
    
    @Field(value="CENTER_LONGITUDE")
    @JsonProperty(value="CENTER_LONGITUDE")
    private Float centerLongitude;
    
    @Field(value="LOCALITY_LABEL_PRIORITY")
    @JsonProperty(value="LOCALITY_LABEL_PRIORITY")
    private String localityLabelPriority;
    
    @Field(value="BUILDER_LABEL_PRIORITY")
    @JsonProperty(value="BUILDER_LABEL_PRIORITY")
    private String builderLabelPriority;
    
    @Field(value="SUBURB_LABEL_PRIORITY")
    @JsonProperty(value="SUBURB_LABEL_PRIORITY")
    private String suburbLabelPriority;
    
    @Field(value="PROJECT_STATUS_BEDROOM")
    @JsonProperty(value="PROJECT_STATUS_BEDROOM")
    private String projectStatusbedroom;
    
    
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
        return projectStatusbedroom;
    }
}
