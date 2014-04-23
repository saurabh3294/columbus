package com.proptiger.data.model;

import java.util.ArrayList;
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
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.b2b.STATUS;
import com.proptiger.data.model.enums.DataVersion;
import com.proptiger.data.model.enums.EntityType;

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@JsonFilter("fieldFilter")
@Table(name = "cms.resi_project_phase")
public class ProjectPhase extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PHASE_ID")
    private Integer           phaseId;

    @Enumerated(EnumType.STRING)
    private DataVersion       version;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHASE_TYPE")
    private EntityType        phaseType;

    @Column(name = "PROJECT_ID")
    private Integer           projectId;

    @Column(name = "PHASE_NAME")
    private String            phaseName;

    @Column(name = "LAUNCH_DATE")
    private Date              launchDate;

    @Column(name = "COMPLETION_DATE")
    private Date              completionDate;

    @Column(name = "REMARKS")
    private String            remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private STATUS            status;

    @Column(name = "updated_by")
    private Integer           updatedBy;

    @Column(name = "created_at")
    private Date              createdAt;

    @Column(name = "updated_at")
    private Date              updatedAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "phaseId", cascade = CascadeType.ALL)
    private List<Listing>     listings         = new ArrayList<>();

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
        this.phaseId = phaseId;
    }

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }

    public EntityType getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(EntityType phaseType) {
        this.phaseType = phaseType;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
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

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }
}