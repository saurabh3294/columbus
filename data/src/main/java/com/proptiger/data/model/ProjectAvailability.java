package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model for project availabilities table
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "cms.project_availabilities")
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class ProjectAvailability extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer           id;

    @Column(name = "project_supply_id")
    private Integer           projectSupplyId;

    @Column(name = "effective_month")
    private Date              effectiveMonth;

    @Column(name = "availability")
    private Integer           inventory;

    @Column(name = "updated_by")
    private Integer           updatedBy;

    private String            comment;

    @Column(name = "created_at")
    private Date              createdAt;

    @Column(name = "updated_at")
    private Date              updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectSupplyId() {
        return projectSupplyId;
    }

    public void setProjectSupplyId(Integer projectSupplyId) {
        this.projectSupplyId = projectSupplyId;
    }

    public Date getEffectiveMonth() {
        return effectiveMonth;
    }

    public void setEffectiveMonth(Date effectiveMonth) {
        this.effectiveMonth = effectiveMonth;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}