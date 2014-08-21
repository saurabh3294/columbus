package com.proptiger.data.external.dto;

import java.util.Date;

/**
 * DTO for convenient update of lead tasks
 * 
 * @author azi
 * 
 */
public class LeadTaskDto {
    private int         id;
    private int         leadOfferId;
    private String      taskStatus;
    private String      taskName;
    private Date        scheduledFor;
    private Integer     callDuration;
    private Date        performedAt;
    private String      notes;

    private LeadTaskDto nextTask;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadOfferId() {
        return leadOfferId;
    }

    public void setLeadOfferId(int leadOfferId) {
        this.leadOfferId = leadOfferId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public LeadTaskDto getNextTask() {
        return nextTask;
    }

    public void setNextTask(LeadTaskDto nextTask) {
        this.nextTask = nextTask;
    }
}