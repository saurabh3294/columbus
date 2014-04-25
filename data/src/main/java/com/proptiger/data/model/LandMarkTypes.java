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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * 
 * @author mukand
 */
@Entity
@Table(name = "cms.landmark_types")
@ResourceMetaInfo
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandMarkTypes extends BaseModel {

    private static final long serialVersionUID = -5130354389286165685L;

    @FieldMetaInfo(displayName = "Id", description = "Id")
    @Column(name = "id")
    @Id
    private int               id;

    @FieldMetaInfo(displayName = "Name", description = "Amenity Name")
    @Column(name = "name")
    private String            name;

    @FieldMetaInfo(displayName = "Display Name", description = "Amenity Display Name")
    @Column(name = "display_name")
    @JsonIgnore
    private String            displayName;

    @FieldMetaInfo(displayName = "Description ", description = "Amenity Description")
    @Column(name = "description")
    private String            description;

    // @OneToMany(mappedBy = "localityAmenityTypes", targetEntity =
    // LocalityAmenity.class, fetch = FetchType.EAGER, cascade =
    // CascadeType.DETACH)
    // private Set<LocalityAmenity> localityAmenity = new
    // HashSet<LocalityAmenity>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * public Set<LocalityAmenity> getLocalityAmenity() { return
     * localityAmenity; }
     * 
     * public void setLocalityAmenity(Set<LocalityAmenity> localityAmenity) {
     * this.localityAmenity = localityAmenity; }
     */
}
