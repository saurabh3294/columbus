package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.DBRawEventAttributeConfig;
import com.proptiger.data.event.generator.model.DBRawEventOperationConfig;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.EventTypeMapping;
import com.proptiger.data.event.repo.EventTypeMappingDao;

@Service
public class EventTypeMappingService {
    
    @Autowired
    private EventTypeMappingDao eventTypeMappingDao;
    
    public static List<DBRawEventTableConfig> dbRawEventTableConfig;
    
    @PostConstruct
    public void constructDbConfig(){
        Iterator<EventTypeMapping> listEventTypeMapping = eventTypeMappingDao.findAll().iterator();
        
        Map<String, DBRawEventTableConfig> dbRawEventMapping = new HashMap<String, DBRawEventTableConfig>();
        Map<String, DBRawEventOperationConfig> dbOperationMap = new HashMap<String, DBRawEventOperationConfig>();
        Map<String, DBRawEventAttributeConfig> dbAttributeMap = new HashMap<String, DBRawEventAttributeConfig>();
        //Map<String, Map<DBOperation, Map<String, DBRawEventTableConfig>>> map = new HashMap<String, Map<DBOperation,Map<String,DBRawEventTableConfig>>>(); 
        while(listEventTypeMapping.hasNext()){
            EventTypeMapping eventTypeMapping = listEventTypeMapping.next();
            String eventKey = eventTypeMapping.getHostName()+eventTypeMapping.getDbName()+eventTypeMapping.getTableName();
            if(dbRawEventMapping.get(eventKey) == null){
                if(eventTypeMapping.getAttributeName()!= null){
                  //  DBRawEventAttributeConfig dbRawEventAttributeConfig = new DBRawEventAttributeConfig(eventTypeMapping.getAttributeName(), new ArrayList<EventType>(eventTypeMapping.getEventType()));
                }
                DBRawEventOperationConfig dbRawEventOperationConfig = new DBRawEventOperationConfig();
                DBRawEventTableConfig dbRawEventTableConfig = new DBRawEventTableConfig();
            }
            
        }
       
    }

}
