/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name = "NEAR_LOCALITY")
@ResourceMetaInfo
public class NearLocalities extends BaseModel{
    @Id
    @FieldMetaInfo( displayName = "Id",  description = "Id")
    @Column(name = "id")
    private int id;
    
    @FieldMetaInfo( displayName = "Main Locality Id",  description = "Main Locality Id")
    @Column(name = "MAIN_LOCALITY")
    private Integer mainLocality;
    
    @FieldMetaInfo( displayName = "Near Locality Id",  description = "Near Locality Id")
    @Column(name = "NEAR_LOCALITY")
    private int nearLocality;
    
    @FieldMetaInfo( displayName = "Distance",  description = "Distance")
    @Column(name = "DISTANCE")
    private int distance;

    public int getMainLocality() {
        return mainLocality;
    }

    public void setMainLocality(int mainLocality) {
        this.mainLocality = mainLocality;
    }

    public int getNearLocality() {
        return nearLocality;
    }

    public void setNearLocality(int nearLocality) {
        this.nearLocality = nearLocality;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
