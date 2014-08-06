package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.RawDBEventDao;

@Service
public class RawDBEventService {

    @Autowired
    private RawDBEventDao rawDBEventDao;

    public List<RawDBEvent> getRawDBEvents(DBRawEventTableConfig dbRawEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();

        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndDate(
                dbRawEventTableConfig.getDbRawEventTableLog().getHostName(),
                dbRawEventTableConfig.getDbRawEventTableLog().getDbName(),
                dbRawEventTableConfig.getDbRawEventTableLog().getTableName(),
                dbRawEventTableConfig.getDbRawEventTableLog().getDateAttributeName(),
                dbRawEventTableConfig.getDbRawEventTableLog().getDateAttributeValue());

        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {

            DBOperation dbOperation = DBOperation.getDBOperationEnum((String) rawDBEventMap.get(DBRawEventTableConfig
                    .getDbOperationAttributeName()));

            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setDbRawEventTableLog(dbRawEventTableConfig.getDbRawEventTableLog());
            rawDBEvent.setDbRawEventOperationConfig(dbRawEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(dbRawEventTableConfig.getDbRawEventTableLog()
                    .getPrimaryKeyName()));
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(dbRawEventTableConfig.getDbRawEventTableLog()
                    .getTransactionKeyName()));
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(dbRawEventTableConfig.getDbRawEventTableLog()
                    .getDateAttributeName()));
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
        Map<String, Object> oldRawEventDataMap = rawDBEventDao.getOldRawDBEvent(
                rawDBEvent.getDbRawEventTableLog().getDbName(),
                rawDBEvent.getDbRawEventTableLog().getDbName(),
                rawDBEvent.getDbRawEventTableLog().getTableName(),
                rawDBEvent.getDbRawEventTableLog().getTransactionKeyName(),
                rawDBEvent.getTransactionKeyValue(),
                rawDBEvent.getDbRawEventTableLog().getPrimaryKeyName(),
                rawDBEvent.getPrimaryKeyValue());

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

}
