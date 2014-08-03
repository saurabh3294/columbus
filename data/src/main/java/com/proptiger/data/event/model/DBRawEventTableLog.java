package com.proptiger.data.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "raw_event_table_details")
public class DBRawEventTableLog {
    @Id
    @Column(name = "id")
    private int    id;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "primary_column_name")
    private String primaryKeyName;

    @Column(name = "transaction_column_name")
    private String transactionKeyName;

    @Column(name = "transaction_date_column_name")
    private String dateAttributeName;

    @Column(name = "transaction_date_column_value")
    private String dateAttributeValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public String getTransactionKeyName() {
        return transactionKeyName;
    }

    public void setTransactionKeyName(String transactionKeyName) {
        this.transactionKeyName = transactionKeyName;
    }

    public String getDateAttributeName() {
        return dateAttributeName;
    }

    public void setDateAttributeName(String dateAttributeName) {
        this.dateAttributeName = dateAttributeName;
    }

    public String getDateAttributeValue() {
        return dateAttributeValue;
    }

    public void setDateAttributeValue(String dateAttributeValue) {
        this.dateAttributeValue = dateAttributeValue;
    }
}
