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

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.event.enums.DBOperation;

@Entity
@Table(name = "notification.raw_event_to_event_type_mapping")
public class RawEventToEventTypeMapping extends BaseModel {

    /**
     * 
     */
    private static final long    serialVersionUID = 8620987853665861856L;

    @Column(name = "id")
    @Id
    private int                  id;

    @Column(name = "db_operation")
    @Enumerated(EnumType.STRING)
    private DBOperation          dbOperation;

    @Column(name = "attribute_name")
    private String               attributeName;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventType            eventType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_event_table_id")
    private RawEventTableDetails rawEventTableDetails;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                 createdAt;

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

    public RawEventTableDetails getRawEventTableDetails() {
        return rawEventTableDetails;
    }

    public void setRawEventTableDetails(RawEventTableDetails rawEventTableDetails) {
        this.rawEventTableDetails = rawEventTableDetails;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
