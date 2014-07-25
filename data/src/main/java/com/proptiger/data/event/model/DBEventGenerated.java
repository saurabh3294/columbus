package com.proptiger.data.event.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.proptiger.data.model.event.payload.EventTypePayload;

@Entity
@Table(name = "raw_event_generated")
public class DBEventGenerated extends Event {

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
    private int         id;
    
    @Column(name = "data")
    private String      data;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "event_type_id")
    private EventType   eventType;
    
    @Column(name = "created_date")
    private Date        createdDate;
    
    @Column(name = "updated_date")
    private Date        updatedDate;
    
    @Column(name = "status")
    private EventStatus eventStatus;
    
    @Column(name = "merge_event_id")
    private int mergedEventId;
    
    @Transient
    private EventTypePayload eventTypePayload;
    
    @PostLoad
    public void setPayload(){
        this.eventTypePayload = (EventTypePayload)new Gson().fromJson(this.data, eventType.getName().getDataClassName());
    }
    
    public DBEventGenerated test(DBEventGenerated t){
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
    
    
    /*private String      tableName;
    private String      attrName;
    private Object      oldValue;
    private Object      newValue;
    private DBOperation dbOperation;

    private String      schedulePolicy;*/
}
