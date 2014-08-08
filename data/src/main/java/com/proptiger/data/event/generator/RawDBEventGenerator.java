package com.proptiger.data.event.generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.service.RawEventToEventTypeMappingService;
import com.proptiger.data.event.service.RawDBEventService;

/**
 * Generates the Raw Events from DB
 * 
 * @author sahil
 * 
 */

@Service
public class RawDBEventGenerator {

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    public List<RawDBEvent> getRawDBEvents() {

        List<RawDBEvent> finalRawDBEventList = new ArrayList<RawDBEvent>();
        List<DBRawEventTableConfig> dbRawEventTableConfigs = eventTypeMappingService.getDbRawEventTableConfigs();

        for (DBRawEventTableConfig dbRawEventTableConfig : dbRawEventTableConfigs) {
            List<RawDBEvent> rawDBEvents = rawDBEventService.getRawDBEvents(dbRawEventTableConfig);
            finalRawDBEventList.addAll(rawDBEvents);

            // Updating the last accessed Transaction Key after generating the
            // rawDBEvents
            if (!rawDBEvents.isEmpty()) {
                DBRawEventTableLog dbRawEventTableLog = dbRawEventTableConfig.getDbRawEventTableLog();
                dbRawEventTableLog.setLastTransactionKeyValue(getLastAccessedTransactionId(
                        rawDBEvents,
                        dbRawEventTableConfig.getDbRawEventTableLog().getTransactionKeyName()));
            }
        }

        return finalRawDBEventList;
    }

    private Long getLastAccessedTransactionId(List<RawDBEvent> rawDBEvents, String transactionKeyName) {
        Long lastAccessedId = null;
        for (RawDBEvent rawDBEvent : rawDBEvents) {
            Long transactionKey = (Long) rawDBEvent.getNewDBValueMap().get(transactionKeyName);
            if (lastAccessedId == null || lastAccessedId < transactionKey) {
                lastAccessedId = transactionKey;
            }
        }
        return lastAccessedId;
    }

}
