/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name = "NEAR_PLACE_TYPES")
@ResourceMetaInfo(name = "Locality Amenity Types")
public class LocalityAmenityTypes {
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
    
    @OneToMany(mappedBy = "localityAmenityTypes", targetEntity = LocalityAmenity.class)
    private Set<LocalityAmenity> localityAmenity = new HashSet<LocalityAmenity>();
    
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

    public Set<LocalityAmenity> getLocalityAmenity() {
        return localityAmenity;
    }

    public void setLocalityAmenity(Set<LocalityAmenity> localityAmenity) {
        this.localityAmenity = localityAmenity;
    }
}
