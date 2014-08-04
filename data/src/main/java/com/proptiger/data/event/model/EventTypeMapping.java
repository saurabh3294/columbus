package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.data.event.enums.DBOperation;

@Entity
@Table(name = "event_type_mapping")
public class EventTypeMapping {

    @Column(name = "id")
    @Id
    private int         id;
    
    @Column(name = "dbOperation")
    private DBOperation dbOperation;

    @Column(name = "attribute_name")
    private String      attributeName;
    
    @Column(name = "event_type_id", insertable = false, updatable = false)
    private Integer eventTypeId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventType   eventType;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_event_table_id")
    private DBRawEventTableLog dbRawEventTableLog;

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
}
