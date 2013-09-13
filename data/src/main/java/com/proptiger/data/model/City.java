package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name = "CITY")
@ResourceMetaInfo(name = "City")
public class City {
    @FieldMetaInfo(name = "cityId", displayName = "City Id", dataType = DataType.LONG, description = "City Id")
    @Column(name = "CITY_ID")
    @Id
    private long id;

    @FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "City label")
    @Column(name = "LABEL")
    private String label;

    @FieldMetaInfo(name = "northEastLatitude", displayName = "North east latitude", dataType = DataType.FLOAT, description = "North east latitude")
    @Field(value="NORTH_EAST_LATITUDE")
    @JsonProperty
    private float northEastLatitude;
    
    @FieldMetaInfo(name = "northEastLongitude", displayName = "North east longitude", dataType = DataType.FLOAT, description = "North east longitude")
    @Field(value="NORTH_EAST_LONGITUDE")
    @JsonProperty
    private float northEastLongitude;
    
    @FieldMetaInfo(name = "southWestLatitude", displayName = "South west latitude", dataType = DataType.FLOAT, description = "South west latitude")
    @Field(value="SOUTH_WEST_LATITUDE")
    @JsonProperty
    private float southWestLatitude;
    
    @FieldMetaInfo(name = "southWestLongitude", displayName = "South west longitude", dataType = DataType.FLOAT, description = "South west latitude")
    @Field(value="SOUTH_WEST_LONGITUDE")
    @JsonProperty
    private float southWestLongitude;
    
    @FieldMetaInfo(name = "centerLatitude", displayName = "Center latitude", dataType = DataType.FLOAT, description = "Center latitude")
    @Field(value="CENTER_LATITUDE")
    @JsonProperty
    private float centerLatitude;
    
    @FieldMetaInfo(name = "centerLatitude", displayName = "Center latitude", dataType = DataType.FLOAT, description = "Center latitude")
    @Field(value="CENTER_LONGITUDE")
    @JsonProperty
    private float centerLongitude;

    public long getId() {
        return id;
    }

    public void setId(long cityId) {
        this.id = cityId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getNorthEastLatitude() {
        return northEastLatitude;
    }

    public void setNorthEastLatitude(float northEastLatitude) {
        this.northEastLatitude = northEastLatitude;
    }

    public float getNorthEastLongitude() {
        return northEastLongitude;
    }

    public void setNorthEastLongitude(float northEastLongitude) {
        this.northEastLongitude = northEastLongitude;
    }

    public float getSouthWestLatitude() {
        return southWestLatitude;
    }

    public void setSouthWestLatitude(float southWestLatitude) {
        this.southWestLatitude = southWestLatitude;
    }

    public float getSouthWestLongitude() {
        return southWestLongitude;
    }

    public void setSouthWestLongitude(float southWestLongitude) {
        this.southWestLongitude = southWestLongitude;
    }

    public float getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(float centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public float getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(float centerLongitude) {
        this.centerLongitude = centerLongitude;
    }
}
