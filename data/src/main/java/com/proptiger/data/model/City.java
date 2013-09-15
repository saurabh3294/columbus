package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name = "CITY")
@ResourceMetaInfo(name = "City")
public class City implements BaseModel {
    @Id
    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    private int id;

    @FieldMetaInfo( displayName = "Label",  description = "City label")
    @Column(name = "LABEL")
    private String label;

    @FieldMetaInfo( displayName = "North east latitude",  description = "North east latitude")
    @Field(value="NORTH_EAST_LATITUDE")
    private Double northEastLatitude;
    
    @FieldMetaInfo( displayName = "North east longitude",  description = "North east longitude")
    @Field(value="NORTH_EAST_LONGITUDE")
    private Double northEastLongitude;
    
    @FieldMetaInfo( displayName = "South west latitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LATITUDE")
    private Double southWestLatitude;
    
    @FieldMetaInfo( displayName = "South west longitude",  description = "South west latitude")
    @Field(value="SOUTH_WEST_LONGITUDE")
    private Double southWestLongitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LATITUDE")
    private Double centerLatitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Field(value="CENTER_LONGITUDE")
    private Double centerLongitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getNorthEastLatitude() {
        return northEastLatitude;
    }

    public void setNorthEastLatitude(Double northEastLatitude) {
        this.northEastLatitude = northEastLatitude;
    }

    public Double getNorthEastLongitude() {
        return northEastLongitude;
    }

    public void setNorthEastLongitude(Double northEastLongitude) {
        this.northEastLongitude = northEastLongitude;
    }

    public Double getSouthWestLatitude() {
        return southWestLatitude;
    }

    public void setSouthWestLatitude(Double southWestLatitude) {
        this.southWestLatitude = southWestLatitude;
    }

    public Double getSouthWestLongitude() {
        return southWestLongitude;
    }

    public void setSouthWestLongitude(Double southWestLongitude) {
        this.southWestLongitude = southWestLongitude;
    }

    public Double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public Double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }
}
