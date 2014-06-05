package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.enums.DataVersion;

/**
 * Model for project supplies
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "cms.project_supplies")
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class ProjectSupply extends BaseModel {
    private static final long         serialVersionUID = 1L;

    @Id
    private Integer                   id;

    @Column(name = "listing_id")
    private Integer                   listingId;

    @Enumerated(EnumType.STRING)
    private DataVersion               version;

    private Integer                   supply;

    @Column(name = "launched")
    private Integer                   launchedUnit;

    private String                    comment;

    @Column(name = "updated_by")
    private Integer                   updatedBy;

    @Column(name = "created_at")
    private Date                      createdAt;

    @Column(name = "updated_at")
    private Date                      updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectSupplyId", cascade = CascadeType.ALL)
    private List<ProjectAvailability> availabilities;

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

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }

    public Integer getSupply() {
        return supply;
    }

    public void setSupply(Integer supply) {
        this.supply = supply;
    }

    public Integer getLaunchedUnit() {
        return launchedUnit;
    }

    public void setLaunchedUnit(Integer launchedUnit) {
        this.launchedUnit = launchedUnit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProjectAvailability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<ProjectAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}