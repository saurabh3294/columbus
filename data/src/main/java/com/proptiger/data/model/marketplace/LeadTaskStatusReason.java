package com.proptiger.data.model.marketplace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_lead_task_status_reasons")
public class LeadTaskStatusReason extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "lead_task_status_mapping_id")
    private int               taskStatusMappingId;

    private String            reason;

    @Column(name = "display_reason")
    private String            displayReason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskStatusMappingId() {
        return taskStatusMappingId;
    }

    public void setTaskStatusMappingId(int taskStatusMappingId) {
        this.taskStatusMappingId = taskStatusMappingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDisplayReason() {
        return displayReason;
    }

    public void setDisplayReason(String displayReason) {
        this.displayReason = displayReason;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}