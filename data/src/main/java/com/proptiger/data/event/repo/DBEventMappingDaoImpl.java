package com.proptiger.data.event.repo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proptiger.data.event.model.EventType;

public class DBEventMappingDaoImpl implements DBEventMappingDao {
	
	private static final String DB_EVENT_MAPPING_FILE = "DBEventMapping.json";
	
	private JsonObject read() {
		JsonParser parser = new JsonParser();	
		JsonObject obj = null;
		try {	 
			obj = (JsonObject) parser.parse(new FileReader(DB_EVENT_MAPPING_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
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
