package com.proptiger.data.event.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "event_type_mapping")
public class EventTypeMapping extends BaseModel{

    @Column(name = "id")
    @Id
    private int         id;
    
    @Column(name = "db_operation")
    @Enumerated(EnumType.STRING)
    private DBOperation dbOperation;

    @Column(name = "attribute_name")
    private String      attributeName;
    
    @Column(name = "event_type_id", insertable = false, updatable = false)
    private Integer eventTypeId;

    //@OneToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    @Transient
    private EventType   eventType;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_event_table_id")
    private DBRawEventTableLog dbRawEventTableLog;
    
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DBOperation getDbOperation() {
        return dbOperation;
    }

    public void setDbOperation(DBOperation dbOperation) {
        this.dbOperation = dbOperation;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public DBRawEventTableLog getDbRawEventTableLog() {
        return dbRawEventTableLog;
    }

    public void setDbRawEventTableLog(DBRawEventTableLog dbRawEventTableLog) {
        this.dbRawEventTableLog = dbRawEventTableLog;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
