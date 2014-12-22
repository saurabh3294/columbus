package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "notification.event_type")
public class EventType extends BaseModel {

    private static final long serialVersionUID = -7894812665751049177L;

    public enum Strategy {
        MERGE, SUPPRESS, NO_STRATEGY
    }

    public enum HoldingPeriodType {
        SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, UPCOMING_MONTHS
    }

    @Id
    @Column(name = "id")
    private int                       id;

    @Column(name = "name")
    private String                    name;

    @Column(name = "strategy")
    @Enumerated(EnumType.STRING)
    private Strategy                  strategy;

    @Column(name = "holding_period_type")
    @Enumerated(EnumType.STRING)
    private HoldingPeriodType         holdingPeriodType;

    @Column(name = "holding_period_value")
    private int                       holdingPeriodValue;

    @Column(name = "verification_required")
    private Boolean                   verficationRequired;

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

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public HoldingPeriodType getHoldingPeriodType() {
        return holdingPeriodType;
    }

    public void setHoldingPeriodType(HoldingPeriodType holdingPeriodType) {
        this.holdingPeriodType = holdingPeriodType;
    }

    public int getHoldingPeriodValue() {
        return holdingPeriodValue;
    }

    public void setHoldingPeriodValue(int holdingPeriodValue) {
        this.holdingPeriodValue = holdingPeriodValue;
    }

    public Boolean getVerficationRequired() {
        return verficationRequired;
    }

    public void setVerficationRequired(Boolean verficationRequired) {
        this.verficationRequired = verficationRequired;
    }

}
