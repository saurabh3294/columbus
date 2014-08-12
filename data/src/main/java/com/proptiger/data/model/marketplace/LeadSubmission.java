package com.proptiger.data.model.marketplace;

import java.util.Date;

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
@Entity(name = "lead_submissions")
@Table(name = "marketplace.lead_submissions")
@JsonFilter("fieldFilter")
public class LeadSubmission extends BaseModel{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2823322053032851864L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    
    @Column(name = "lead_id")
    private int leadId;
    
    @Column(name = "source_id")
    private int sourceId;
    
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "lead_requirement_id")
    private int leadRequirementId;
    
    public int getLeadRequirementId() {
        return leadRequirementId;
    }

    public void setLeadRequirementId(int leadRequirementId) {
        this.leadRequirementId = leadRequirementId;
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

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
