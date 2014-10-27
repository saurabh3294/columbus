package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "notification.event_type")
/**
 * TODO to make the configuration required for the event type in json format and then load it
 * into the  coressponding model for each event type. The model mapping will be present in the
 * Types Enum.
 * @author Mukand Agarwal
 *
 */
public class EventType extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = -7894812665751049177L;

    public enum Operation {
        Replace, Merge;
    }

    @Id
    @Column(name = "id")
    private int                       id;

    @Column(name = "name")
    private String                    name;

    @Column(name = "is_mergeable")
    private boolean                   isMergeable;

    @Column(name = "validation_cycle_in_hours")
    private int                       validationCycleHours;

    @Column(name = "verification_required")
    private int                       verficationRequired;

    @Column(name = "queued_items_in_validation_cycle")
    private int                       queuedItemsValidationCycle;

    @Column(name = "operation")
    private Operation                 operation;

    @Column(name = "overwrite_config_name")
    private String                    overwriteConfigName;

    @Transient
    @JsonIgnore
    private transient EventTypeConfig eventTypeConfig;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMergeable() {
        return isMergeable;
    }

    public void setMergeable(boolean isMergeable) {
        this.isMergeable = isMergeable;
    }

    public int getValidationCycleHours() {
        return validationCycleHours;
    }

    public void setValidationCycleHours(int validationCycleHours) {
        this.validationCycleHours = validationCycleHours;
    }

    public int getVerficationRequired() {
        return verficationRequired;
    }

    public void setVerficationRequired(int verficationRequired) {
        this.verficationRequired = verficationRequired;
    }

    public int getQueuedItemsValidationCycle() {
        return queuedItemsValidationCycle;
    }

    public void setQueuedItemsValidationCycle(int queuedItemsValidationCycle) {
        this.queuedItemsValidationCycle = queuedItemsValidationCycle;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public EventTypeConfig getEventTypeConfig() {
        return eventTypeConfig;
    }

    public void setEventTypeConfig(EventTypeConfig eventTypeConfig) {
        this.eventTypeConfig = eventTypeConfig;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverwriteConfigName() {
        return overwriteConfigName;
    }

    public void setOverwriteConfigName(String overwriteConfigName) {
        this.overwriteConfigName = overwriteConfigName;
    }

    public String getName() {
        return name;
    }

}
