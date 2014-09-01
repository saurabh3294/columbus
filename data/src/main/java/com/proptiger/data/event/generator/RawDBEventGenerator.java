package com.proptiger.data.event.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.service.RawDBEventService;
import com.proptiger.data.event.service.RawEventToEventTypeMappingService;

/**
 * Generates the Raw Events from DB
 * 
 * @author sahil
 * 
 */

@Service
public class RawDBEventGenerator {
    private static Logger                     logger = LoggerFactory.getLogger(RawDBEventGenerator.class);
    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    public List<RawDBEvent> getRawDBEvents() {

        List<RawDBEvent> finalRawDBEventList = new ArrayList<RawDBEvent>();
        List<DBRawEventTableConfig> dbRawEventTableConfigs = eventTypeMappingService.getDbRawEventTableConfigs();
        logger.info("Iterating " + dbRawEventTableConfigs.size() + " table configurations.");

        for (DBRawEventTableConfig dbRawEventTableConfig : dbRawEventTableConfigs) {

            List<RawDBEvent> rawDBEvents = rawDBEventService.getRawDBEvents(dbRawEventTableConfig);
            finalRawDBEventList.addAll(rawDBEvents);

            // Updating the last accessed Transaction Key after generating the
            // rawDBEvents
            // TODO to move the setting of last transaction Id at the end when
            // rows have been inserted.
            // as we are taking the configuration on static not every db call.
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
            Number number = (Number) rawDBEvent.getNewDBValueMap().get(transactionKeyName);
            Long transactionKey = number.longValue();
            if (lastAccessedId == null || lastAccessedId < transactionKey) {
                lastAccessedId = transactionKey;
            }
        }
        logger.info(" Getting the Last Transaction id " + lastAccessedId);
        return lastAccessedId;
    }

}
