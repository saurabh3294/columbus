package com.proptiger.data.event.repo;

import java.util.List;

import com.proptiger.data.event.model.EventType;

public interface DBRawEventToEventTypeMappingDao {
	
	public List<EventType> getEventTypesForInsertDBOperation(String hostName, String dbName, String tableName);
	
	public List<EventType> getEventTypesForDeleteDBOperation(String hostName, String dbName, String tableName);
	
	public List<EventType> getEventTypesForUpdateDBOperation(String hostName, String dbName, String tableName, String attributeName);
		
}
