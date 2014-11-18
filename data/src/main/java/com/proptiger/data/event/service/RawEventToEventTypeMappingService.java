package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.RawDBEventAttributeConfig;
import com.proptiger.data.event.generator.model.RawDBEventOperationConfig;
import com.proptiger.data.event.generator.model.RawDBEventTableConfig;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawEventToEventTypeMapping;
import com.proptiger.data.event.repo.RawEventToEventTypeMappingDao;
import com.proptiger.data.util.Serializer;

@Service
public class RawEventToEventTypeMappingService {
    private static Logger                     logger = LoggerFactory.getLogger(RawEventToEventTypeMappingService.class);

    @Autowired
    private RawEventToEventTypeMappingDao     rawEventToEventTypeMappingDao;

    @Autowired
    private EventTypeService                  eventTypeService;

    public static List<RawDBEventTableConfig> rawDBEventTableConfigs;

    @PostConstruct
    public void constructDbConfig() {
        logger.debug("Constructing RawEventToEventTypeMapping Config.");
        Iterable<RawEventToEventTypeMapping> listEventTypeMapping = getAllMappingOfRawEventsToEventType();
        Iterator<RawEventToEventTypeMapping> itEvenIterator = listEventTypeMapping.iterator();

        Map<Integer, RawDBEventTableConfig> dbRawEventMapping = new HashMap<Integer, RawDBEventTableConfig>();
        Map<String, RawDBEventOperationConfig> dbOperationMap = new HashMap<String, RawDBEventOperationConfig>();
        Map<String, RawDBEventAttributeConfig> dbAttributeMap = new HashMap<String, RawDBEventAttributeConfig>();
        Map<String, EventType> dbEventTypemap = new HashMap<String, EventType>();

        List<EventType> eventTypeList;
        List<RawDBEventOperationConfig> operationConfigslist;
        List<RawDBEventAttributeConfig> attributeConfigslist;

        rawDBEventTableConfigs = new ArrayList<RawDBEventTableConfig>();

        while (itEvenIterator.hasNext()) {
            RawEventToEventTypeMapping eventTypeMapping = itEvenIterator.next();

            Integer eventKey = eventTypeMapping.getRawEventTableDetails().getId();
            String operationKey = eventKey + eventTypeMapping.getDbOperation().name();
            String attributeKey = operationKey;
            if (eventTypeMapping.getAttributeName() != null) {
                attributeKey += eventTypeMapping.getAttributeName();
            }
            String eventTypeKey = attributeKey + eventTypeMapping.getEventType().getName();

            if (dbRawEventMapping.get(eventKey) == null) {
                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                RawDBEventAttributeConfig attributeConfig = null;
                RawDBEventOperationConfig operationConfig = new RawDBEventOperationConfig(
                        eventTypeMapping.getDbOperation(),
                        null,
                        null);

                operationConfigslist = new ArrayList<RawDBEventOperationConfig>();
                operationConfigslist.add(operationConfig);
                dbOperationMap.put(operationKey, operationConfig);

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new RawDBEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    attributeConfigslist = new ArrayList<RawDBEventAttributeConfig>();
                    attributeConfigslist.add(attributeConfig);

                    operationConfig.setRawDBEventAttributeConfigs(attributeConfigslist);
                    dbAttributeMap.put(attributeKey, attributeConfig);

                }
                else {
                    operationConfig.setListEventTypes(eventTypeList);
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());

                RawDBEventTableConfig TableConfig = new RawDBEventTableConfig(
                        eventTypeMapping.getRawEventTableDetails(),
                        operationConfigslist);
                dbRawEventMapping.put(eventKey, TableConfig);
                rawDBEventTableConfigs.add(TableConfig);

            }
            else if (dbOperationMap.get(operationKey) == null) {
                // common code starts
                RawDBEventTableConfig tableConfig = dbRawEventMapping.get(eventKey);

                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                RawDBEventAttributeConfig attributeConfig = null;
                RawDBEventOperationConfig operationConfig = new RawDBEventOperationConfig(
                        eventTypeMapping.getDbOperation(),
                        null,
                        null);

                dbOperationMap.put(operationKey, operationConfig);

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new RawDBEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    attributeConfigslist = new ArrayList<RawDBEventAttributeConfig>();
                    attributeConfigslist.add(attributeConfig);

                    operationConfig.setRawDBEventAttributeConfigs(attributeConfigslist);
                    dbAttributeMap.put(attributeKey, attributeConfig);
                }
                else {
                    operationConfig.setListEventTypes(eventTypeList);
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());
                // common code ends.

                tableConfig.getRawDBEventOperationConfigs().add(operationConfig);
            }
            else if (dbAttributeMap.get(attributeKey) == null) {
                RawDBEventOperationConfig operationConfig = dbOperationMap.get(operationKey);

                eventTypeList = new ArrayList<EventType>();
                eventTypeList.add(eventTypeMapping.getEventType());

                RawDBEventAttributeConfig attributeConfig = null;

                if (eventTypeMapping.getAttributeName() != null) {

                    attributeConfig = new RawDBEventAttributeConfig(eventTypeMapping.getAttributeName(), eventTypeList);

                    operationConfig.getRawDBEventAttributeConfigs().add(attributeConfig);
                    dbAttributeMap.put(attributeKey, attributeConfig);

                }
                else {
                    operationConfig.getListEventTypes().add(eventTypeMapping.getEventType());
                }
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());

            }
            else if (dbEventTypemap.get(eventTypeKey) == null) {
                RawDBEventAttributeConfig attributeConfig = dbAttributeMap.get(attributeKey);
                attributeConfig.getListEventTypes().add(eventTypeMapping.getEventType());
                dbEventTypemap.put(eventTypeKey, eventTypeMapping.getEventType());
            }
        }
        logger.debug("RawEventToEventTypeMapping " + Serializer.toJson(rawDBEventTableConfigs));
    }

    public Iterable<RawEventToEventTypeMapping> getAllMappingOfRawEventsToEventType() {
        Iterable<RawEventToEventTypeMapping> listEventTypeMapping = findAll();
        Iterator<RawEventToEventTypeMapping> itEventTypeMapping = listEventTypeMapping.iterator();

        while (itEventTypeMapping.hasNext()) {
            RawEventToEventTypeMapping eventTypeMapping = itEventTypeMapping.next();
            setEventTypeObject(eventTypeMapping);
        }

        return listEventTypeMapping;
    }

    public Iterable<RawEventToEventTypeMapping> findAll() {
        return rawEventToEventTypeMappingDao.findAllMapping();
    }

    public RawEventToEventTypeMapping getMappingByEventTypeId(Integer eventTypeId) {
        RawEventToEventTypeMapping eventTypeMapping = rawEventToEventTypeMappingDao.findByEventTypeId(eventTypeId).get(
                0);
        setEventTypeObject(eventTypeMapping);
        return eventTypeMapping;
    }

    private void setEventTypeObject(RawEventToEventTypeMapping eventTypeMapping) {
        EventType eventType = eventTypeService.populateConfig(eventTypeMapping.getEventType());
        eventTypeMapping.setEventType(eventType);
    }

    public List<RawDBEventTableConfig> getRawDBEventTableConfigs() {
        return rawDBEventTableConfigs;
    }

    public void setDbRawEventTableConfigs(List<RawDBEventTableConfig> rawDBEventTableConfigs) {
        RawEventToEventTypeMappingService.rawDBEventTableConfigs = rawDBEventTableConfigs;
    }
}
