package com.proptiger.data.event.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.data.event.legacy.Event;
import com.proptiger.data.event.model.payload.EventTypePayload;

@Entity
@Table(name = "event_generated")
public class EventGenerated extends Event {

    /**
     * 
     */
    private static final long serialVersionUID = 778194433417706629L;

    public enum EventStatus {
        Raw, Merged, Discarded, Verfied, PendingVerification, Sent, Processed;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int              id;

    @Column(name = "data")
    private String           data;

    @Transient
    private EventType        eventType;

    @Column(name = "event_type_id")
    private Integer          eventTypeId;

    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date             createdDate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date             updatedDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventStatus      eventStatus;

    @Column(name = "merged_event_id")
    private Integer          mergedEventId;

    @Column(name = "expiry_date")
    private Date             expiryDate;

    @Column(name = "event_type_unique_key")
    private String           eventTypeUniqueKey;

    @Transient
    private EventTypePayload eventTypePayload;

    @PreUpdate
    public void autoUpdateFields() {
        this.updatedDate = new Date();
    }

    @PrePersist
    public void autoPopulateFields() {
        this.createdDate = new Date();
        this.eventStatus = EventStatus.Raw;

        autoUpdateFields();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Integer getMergedEventId() {
        return mergedEventId;
    }

    public void setMergedEventId(Integer mergedEventId) {
        this.mergedEventId = mergedEventId;
    }

    public EventTypePayload getEventTypePayload() {
        return eventTypePayload;
    }

    public void setEventTypePayload(EventTypePayload eventTypePayload) {
        this.eventTypePayload = eventTypePayload;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEventTypeUniqueKey() {
        return eventTypeUniqueKey;
    }

    public void setEventTypeUniqueKey(String eventTypeUniqueKey) {
        this.eventTypeUniqueKey = eventTypeUniqueKey;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
