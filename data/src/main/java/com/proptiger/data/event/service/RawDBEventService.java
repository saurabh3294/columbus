package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.enums.EventAllAttributeName;
import com.proptiger.data.event.generator.model.RawDBEventTableConfig;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.RawEventTableDetails;
import com.proptiger.data.event.repo.RawDBEventDao;
import com.proptiger.data.event.repo.RawEventTableDetailsDao;
import com.proptiger.data.util.Serializer;

@Service
public class RawDBEventService {
    private static Logger logger = LoggerFactory.getLogger(RawDBEventService.class);

    @Autowired
    private RawDBEventDao rawDBEventDao;
    
    @Autowired
    private RawEventTableDetailsDao rawEventTableDetailsDao;
    
    public List<RawDBEvent> getRawDBEvents(RawDBEventTableConfig rawDBEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();
        RawEventTableDetails tableLog = rawDBEventTableConfig.getRawEventTableDetails();

        if(tableLog.getLastTransactionKeyValue() == null) {
            Map<String, Object> latestTransaction = rawDBEventDao.getLatestTransaction(tableLog);
            Long transactionId = Long.parseLong(latestTransaction.get(tableLog.getTransactionKeyName()).toString());
            rawEventTableDetailsDao.updateLastTransactionKeyValueById(tableLog.getId(), transactionId);
            return rawDBEventList;
        }
        
        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndId(tableLog);
        
        logger.info(" Retrieved "+rawDBEventDataList.size() + " raw events from the table config ID: "+ tableLog.getId());
        
        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {
            //logger.debug(rawDBEventMap.toString());
            // TODO to handle the null value of the dbOperation if it is not
            // found.
            DBOperation dbOperation = DBOperation.getDBOperationEnum((Character) rawDBEventMap
                    .get(RawDBEventTableConfig.getDbOperationAttributeName()));
            
            if (rawDBEventTableConfig.getDbRawEventOperationConfig(dbOperation) == null) {
                logger.debug("Skipping raw event creation as DbRawEventOperationConfig not found for " + rawDBEventMap.toString());
                continue;
            }
            
            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setRawEventTableDetails(tableLog);
            rawDBEvent.setRawDBEventOperationConfig(rawDBEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(tableLog.getPrimaryKeyName()).toString());
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(tableLog.getTransactionKeyName()).toString());
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(tableLog.getDateAttributeName()));
            rawDBEvent.setUniqueKeyValuesMap(getUniqueKeyValuesMap(rawDBEventMap, tableLog));
            rawDBEventList.add(rawDBEvent);
            
            //logger.debug(" RAW DB EVENT Generated "+Serializer.toJson(rawDBEvent));
        }

        return rawDBEventList;
    }

    public RawDBEvent populateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" POPULATE OLD VALUE FOR Raw DB Event with Transaction Id : "+rawDBEvent.getTransactionKeyValue());
        
        if (DBOperation.INSERT.equals(rawDBEvent.getRawDBEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateInsertRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getRawDBEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateDeleteRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getRawDBEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateUpdateRawDBEventData(rawDBEvent);
        }
        return rawDBEvent;
    }

    public RawDBEvent populateInsertRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" INSERT RAW DB EVENT ");
        Map<String, Object> allattributes = new HashMap<String, Object>();
        allattributes.put(EventAllAttributeName.All.name(), rawDBEvent.getNewDBValueMap());
        rawDBEvent.setNewDBValueMap(allattributes);
        
        return rawDBEvent;
    }

    public RawDBEvent populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" DELETE RAW DB EVENT ");
        return rawDBEvent;
    }

    public RawDBEvent populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.info(" UPDATE RAW DB EVENT ");
        
        RawEventTableDetails tableLog = rawDBEvent.getRawEventTableDetails();
        Map<String, Object> oldRawEventDataMap = rawDBEventDao.getOldRawDBEvent(
                tableLog,
                rawDBEvent.getTransactionKeyValue(),
                rawDBEvent.getPrimaryKeyValue(),
                rawDBEvent.getUniqueKeyValuesMap());
        
        if(oldRawEventDataMap == null){
            return null;
        }
        
        Map<String, Object> newRawEventDataMap = rawDBEvent.getNewDBValueMap();
        Map<String, Object> oldRawEventDataMapToSet = rawDBEvent.getOldDBValueMap();
        
        Iterator<Map.Entry<String, Object>> it = newRawEventDataMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            
            Object obj = oldRawEventDataMap.get(key);
            Object newObj = entry.getValue();
            
            /**
             *  Making new Object and old objects as null.
             *  Making the comparison easier as "" and null
             *  should be matched as same.
             */
            if(newObj == null){
                newObj = "";
            }
            if(obj == null){
                obj = "";
            }
            
            if (newObj.equals(obj)) {
                it.remove();
            }
            else {
                oldRawEventDataMapToSet.put(key, obj);
            }
        }
        
        logger.debug(" FIELDS FOUND UPDATED "+ Serializer.toJson(oldRawEventDataMapToSet));
        
        return rawDBEvent;
    }

    public Map<String, Object> getRawEventTransactionRow(
            RawEventTableDetails dbRawEventTableLog,
            Object transactionKeyValue) {
        return rawDBEventDao.getRawEventDataOnTransactionId(dbRawEventTableLog, transactionKeyValue);
    }

    private Map<String, Object> getUniqueKeyValuesMap(
            Map<String, Object> rawDbEventDataMap,
            RawEventTableDetails dbRawEventTableLog) {
        String[] uniqueKeyStrings = dbRawEventTableLog.getUniqueKeysArray();
        Map<String, Object> postFiltersMap = new HashMap<String, Object>();
        Object value = null;
        if (uniqueKeyStrings != null && uniqueKeyStrings.length > 0) {
            for (int i = 0; i < uniqueKeyStrings.length; i++) {
                value = rawDbEventDataMap.get(uniqueKeyStrings[i]);
                if (value != null) {
                    postFiltersMap.put(uniqueKeyStrings[i], value);
                    logger.info(" SQL UNIQUE KEY VALUE "+uniqueKeyStrings[i]+ " VALUE : "+value.toString());
                }
            }
        }

        return postFiltersMap;

    }
}
