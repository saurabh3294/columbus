package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import javax.persistence.Transient;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "CITY")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class City{
    @Id
    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    private Integer id;

    @FieldMetaInfo( displayName = "Label",  description = "City label")
    @Column(name = "LABEL")
    private String label;

    @FieldMetaInfo( displayName = "North east latitude",  description = "North east latitude")
    @Column(name="NORTH_EAST_LATITUDE")
    @Field(value="NORTH_EAST_LATITUDE")
    private Double northEastLatitude;
    
    @FieldMetaInfo( displayName = "North east longitude",  description = "North east longitude")
    @Column(name="NORTH_EAST_LONGITUDE")
    @Field(value="NORTH_EAST_LONGITUDE")
    private Double northEastLongitude;
    
    @FieldMetaInfo( displayName = "South west latitude",  description = "South west latitude")
    @Column(name="SOUTH_WEST_LATITUDE")
    @Field(value="SOUTH_WEST_LATITUDE")
    private Double southWestLatitude;
    
    @FieldMetaInfo( displayName = "South west longitude",  description = "South west latitude")
    @Column(name="SOUTH_WEST_LONGITUDE")
    @Field(value="SOUTH_WEST_LONGITUDE")
    private Double southWestLongitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Column(name="CENTER_LATITUDE")
    @Field(value="CENTER_LATITUDE")
    private Double centerLatitude;
    
    @FieldMetaInfo( displayName = "Center latitude",  description = "Center latitude")
    @Column(name="CENTER_LONGITUDE")
    @Field(value="CENTER_LONGITUDE")
    private Double centerLongitude;

    @Column(name="DISPLAY_PRIORITY")
    @Field(value="DISPLAY_PRIORITY")
    @FieldMetaInfo( displayName = "Display Priority",  description = "Display Priority")
    @Transient
    private Integer displayPriority;
    
    @Column(name="DISPLAY_ORDER")
    @Field(value="DISPLAY_ORDER")
    @FieldMetaInfo( displayName = "Display Order",  description = "Display Order")
    private Integer displayOrder;
    
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

	public Integer getDisplayPriority() {
		return displayPriority;
	}

	public void setDisplayPriority(Integer displayPriority) {
		this.displayPriority = displayPriority;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
    
}
