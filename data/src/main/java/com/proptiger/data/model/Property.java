package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class Property {
    private String id;
    private Long typeId;
    private Long bedrooms;
    private long bathrooms;
    private String unitType;
    @Field(value="UNIT_NAME")
    @JsonProperty(value="UNIT_NAME")
    private String unitName;
    private String projectType;
    private Float pricePerUnitArea;
    private Float size;
    private Float budget;
    private String price;
    private String propertyUrl;
    private Float northEastLatitude;
    private Float northEastLongitude;
    private Float southWestLatitude;
    private Float southWestLongitude;
    private Float centerLatitude;
    private Float centerLongitude;
    private String localityLabelPriority;
    private String builderLabelPriority;
    private String suburbLabelPriority;
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
}
