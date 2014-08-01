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
@Table(name = "raw_event_to_event_type_mapping")
public class EventTypeMapping {

    @Column(name = "id")
    @Id
    private int         id;

    @Column(name = "host_name")
    private String      hostName;

    @Column(name = "db_name")
    private String      dbName;

    @Column(name = "table_name")
    private String      tableName;

    @Column(name = "primary_key_name")
    private String      primaryKeyName;

    @Column(name = "transaction_key_name")
    private String      transactionKeyName;

    @Column(name = "dbOperation")
    private DBOperation dbOperation;

    @Column(name = "attribute_name")
    private String      attributeName;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventType   eventType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTransactionKeyName() {
        return transactionKeyName;
    }

    public void setTransactionKeyName(String transactionKeyName) {
        this.transactionKeyName = transactionKeyName;
    }

}
