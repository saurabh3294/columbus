package com.proptiger.data.event.repo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proptiger.data.event.model.EventType;

public class EventTypeMappingDaoImpl {
	
	public List<EventType> getEventTypesForInsertDBOperation(String hostName, String dbName, String tableName) {
		return null;		
	}
	
	public List<EventType> getEventTypesForDeleteDBOperation(String hostName, String dbName, String tableName) {
		return null;		
	}
	
	public List<EventType> getEventTypesForUpdateDBOperation(String hostName, String dbName, String tableName, String attributeName) {
		return null;		
	}
	
	
}
