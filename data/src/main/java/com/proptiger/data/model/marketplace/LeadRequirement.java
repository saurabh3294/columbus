package com.proptiger.data.model.marketplace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;

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
    private int               bedroom;

    @Column(name = "project_id")
    private Integer           projectId;

    @Column(name = "locality_id")
    private Integer           localityId;

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

    public int getBedroom() {
        return bedroom;
    }

    public void setBedroom(int bedroom) {
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
}
