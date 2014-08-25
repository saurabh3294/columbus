package com.proptiger.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.proptiger.data.model.marketplace.LeadTaskStatusReason;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_lead_task_status_mappings")
public class LeadTaskStatus extends BaseModel {
    private static final long          serialVersionUID = 1L;

    @Id
    private int                        id;

    @Column(name = "master_task_id")
    private int                        masterTaskId;

    @Column(name = "master_task_status_id")
    private int                        masterTaskStatusId;

    @Column(name = "master_status_id")
    private Integer                    resultingStatusId;

    @ManyToOne
    @JoinColumn(name = "master_task_id", insertable = false, updatable = false)
    private MasterLeadTask             masterLeadTask;

    @ManyToOne
    @JoinColumn(name = "master_task_status_id", insertable = false, updatable = false)
    private MasterLeadTaskStatus       masterLeadTaskStatus;

    @ManyToOne
    @JoinColumn(name = "master_status_id", insertable = false, updatable = false)
    private MasterLeadOfferStatus      resultingStatus;

    @OneToMany(mappedBy = "taskStatusMappingId")
    private List<LeadTaskStatusReason> statusReasons;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMasterTaskId() {
        return masterTaskId;
    }

    public void setMasterTaskId(int masterTaskId) {
        this.masterTaskId = masterTaskId;
    }

    public int getMasterTaskStatusId() {
        return masterTaskStatusId;
    }

    public void setMasterTaskStatusId(int masterTaskStatusId) {
        this.masterTaskStatusId = masterTaskStatusId;
    }

    public Integer getResultingStatusId() {
        return resultingStatusId;
    }

    public void setResultingStatusId(Integer resultingStatusId) {
        this.resultingStatusId = resultingStatusId;
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

    public MasterLeadOfferStatus getResultingStatus() {
        return resultingStatus;
    }

    public void setResultingStatus(MasterLeadOfferStatus resultingStatus) {
        this.resultingStatus = resultingStatus;
    }

    public List<LeadTaskStatusReason> getStatusReasons() {
        return statusReasons;
    }

    public void setStatusReasons(List<LeadTaskStatusReason> statusReasons) {
        this.statusReasons = statusReasons;
    }
}