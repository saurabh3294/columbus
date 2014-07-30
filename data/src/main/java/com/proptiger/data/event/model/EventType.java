package com.proptiger.data.event.model;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.proptiger.data.event.enums.EventTypeConfig;

@Entity
@Table(name = "event_type")
/**
 * TODO to make the configuration required for the event type in json format and then load it
 * into the  coressponding model for each event type. The model mapping will be present in the
 * Types Enum.
 * @author Mukand Agarwal
 *
 */
public class EventType {
    public enum Operation {
        Replace, Merge;
    }

    static {
        //map = id => "{payr*CLassNAme = abdbad.class}"
    }
    
    @Id
    @Column(name = "id")
    private int       id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "is_mergeable")
    private boolean   isMergeable;

    @Column(name = "validation_cycle_in_hours")
    private int       validationCycleHours;

    @Column(name = "verification_required")
    private int       verficationRequired;

    @Column(name = "queued_items_in_validation_cycle")
    private int       queuedItemsValidationCycle;

    @Column(name = "operation")
    private Operation operation;
    
    @Column(name = "overwrite_config_name")
    private String overwriteConfigName;
    
    @Transient
    private EventTypeConfig     eventTypeConfig;
    
    @PostConstruct
    public void populateConfig(){
        String configName = this.name;
        if(this.overwriteConfigName != null){
            configName = this.overwriteConfigName;
        }
        EventTypeConfig savedEventTypeConfig = EventTypeConfig.eventTypeConfig.get(configName);
        // TODO to handle the case when there is no mapping of name in the config.
        // Code execution should not be stopped as a proper logging of error has to be done.
        if(eventTypeConfig == null){
            
        }
        this.eventTypeConfig = savedEventTypeConfig;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventTypeConfig getName() {
        return eventTypeConfig;
    }

    public void setName(EventTypeConfig name) {
        this.eventTypeConfig = name;
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
}
