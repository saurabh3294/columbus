package com.proptiger.data.event.service;

import java.util.ArrayList;
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
                dbRawEventTableConfig.getHostName(),
                dbRawEventTableConfig.getDbName(),
                dbRawEventTableConfig.getTableName(),
                dbRawEventTableConfig.getDateAttributeName(),
                dbRawEventTableConfig.getDateAttributeValue());

        for (Map<String, Object> rawDBEventMap : rawDBEventDataList) {

            DBOperation dbOperation = DBOperation.valueOf((String) rawDBEventMap.get(DBRawEventTableConfig
                    .getDbOperationAttributeName()));

            RawDBEvent rawDBEvent = new RawDBEvent();
            rawDBEvent.setHostName(dbRawEventTableConfig.getHostName());
            rawDBEvent.setDbName(dbRawEventTableConfig.getDbName());
            rawDBEvent.setTableName(dbRawEventTableConfig.getTableName());
            rawDBEvent.setDbOperation(dbOperation);
            rawDBEvent.setNewDBValueMap(rawDBEventMap);
            rawDBEvent.setIdName(dbRawEventTableConfig.getPrimaryKeyName());
            rawDBEvent.setIdValue(rawDBEventMap.get(dbRawEventTableConfig.getPrimaryKeyName()));

            rawDBEventList.add(rawDBEvent);
        }

        return rawDBEventList;
    }
    
}
