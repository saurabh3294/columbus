package com.proptiger.data.model.marketplace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "marketplace.lead_requirements")
@JsonFilter("fieldFilter")
public class LeadRequirement extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 5133688579363136088L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int               id;

    @Column(name = "lead_id")
    private int               leadId;

    @Column(name = "bedroom")
    private Integer           bedroom;

    @Column(name = "project_id")
    private Integer           projectId;

    @Column(name = "locality_id")
    private Integer           localityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id", nullable = true, insertable = false, updatable = false)    
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true, insertable = false, updatable = false)    
    private Project project;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="lead_id" ,insertable=false, updatable=false)
    private Lead lead;

    @PrePersist
    public void validate() {
        if (localityId == null && projectId == null) {
            throw new IllegalArgumentException("At least one of locality or project is mandatory");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public Integer getBedroom() {
        return bedroom;
    }

    public void setBedroom(Integer bedroom) {
        this.bedroom = bedroom;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
