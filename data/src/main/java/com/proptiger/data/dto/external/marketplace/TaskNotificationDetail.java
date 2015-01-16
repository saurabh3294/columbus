package com.proptiger.data.dto.external.marketplace;

import java.util.Date;

public class TaskNotificationDetail {
    private int    id;
    private Date   scheduledFor;
    private String clientName;
    private int    leadOfferId;
    private String taskName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Date scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getLeadOfferId() {
        return leadOfferId;
    }

    public void setLeadOfferId(int leadOfferId) {
        this.leadOfferId = leadOfferId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}