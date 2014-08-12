package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.DBRawEventTableLogDao;
import com.proptiger.data.event.repo.RawDBEventDao;
import com.proptiger.data.util.Serializer;

@Service
public class RawDBEventService {
    private static Logger logger = Logger.getLogger(RawDBEventService.class);

    @Autowired
    private RawDBEventDao rawDBEventDao;
    
    @Autowired
    private DBRawEventTableLogDao dbRawEventTableLogDao;

    public List<RawDBEvent> getRawDBEvents(DBRawEventTableConfig dbRawEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();
        DBRawEventTableLog tableLog = dbRawEventTableConfig.getDbRawEventTableLog();

        if(tableLog.getLastTransactionKeyValue() == null) {
            Map<String, Object> latestTransaction = rawDBEventDao.getLatestTransaction(tableLog);
            Long transactionId = (Long)latestTransaction.get(tableLog.getTransactionKeyName());
            dbRawEventTableLogDao.updateLastTransactionKeyValueById(tableLog.getId(), transactionId);
            return rawDBEventList;
        }
        
        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndId(tableLog);
        
        logger.info(" Retrieved "+rawDBEventDataList.size() + " raw events from the table config ID: "+ tableLog.getId());
        
        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {
            logger.debug(rawDBEventMap);
            // TODO to handle the null value of the dbOperation if it is not
            // found.
            DBOperation dbOperation = DBOperation.getDBOperationEnum((Character) rawDBEventMap
                    .get(DBRawEventTableConfig.getDbOperationAttributeName()));
            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setDbRawEventTableLog(tableLog);
            rawDBEvent.setDbRawEventOperationConfig(dbRawEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(tableLog.getPrimaryKeyName()));
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(tableLog.getTransactionKeyName()));
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(tableLog.getDateAttributeName()));
            rawDBEvent.setUniqueKeyValuesMap(getUniqueKeyValuesMap(rawDBEventMap, tableLog));
            rawDBEventList.add(rawDBEvent);
            
            logger.debug(" RAW DB EVENT Generated "+Serializer.toJson(rawDBEvent));
        }

        return rawDBEventList;
    }

    public RawDBEvent populateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" POPULATE OLD VALUE FOR Raw DB Event with Transaction Id : "+rawDBEvent.getTransactionKeyValue());
        
        if (DBOperation.INSERT.equals(rawDBEvent.getDbRawEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateInsertRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getDbRawEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateDeleteRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getDbRawEventOperationConfig().getDbOperation())) {
            rawDBEvent = populateUpdateRawDBEventData(rawDBEvent);
        }
        return rawDBEvent;
    }

    public RawDBEvent populateInsertRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" INSERT RAW DB EVENT ");
        return rawDBEvent;
    }

    public RawDBEvent populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" DELETE RAW DB EVENT ");
        return rawDBEvent;
    }

    public RawDBEvent populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.info(" UPDATE RAW DB EVENT ");
        
        DBRawEventTableLog tableLog = rawDBEvent.getDbRawEventTableLog();
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
            DBRawEventTableLog dbRawEventTableLog,
            Object transactionKeyValue) {
        return rawDBEventDao.getRawEventDataOnTransactionId(dbRawEventTableLog, transactionKeyValue);
    }

    private Map<String, Object> getUniqueKeyValuesMap(
            Map<String, Object> rawDbEventDataMap,
            DBRawEventTableLog dbRawEventTableLog) {
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
