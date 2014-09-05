package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.enums.TaskStatus;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_lead_task_statuses")
public class MasterLeadTaskStatus extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Enumerated(EnumType.STRING)
    private TaskStatus        status;

    @Column(name = "display_status")
    private String            displayStatus;

    @Column(name = "is_next_task_required")
    private boolean           nextTaskRequired;

    @Column(name = "is_beginning")
    private boolean           beginning;

    @Column(name = "is_complete")
    private boolean           complete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public boolean isNextTaskRequired() {
        return nextTaskRequired;
    }

    public void setNextTaskRequired(boolean nextTaskRequired) {
        this.nextTaskRequired = nextTaskRequired;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isBeginning() {
        return beginning;
    }

    public void setBeginning(boolean beginning) {
        this.beginning = beginning;
    }
}