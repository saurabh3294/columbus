package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.listing_amenities")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class ListingAmenity extends BaseModel {
    private static final long serialVersionUID = 1867361002041943214L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @Column(name = "listing_id")
    private Integer           listingId;

    @Column(name = "project_amenity_id")
    private Integer           projectAmenityId;

    @Column(name = "created_at")
    private Date              createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public Integer getProjectAmenityId() {
        return projectAmenityId;
    }

    public void setProjectAmenityId(Integer projectAmenityId) {
        this.projectAmenityId = projectAmenityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
    }
    
}
