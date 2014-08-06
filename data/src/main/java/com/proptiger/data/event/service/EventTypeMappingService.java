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
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.EventTypeMapping;
import com.proptiger.data.event.repo.EventTypeMappingDao;

@Service
public class EventTypeMappingService {

    @Autowired
    private EventTypeMappingDao               eventTypeMappingDao;
    
    @Autowired
    private EventTypeService 				  eventTypeService;

    public static List<DBRawEventTableConfig> dbRawEventTableConfig;

    @PostConstruct
    public void constructDbConfig() {
        Iterator<EventTypeMapping> listEventTypeMapping = getAllMappingOfRawEventsToEventType();

        Map<Integer, DBRawEventTableConfig> dbRawEventMapping = new HashMap<Integer, DBRawEventTableConfig>();
        Map<String, DBRawEventOperationConfig> dbOperationMap = new HashMap<String, DBRawEventOperationConfig>();
        Map<String, DBRawEventAttributeConfig> dbAttributeMap = new HashMap<String, DBRawEventAttributeConfig>();
        Map<String, EventType> dbEventTypemap = new HashMap<String, EventType>();

        List<EventType> eventTypeList;
        List<DBRawEventOperationConfig> operationConfigslist;
        List<DBRawEventAttributeConfig> attributeConfigslist;

        dbRawEventTableConfigs = new ArrayList<DBRawEventTableConfig>();
        while (listEventTypeMapping.hasNext()) {
            EventTypeMapping eventTypeMapping = listEventTypeMapping.next();
            Integer eventKey = eventTypeMapping.getDbRawEventTableLog().getId();
            String operationKey = eventKey + eventTypeMapping.getDbOperation().name();
            String attributeKey = operationKey;
            if (eventTypeMapping.getAttributeName() != null) {
                attributeKey += eventTypeMapping.getAttributeName();
            }
            String eventTypeKey = attributeKey + eventTypeMapping.getEventType().getName();

            if (dbRawEventMapping.get(eventKey) == null) {
                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                DBRawEventAttributeConfig attributeConfig = null;
                DBRawEventOperationConfig operationConfig = new DBRawEventOperationConfig(
                        eventTypeMapping.getDbOperation(),
                        null,
                        null);

                operationConfigslist = new ArrayList<DBRawEventOperationConfig>();
                operationConfigslist.add(operationConfig);
                dbOperationMap.put(operationKey, operationConfig);

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new DBRawEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    attributeConfigslist = new ArrayList<DBRawEventAttributeConfig>();
                    attributeConfigslist.add(attributeConfig);

                    operationConfig.setListDBRawEventAttributeConfigs(attributeConfigslist);
                    dbAttributeMap.put(attributeKey, attributeConfig);

                }
                else {
                    operationConfig.setListEventTypes(eventTypeList);
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());

                DBRawEventTableConfig TableConfig = new DBRawEventTableConfig(
                        eventTypeMapping.getDbRawEventTableLog(),
                        operationConfigslist);
                dbRawEventMapping.put(eventKey, TableConfig);
                dbRawEventTableConfigs.add(TableConfig);

            }
            else if (dbOperationMap.get(attributeKey) == null) {
                // common code starts
                DBRawEventTableConfig tableConfig = dbRawEventMapping.get(eventKey);

                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                DBRawEventAttributeConfig attributeConfig = null;
                DBRawEventOperationConfig operationConfig = new DBRawEventOperationConfig(
                        eventTypeMapping.getDbOperation(),
                        null,
                        null);

                dbOperationMap.put(operationKey, operationConfig);

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new DBRawEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    attributeConfigslist = new ArrayList<DBRawEventAttributeConfig>();
                    attributeConfigslist.add(attributeConfig);

                    operationConfig.setListDBRawEventAttributeConfigs(attributeConfigslist);
                    dbAttributeMap.put(attributeKey, attributeConfig);
                }
                else {
                    operationConfig.setListEventTypes(eventTypeList);
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());
                // common code ends.

                tableConfig.getDbRawEventOperationConfigs().add(operationConfig);
            }
            else if (dbAttributeMap.get(attributeKey) == null) {
                DBRawEventOperationConfig operationConfig = dbOperationMap.get(operationKey);

                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                DBRawEventAttributeConfig attributeConfig = null;

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new DBRawEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    operationConfig.getListDBRawEventAttributeConfigs().add(attributeConfig);
                    dbAttributeMap.put(attributeKey, attributeConfig);

                }
                else {
                    operationConfig.getListEventTypes().add(eventTypeMapping.getEventType());
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());

            }
            else if (dbEventTypemap.get(eventTypeKey) == null) {
                DBRawEventAttributeConfig attributeConfig = dbAttributeMap.get(attributeKey);
                attributeConfig.getListEventTypes().add(eventTypeMapping.getEventType());
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());
            }
        }
    }

    public Iterator<EventTypeMapping> getAllMappingOfRawEventsToEventType(){
        Iterator<EventTypeMapping> listEventTypeMapping = eventTypeMappingDao.findAll().iterator();
        
        while(listEventTypeMapping.hasNext()){
            EventTypeMapping eventTypeMapping = listEventTypeMapping.next();
            setEventTypeObject(eventTypeMapping);
        }
        
        return listEventTypeMapping;
    }
    
    public EventTypeMapping getMappingByEventTypeId(Integer eventTypeId) {
        EventTypeMapping eventTypeMapping = eventTypeMappingDao.findByEventTypeId(eventTypeId).get(0);
        setEventTypeObject(eventTypeMapping);
        
        return eventTypeMapping;
    }
    
    private void setEventTypeObject(EventTypeMapping eventTypeMapping){
        EventType eventType = eventTypeService.getEventTypeByEventTypeId(eventTypeMapping.getEventTypeId());
        eventTypeMapping.setEventType(eventType);
    }

    public List<DBRawEventTableConfig> getDbRawEventTableConfigs() {
        return dbRawEventTableConfigs;
    }

    public void setDbRawEventTableConfigs(List<DBRawEventTableConfig> dbRawEventTableConfigs) {
        this.dbRawEventTableConfigs = dbRawEventTableConfigs;
    }

}
