package com.proptiger.data.event.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "raw_event_table_details")
public class DBRawEventTableLog extends BaseModel {
    /**
     * 
     */
    private static final long   serialVersionUID = -4192519971268898777L;

    @Id
    @Column(name = "id")
    private int                 id;

    @Column(name = "host_name")
    private String              hostName;

    @Column(name = "db_name")
    private String              dbName;

    @Column(name = "table_name")
    private String              tableName;

    @Column(name = "primary_column_name")
    private String              primaryKeyName;

    @Column(name = "transaction_column_name")
    private String              transactionKeyName;

    @Column(name = "transaction_date_column_name")
    private String              dateAttributeName;

    @Column(name = "transaction_column_value")
    private Long                lastTransactionKeyValue;

    @Column(name = "filters")
    private String              filters;

    @Transient
    private Map<String, Object> filterMap;

    @PostLoad
    public void populateTransientFields() {
        if (this.filters != null) {
            try {
                this.filterMap = new Gson().fromJson(this.filters, Map.class);
            }
            catch (JsonSyntaxException e) {

            }
        }
    }

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

    public Long getLastTransactionKeyValue() {
        return lastTransactionKeyValue;
    }

    public void setLastTransactionKeyValue(Long lastTransactionKeyValue) {
        this.lastTransactionKeyValue = lastTransactionKeyValue;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public Map<String, Object> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, Object> filterMap) {
        this.filterMap = filterMap;
    }

}
