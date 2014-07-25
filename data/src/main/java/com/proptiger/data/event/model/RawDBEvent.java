package com.proptiger.data.event.model;

import java.util.Map;

import com.proptiger.data.event.enums.DBOperation;

/**
 * 
 * @author sahil
 *
 */
public abstract class RawDBEvent {
	
	private String hostName;
	private String dbName;
	private String tableName;
	private DBOperation dbOperation;
	
	/* Map of Attribute and (oldDbValue, newDbValue) */
	private Map<String, Object> dbValueMap;
	
	/* Map of Attribute and tablePrimaryKey */
	private Map<String, Object> idMap;

	
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

	public Map<String, Object> getDbValueMap() {
		return dbValueMap;
	}

	public void setDbValueMap(Map<String, Object> dbValueMap) {
		this.dbValueMap = dbValueMap;
	}

	public Map<String, Object> getIdMap() {
		return idMap;
	}

	public void setIdMap(Map<String, Object> idMap) {
		this.idMap = idMap;
	}
		
}
