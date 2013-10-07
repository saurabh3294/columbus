/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name = "NEAR_PLACE_TYPES")
@ResourceMetaInfo(name = "Locality Amenity Types")
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalityAmenityTypes implements BaseModel{
    @FieldMetaInfo(displayName = "Id", description = "Id")
    @Column(name="id")
    @Id 
    private int id;
    
    @FieldMetaInfo(displayName = "Name", description = "Amenity Name")
    @Column(name="name")
    private String name;
    
    @FieldMetaInfo(displayName = "Display Name", description = "Amenity Display Name")
    @Column(name="display_name")
    private String displayName;
    
    //@OneToMany(mappedBy = "localityAmenityTypes", targetEntity = LocalityAmenity.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    //private Set<LocalityAmenity> localityAmenity = new HashSet<LocalityAmenity>();
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*public Set<LocalityAmenity> getLocalityAmenity() {
        return localityAmenity;
    }

    public void setLocalityAmenity(Set<LocalityAmenity> localityAmenity) {
        this.localityAmenity = localityAmenity;
    }*/
}
