package com.proptiger.data.model.marketplace;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.core.annotations.ExcludeFromBeanCopy;
import com.proptiger.core.model.BaseModel;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.MasterLeadTask;
import com.proptiger.data.model.MasterLeadTaskStatus;

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
    @JsonIgnore
    private int                             taskStatusId;

    @Column(name = "task_status_reason_id")
    @JsonIgnore
    private Integer                         statusReasonId;

    @Nonnull
    @Column(name = "scheduled_for")
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

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_offer_id", insertable = false, updatable = false)
    private LeadOffer                       leadOffer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "lead_task_status_id", insertable = false, updatable = false)
    private LeadTaskStatus                  taskStatus;

    @OneToMany(mappedBy = "taskId")
    private List<TaskOfferedListingMapping> offeredListingMappings;

    @Transient
    private MasterLeadTask                  masterLeadTask;

    @Transient
    private MasterLeadTaskStatus            masterLeadTaskStatus;

    @ManyToOne(optional = true)
    @JoinColumn(name = "task_status_reason_id", insertable = false, updatable = false)
    private LeadTaskStatusReason            statusReason;

    // XXX will also return notification for other objects with the same id
    @OneToMany(mappedBy = "objectId")
    @JsonIgnore
    private List<Notification>              notifications;

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

    public Integer getStatusReasonId() {
        return statusReasonId;
    }

    public void setStatusReasonId(Integer statusReasonId) {
        this.statusReasonId = statusReasonId;
    }

    public List<TaskOfferedListingMapping> getOfferedListingMappings() {
        return offeredListingMappings;
    }

    public void setOfferedListingMappings(List<TaskOfferedListingMapping> offeredListingMappings) {
        this.offeredListingMappings = offeredListingMappings;
    }

    public int getTaskStatusId() {
        return taskStatusId;
    }

    public MasterLeadTask getMasterLeadTask() {
        return masterLeadTask;
    }

    public void setMasterLeadTask(MasterLeadTask masterLeadTask) {
        this.masterLeadTask = masterLeadTask;
    }

    public MasterLeadTaskStatus getMasterLeadTaskStatus() {
        return masterLeadTaskStatus;
    }

    public void setMasterLeadTaskStatus(MasterLeadTaskStatus masterLeadTaskStatus) {
        this.masterLeadTaskStatus = masterLeadTaskStatus;
    }

    public LeadTaskStatusReason getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(LeadTaskStatusReason statusReason) {
        this.statusReason = statusReason;
    }

    public LeadTask populateTransientAttributes() {
        if (taskStatus != null) {
            if (taskStatus.getMasterLeadTask() != null) {
                masterLeadTask = taskStatus.getMasterLeadTask();
            }
            if (taskStatus.getMasterLeadTaskStatus() != null) {
                masterLeadTaskStatus = taskStatus.getMasterLeadTaskStatus();
            }
        }
        if (nextTask != null) {
            nextTask.populateTransientAttributes();
        }
        return this;
    }

    public static List<LeadTask> populateTransientAttributes(List<LeadTask> leadTasks) {
        for (LeadTask leadTask : leadTasks) {
            leadTask.populateTransientAttributes();
        }
        return leadTasks;
    }

    // XXX deprecated because also returns notification for other objects with
    // the same id
    @Deprecated
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * unlinks the circular loop present b/w masterLeadTasks and leadTaskStatus
     * 
     * @param leadTasks
     * @return
     */
    public static List<LeadTask> unlinkCircularLoop(List<LeadTask> leadTasks) {
        for (LeadTask leadTask : leadTasks) {
            leadTask.unlinkCircularLoop();
        }
        return leadTasks;
    }

    /**
     * unlinks the circular loop present b/w masterLeadTasks and leadTaskStatus
     * 
     * @return
     */
    public LeadTask unlinkCircularLoop() {
        // putting in try-catch since getter can't be called in case
        // connection is not available.. but circular loop will not be there
        // in that case
        try {
            if (taskStatus != null) {
                if (taskStatus.getMasterLeadTask() != null) {
                    taskStatus.getMasterLeadTask().setLeadTaskStatuses(null);
                }
            }

            if (leadOffer != null) {
                leadOffer.setNextTask(null);
                leadOffer.setLastTask(null);
            }
        }
        catch (Exception e) {
        }
        if (nextTask != null) {
            nextTask.unlinkCircularLoop();
        }

        return this;
    }
}