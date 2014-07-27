package com.proptiger.data.event.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.proptiger.data.model.event.payload.EventTypePayload;

@Entity
@Table(name = "raw_event_generated")
public class EventGenerated extends Event {

    public enum EventStatus {
        Raw("raw"), Merged("merged"), Discarded("discarded"), Verfied("verified"), PendingVerification(
                "pending_verfication"), Sent("sent"), Processed("processed");

        private String name;

        EventStatus(String name) {
            this.name = name;
            // TODO Auto-generated constructor stub
        }
    }

    @Column(name = "id")
    @Id
    private int              id;

    @Column(name = "data")
    private String           data;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id")
    private EventType        eventType;

    @Column(name = "created_date")
    private Date             createdDate;

    @Column(name = "updated_date")
    private Date             updatedDate;

    @Column(name = "status")
    private EventStatus      eventStatus;

    @Column(name = "merge_event_id")
    private Integer          mergedEventId;

    @Column(name = "expiry_date")
    private Date             expiryDate;

    @Transient
    private EventTypePayload eventTypePayload;

    @PostLoad
    public void setPayload() {
        // TODO to look into the Gson works on the recursive level of objects from json or not.
        this.eventTypePayload = (EventTypePayload) new Gson().fromJson(this.data, eventType.getName()
                .getDataClassName());

        String uniqueKeyString = "";
        for (Map.Entry<String, Object> entry : eventTypePayload.getIdMap().entrySet()) {
            uniqueKeyString += entry.getValue() + "-";
        }
        this.eventTypePayload.setUniqueKeyString(uniqueKeyString);
    }
    
    @PreUpdate
    public void updatePayload() {
        // TODO to look into the Gson works on the recursive level of objects from json or not.
        this.data = new Gson().toJson(this.eventTypePayload);
    }

    public EventGenerated test(EventGenerated t) {
        return this;
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
}
