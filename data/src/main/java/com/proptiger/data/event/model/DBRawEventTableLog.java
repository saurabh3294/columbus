package com.proptiger.data.event.model;

import java.util.List;
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
@Table(name = "notification.raw_event_table_details")
public class DBRawEventTableLog extends BaseModel {
    /**
     * 
     */
    private static final long         serialVersionUID = -4192519971268898777L;

    @Id
    @Column(name = "id")
    private int                       id;

    @Column(name = "host_name")
    private String                    hostName;

    @Column(name = "db_name")
    private String                    dbName;

    @Column(name = "table_name")
    private String                    tableName;

    @Column(name = "primary_column_name")
    private String                    primaryKeyName;

    @Column(name = "transaction_column_name")
    private String                    transactionKeyName;

    @Column(name = "transaction_date_column_name")
    private String                    dateAttributeName;

    @Column(name = "transaction_column_value")
    private Long                      lastTransactionKeyValue;

    /*
     * List of filters that needs to be applied while reading an entry from the
     * trigger table.
     */
    @Column(name = "pre_filters")
    private String                    prefilters;

    @Column(name = "unique_keys")
    private String                    uniqueKeys;

    @Transient
    private Map<String, List<Object>> filterMap;

    @Transient
    private String[]                  uniqueKeysArray;

    @SuppressWarnings("unchecked")
    @PostLoad
    public void populateTransientFields() {
        if (this.prefilters != null) {
            try {
                this.filterMap = new Gson().fromJson(this.prefilters, Map.class);
            }
            catch (JsonSyntaxException e) {

            }
        }

        if (this.uniqueKeys != null) {
            this.uniqueKeysArray = this.uniqueKeys.split(",");
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

    public Map<String, List<Object>> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, List<Object>> filterMap) {
        this.filterMap = filterMap;
    }

    public String getPrefilters() {
        return prefilters;
    }

    public void setPrefilters(String prefilters) {
        this.prefilters = prefilters;
    }

    public String[] getUniqueKeysArray() {
        return uniqueKeysArray;
    }

    public void setUniqueKeysArray(String[] postFilterKeysArray) {
        this.uniqueKeysArray = postFilterKeysArray;
    }

    public String getUniqueKeys() {
        return uniqueKeys;
    }

    public void setUniqueKeys(String uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }

}
