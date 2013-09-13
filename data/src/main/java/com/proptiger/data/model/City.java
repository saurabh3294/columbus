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
    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    @Id
    private long id;

    @FieldMetaInfo( displayName = "Label",  description = "City label")
    @Column(name = "LABEL")
    private String label;

    @FieldMetaInfo( displayName = "North east latitude",  description = "North east latitude")
    @Field(value="NORTH_EAST_LATITUDE")
    private float northEastLatitude;
    
    @FieldMetaInfo( displayName = "North east longitude",  description = "North east longitude")
    @Field(value="NORTH_EAST_LONGITUDE")
    private float northEastLongitude;
    
    @FieldMetaInfo( displayName = "South west latitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LATITUDE")
    private float southWestLatitude;
    
    @FieldMetaInfo( displayName = "South west longitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LONGITUDE")
    private float southWestLongitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LATITUDE")
    private float centerLatitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LONGITUDE")
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
