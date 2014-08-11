package com.proptiger.data.model.marketplace;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.enums.LeadTaskStatus;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "marketplace.lead_tasks")
@JsonFilter("fieldFilter")
public class LeadTask extends BaseModel{
    private static final long serialVersionUID = -5139446103498473442L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "lead_offer_id")
    private Integer leadOfferId;
    
    @Column(name = "task_id")
    private Integer taskId;
    
    @Column(name = "lead_status_id")
    private Integer leadStatusId;
    
    @Column(name = "scheduled_at")
    private Date scheduledAt;
    
    @Column(name = "call_time_seconds")
    private Integer callTimeSeconds;
    
    @Column(name = "performed_at")
    private Date performedAt;
    
    @Column(name = "status")
    private LeadTaskStatus status;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeadOfferId() {
        return leadOfferId;
    }

    public void setLeadOfferId(Integer leadOfferId) {
        this.leadOfferId = leadOfferId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getLeadStatusId() {
        return leadStatusId;
    }

    public void setLeadStatusId(Integer leadStatusId) {
        this.leadStatusId = leadStatusId;
    }

    public Date getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getCallTimeSeconds() {
        return callTimeSeconds;
    }

    public void setCallTimeSeconds(Integer callTimeSeconds) {
        this.callTimeSeconds = callTimeSeconds;
    }

    public Date getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Date performedAt) {
        this.performedAt = performedAt;
    }

    public LeadTaskStatus getStatus() {
        return status;
    }

    public void setStatus(LeadTaskStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

}
