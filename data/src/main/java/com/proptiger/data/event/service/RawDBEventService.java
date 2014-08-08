package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.RawDBEventDao;

@Service
public class RawDBEventService {

    @Autowired
    private RawDBEventDao rawDBEventDao;

    public List<RawDBEvent> getRawDBEvents(DBRawEventTableConfig dbRawEventTableConfig) {

        List<RawDBEvent> rawDBEventList = new ArrayList<RawDBEvent>();
        DBRawEventTableLog tableLog = dbRawEventTableConfig.getDbRawEventTableLog();

        List<Map<String, Object>> rawDBEventDataList = rawDBEventDao.getRawDBEventByTableNameAndId(tableLog);

        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {

            DBOperation dbOperation = DBOperation.getDBOperationEnum((String) rawDBEventMap.get(DBRawEventTableConfig
                    .getDbOperationAttributeName()));

            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setDbRawEventTableLog(tableLog);
            rawDBEvent.setDbRawEventOperationConfig(dbRawEventTableConfig.getDbRawEventOperationConfig(dbOperation));
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setPrimaryKeyValue(rawDBEventMap.get(tableLog.getPrimaryKeyName()));
            rawDBEvent.setTransactionKeyValue(rawDBEventMap.get(tableLog.getTransactionKeyName()));
            rawDBEvent.setTransactionDate((Date) rawDBEventMap.get(tableLog.getDateAttributeName()));
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
