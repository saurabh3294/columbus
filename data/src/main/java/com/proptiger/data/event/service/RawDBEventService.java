package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.RawDBEventDao;

@Service
public class RawDBEventService {
    private static Logger logger = Logger.getLogger(RawDBEventService.class);

    @Autowired
    private RawDBEventDao rawDBEventDao;

    public List<RawDBEvent> getRawDBEvents(DBRawEventTableConfig dbRawEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();
        DBRawEventTableLog tableLog = dbRawEventTableConfig.getDbRawEventTableLog();

        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndId(tableLog);

        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {
            // TODO to handle the null value of the dbOperation if it is not
            // found.
            DBOperation dbOperation = DBOperation.getDBOperationEnum((Character) rawDBEventMap
                    .get(DBRawEventTableConfig.getDbOperationAttributeName()));
            logger.info(dbOperation);
            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setDbRawEventTableLog(tableLog);
            rawDBEvent.setDbRawEventOperationConfig(dbRawEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(tableLog.getPrimaryKeyName()));
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(tableLog.getTransactionKeyName()));
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(tableLog.getDateAttributeName()));
            rawDBEvent.setUniqueKeyValuesMap(getUniqueKeyValuesMap(rawDBEventMap, tableLog));
            rawDBEventList.add(rawDBEvent);
        }

        return rawDBEventList;
    }

    public RawDBEvent populateRawDBEventData(RawDBEvent rawDBEvent) {
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
        return rawDBEvent;
    }

    public RawDBEvent populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {
        return rawDBEvent;
    }

    public RawDBEvent populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {
        DBRawEventTableLog tableLog = rawDBEvent.getDbRawEventTableLog();
        Map<String, Object> oldRawEventDataMap = rawDBEventDao.getOldRawDBEvent(
                tableLog,
                rawDBEvent.getTransactionKeyValue(),
                rawDBEvent.getPrimaryKeyValue(),
                rawDBEvent.getUniqueKeyValuesMap());

        Map<String, Object> newRawEventDataMap = rawDBEvent.getOldDBValueMap();

        for (String key : rawDBEvent.getNewDBValueMap().keySet()) {
            Object obj = oldRawEventDataMap.get(key);
            if (newRawEventDataMap.get(key).equals(obj)) {
                newRawEventDataMap.remove(key);
            }
            else {
                rawDBEvent.getOldDBValueMap().put(key, obj);
            }
        }

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
                }
            }
        }

        return postFiltersMap;

    }
}
