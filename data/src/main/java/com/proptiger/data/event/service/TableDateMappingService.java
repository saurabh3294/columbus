package com.proptiger.data.event.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;

public class TableDateMappingService {
    
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
    
    // TODO
    public List<DBRawEventTableConfig> polulateLastAccessedDate(List<DBRawEventTableConfig> dbRawEventTableConfigs) {
        return null;
    }
    
    // TODO
    public void updateTableDateMap(List<DBRawEventTableConfig> dbRawEventTableConfigs) {
        
    }
}
