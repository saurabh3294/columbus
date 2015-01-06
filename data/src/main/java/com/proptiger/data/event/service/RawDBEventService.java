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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.event.DBOperation;
import com.proptiger.core.model.event.RawDBEvent;
import com.proptiger.core.model.event.RawEventTableDetails;
import com.proptiger.core.model.event.generator.model.RawDBEventTableConfig;
import com.proptiger.data.event.enums.EventAllAttributeName;
import com.proptiger.data.event.repo.RawDBEventDao;
import com.proptiger.data.util.Serializer;

@Service
public class RawDBEventService {

    private static Logger                     logger = LoggerFactory.getLogger(RawDBEventService.class);

    @Autowired
    private RawDBEventDao                     rawDBEventDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Value("${transaction.maxCount}")
    private Integer                           MAX_TRANSACTION_COUNT;

    /**
     * Generates list of RawDBEvents corresponding to a particular
     * RawDBEventTableConfig. RawDBEventTableConfig represents a particular
     * table of a particular DB. RawDBEvent is generated corresponding to an
     * event i.e. Insert/Update/Delete from the trigger tables.
     * 
     * @param rawDBEventTableConfig
     * @return list of RawDBEvents
     */
    public List<RawDBEvent> getRawDBEvents(RawDBEventTableConfig rawDBEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();
        RawEventTableDetails tableLog = rawDBEventTableConfig.getRawEventTableDetails();

        if (tableLog.getLastTransactionKeyValue() == null) {
            Map<String, Object> latestTransaction = rawDBEventDao.getLatestTransaction(tableLog);
            Long transactionId = Long.parseLong(latestTransaction.get(tableLog.getTransactionKeyName()).toString());
            eventTypeMappingService.updateLastAccessedTransactionId(tableLog, transactionId);
            logger.info("Retrieving transactions for RawEventTableDetailsID: " + tableLog.getId()
                    + " Table: "
                    + tableLog.getTableName()
                    + " for the first time. Hence, setting last accessed transaction id to latest transaction id i.e. "
                    + transactionId);
            return rawDBEventList;
        }

        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndId(
                tableLog,
                MAX_TRANSACTION_COUNT);
        logger.debug("Retrieved data: " + rawDBEventDataList
                + " for the RawEventTableDetailsID: "
                + tableLog.getId()
                + " Table: "
                + tableLog.getTableName());

        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {
            DBOperation dbOperation = DBOperation.getDBOperationEnum((Character) rawDBEventMap
                    .get(RawDBEventTableConfig.getDbOperationAttributeName()));

            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setRawEventTableDetails(tableLog);
            rawDBEvent.setRawDBEventOperationConfig(rawDBEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(tableLog.getPrimaryKeyName()).toString());
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(tableLog.getTransactionKeyName()).toString());
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(tableLog.getDateAttributeName()));
            rawDBEvent.setUniqueKeyValuesMap(getUniqueKeyValuesMap(rawDBEventMap, tableLog));
            rawDBEventList.add(rawDBEvent);
        }

        return rawDBEventList;
    }

    /**
     * Populate remaining data in RawDBEvent such as Old Value for an Update
     * event.
     * 
     * @param rawDBEvent
     * @return
     */
    public RawDBEvent populateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug("Populating RawDBEvent data with TransactionId : " + rawDBEvent.getTransactionKeyValue());

        if (rawDBEvent.getRawDBEventOperationConfig() == null) {
            logger.debug("No operation config found for RawDBEvent with TransactionId : " + rawDBEvent
                    .getTransactionKeyValue() + " and table: " + rawDBEvent.getRawEventTableDetails().getTableName());
            return null;
        }

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

    private RawDBEvent populateInsertRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug(" INSERT RAW DB EVENT ");
        Map<String, Object> allattributes = new HashMap<String, Object>();
        allattributes.put(EventAllAttributeName.All.name(), rawDBEvent.getNewDBValueMap());
        rawDBEvent.setNewDBValueMap(allattributes);
        
        return rawDBEvent;
    }

    private RawDBEvent populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug("Populating RawDBEvent data for DELETE event with TransactionId : " + rawDBEvent
                .getTransactionKeyValue());
        return rawDBEvent;
    }

    private RawDBEvent populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {
        logger.debug("Populating RawDBEvent data for UPDATE event with TransactionId : " + rawDBEvent
                .getTransactionKeyValue());

        RawEventTableDetails tableLog = rawDBEvent.getRawEventTableDetails();

        // Getting old values for an Update RawDBEvent
        Map<String, Object> oldRawEventDataMap = rawDBEventDao.getOldRawDBEvent(
                tableLog,
                rawDBEvent.getTransactionKeyValue(),
                rawDBEvent.getPrimaryKeyValue(),
                rawDBEvent.getUniqueKeyValuesMap());

        logger.debug("Retrieved OldData: " + oldRawEventDataMap
                + " for the RawEventTableDetailsID: "
                + tableLog.getId()
                + " Table: "
                + tableLog.getTableName()
                + " TransactionID: "
                + rawDBEvent.getTransactionKeyValue());

        if (oldRawEventDataMap == null) {
            logger.error("No OldData found for the RawEventTableDetailsID: " + tableLog.getId()
                    + " Table: "
                    + tableLog.getTableName()
                    + " TransactionID: "
                    + rawDBEvent.getTransactionKeyValue());
            return null;
        }

        Map<String, Object> newRawEventDataMap = rawDBEvent.getNewDBValueMap();
        Map<String, Object> oldRawEventDataMapToSet = rawDBEvent.getOldDBValueMap();

        // Removing attributes from NewDBValueMap and OldDBValueMap that are
        // unchanged
        Iterator<Map.Entry<String, Object>> it = newRawEventDataMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();

            Object obj = oldRawEventDataMap.get(key);
            Object newObj = entry.getValue();

            /**
             * Making new Object and old objects as null. Making the comparison
             * easier as "" and null should be matched as same.
             */
            if (newObj == null) {
                newObj = "";
            }
            if (obj == null) {
                obj = "";
            }

            if (newObj.equals(obj)) {
                it.remove();
            }
            else {
                oldRawEventDataMapToSet.put(key, obj);
            }
        }

        logger.debug("Updated fields: " + Serializer.toJson(oldRawEventDataMapToSet)
                + " TransactionID: "
                + rawDBEvent.getTransactionKeyValue());

        return rawDBEvent;
    }

    /**
     * Return a TransactionRow for a given transaction id corresponding to the
     * transaction table details given in RawEventTableDetails
     * 
     * @param rawEventTableDetails
     * @param transactionKeyValue
     * @return
     */
    public Map<String, Object> getRawEventTransactionRow(
            RawEventTableDetails rawEventTableDetails,
            Object transactionKeyValue) {
        return rawDBEventDao.getRawEventDataOnTransactionId(rawEventTableDetails, transactionKeyValue);
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
                }
            }
        }
        return postFiltersMap;
    }
}
