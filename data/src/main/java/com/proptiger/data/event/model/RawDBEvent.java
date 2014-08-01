package com.proptiger.data.event.model;

import java.util.Map;

import com.proptiger.data.event.enums.DBOperation;

/**
 * 
 * @author sahil
 *
 */
public class RawDBEvent {
	
	private String hostName;
	private String dbName;
	private String tableName;
	private DBOperation dbOperation;
	private Map<String, Object> oldDBValueMap;
	private Map<String, Object> newDBValueMap;	
	private String idName;
	private Object idValue;

	
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

	public DBOperation getDbOperation() {
		return dbOperation;
	}

	public void setDbOperation(DBOperation dbOperation) {
		this.dbOperation = dbOperation;
	}

    public Map<String, Object> getOldDBValueMap() {
        return oldDBValueMap;
    }

    public void setOldDBValueMap(Map<String, Object> oldDBValueMap) {
        this.oldDBValueMap = oldDBValueMap;
    }

    public Map<String, Object> getNewDBValueMap() {
        return newDBValueMap;
    }

    public void setNewDBValueMap(Map<String, Object> newDBValueMap) {
        this.newDBValueMap = newDBValueMap;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Object getIdValue() {
        return idValue;
    }

    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }

	
		
}
