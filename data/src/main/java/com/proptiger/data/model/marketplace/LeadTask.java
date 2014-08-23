package com.proptiger.data.model.marketplace;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Future;

import com.proptiger.data.annotations.ExcludeFromBeanCopy;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.LeadTaskStatus;

/**
 * @author Rajeev Pandey
 * @author azi
 */
@Entity
@Table(name = "marketplace.lead_tasks")
public class LeadTask extends BaseModel {
    private static final long               serialVersionUID = -5139446103498473442L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer                         id;

    @ExcludeFromBeanCopy
    @Column(name = "lead_offer_id")
    private int                             leadOfferId;

    @Column(name = "lead_task_status_id")
    private int                             taskStatusId;

    @Nonnull
    @Column(name = "scheduled_for")
    @Future
    private Date                            scheduledFor;

    @Column(name = "call_time_seconds")
    private Integer                         callDuration;

    @Column(name = "performed_at")
    private Date                            performedAt;

    @Column(name = "notes")
    private String                          notes;

    @ExcludeFromBeanCopy
    @Column(name = "created_at")
    private Date                            createdAt;

    @ExcludeFromBeanCopy
    @Column(name = "updated_at")
    private Date                            updatedAt;

    @ManyToOne
    @JoinColumn(name = "lead_offer_id", insertable = false, updatable = false)
    private LeadOffer                       leadOffer;

    @ManyToOne
    @JoinColumn(name = "lead_task_status_id", insertable = false, updatable = false)
    private LeadTaskStatus                  taskStatus;

    @OneToMany(mappedBy = "taskId")
    private List<TaskOfferedListingMapping> offeredListingMappings;

    @Transient
    private LeadTask                        nextTask;

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getLeadOfferId() {
        return leadOfferId;
    }

    public void setLeadOfferId(int leadOfferId) {
        this.leadOfferId = leadOfferId;
    }

    public Date getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Date scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
    }

    public Date getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Date performedAt) {
        this.performedAt = performedAt;
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

    public LeadOffer getLeadOffer() {
        return leadOffer;
    }

    public void setLeadOffer(LeadOffer leadOffer) {
        this.leadOffer = leadOffer;
    }

    public LeadTask getNextTask() {
        return nextTask;
    }

    public void setNextTask(LeadTask nextTask) {
        this.nextTask = nextTask;
    }

    public void setTaskStatusId(int taskStatusId) {
        this.taskStatusId = taskStatusId;
    }

    public LeadTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(LeadTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskStatusId() {
        return taskStatusId;
    }
}