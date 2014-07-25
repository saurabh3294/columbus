package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.event.payload.DefaultEventTypePayload;

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
    // TODO remove the Types ENUM. make it dynamic.
    public enum Types {
        PortfolioPriceChange("portfolio_price_change", DefaultEventTypePayload.class), PortfolioPhotoAdded(
                "portfolio_photo_added", DefaultEventTypePayload.class);

        private String   name;
        private Class<?> dataClassName;

        Types(String name, Class<?> dataClassName) {
            this.name = name;
            this.dataClassName = dataClassName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?> getDataClassName() {
            return dataClassName;
        }

        public void setDataClassName(Class<?> dataClassName) {
            this.dataClassName = dataClassName;
        }
    }

    public enum Operation {
        Replace, Merge;
    }

    @Id
    @Column(name = "id")
    private int       id;

    @Column(name = "name")
    private Types     name;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Types getName() {
        return name;
    }

    public void setName(Types name) {
        this.name = name;
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
}
