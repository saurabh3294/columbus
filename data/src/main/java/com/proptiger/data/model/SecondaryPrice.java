package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.enums.UnitType;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "cms.project_secondary_price")
public class SecondaryPrice extends BaseModel {
    private static final long serialVersionUID = -1L;

    @Id
    @Column(name = "ID")
    private int               id;

    @Column(name = "PROJECT_ID")
    private int               projectId;

    @Column(name = "PHASE_ID")
    private int               phaseId;

    @Column(name = "UNIT_TYPE")
    @Enumerated(EnumType.STRING)
    private UnitType          unitType;

    @Column(name = "BROKER_ID")
    private Integer           brokerId;

    @Column(name = "MIN_PRICE")
    private int               minPrice;

    @Column(name = "MAX_PRICE")
    private int               maxPrice;

    @Column(name = "EFFECTIVE_DATE")
    private Date              effectiveDate;

    @Column(name = "LAST_MODIFIED_BY")
    private Integer           updatedBy;

    @Column(name = "LAST_MODIFIED_DATE")
    private Date              updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(int phaseId) {
        this.phaseId = phaseId;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}