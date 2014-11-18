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

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.event.model.payload.EventTypePayload;

@Entity
@Table(name = "notification.event_generated")
public class EventGenerated extends BaseModel {

    private static final long serialVersionUID = 778194433417706629L;

    public enum EventStatus {
        Raw, Merged, Discarded, Verified, PendingVerification, Sent, Processed;
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

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date             createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date             updatedAt;

    @PreUpdate
    public void autoUpdateFields() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void autoPopulateFields() {
        this.createdAt = new Date();
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
