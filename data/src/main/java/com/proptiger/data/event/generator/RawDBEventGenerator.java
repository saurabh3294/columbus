package com.proptiger.data.event.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.RawDBEventTableConfig;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.RawEventTableDetails;
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

    /**
     * Generates RawDBEvents using RawDBEventTableConfigs present in DB. Updates
     * the transaction date of last accessed transaction in the corresponding
     * RawDBEventTableConfig
     * 
     * @return list of RawDBEvents
     */
    public List<RawDBEvent> getRawDBEvents() {
        List<RawDBEvent> finalRawDBEventList = new ArrayList<RawDBEvent>();
        List<RawDBEventTableConfig> rawDBEventTableConfigs = eventTypeMappingService.getRawDBEventTableConfigs();

        logger.debug("Iterating over " + rawDBEventTableConfigs.size() + " RawDBEventTableConfigs found in DB");
        for (RawDBEventTableConfig rawDBEventTableConfig : rawDBEventTableConfigs) {
            RawEventTableDetails rawEventTableDetails = rawDBEventTableConfig.getRawEventTableDetails();

            List<RawDBEvent> rawDBEvents = rawDBEventService.getRawDBEvents(rawDBEventTableConfig);
            logger.debug("Generated " + rawDBEvents.size()
                    + " RawDBEvents using RawDBEventTableConfig with id: "
                    + rawEventTableDetails.getId()
                    + " and table: "
                    + rawEventTableDetails.getTableName());
            finalRawDBEventList.addAll(rawDBEvents);

            // Updating the last accessed Transaction Key after generating the
            // RawDBEvents
            // TODO: Move this to the end where EventGenerated is being
            // persisted to DB because we are storing this updated value in a
            // static map. Therefore, if the code fails in between then the
            // static map will still have the updated value.
            if (!rawDBEvents.isEmpty()) {
                rawEventTableDetails.setLastTransactionKeyValue(getLastAccessedTransactionId(
                        rawDBEvents,
                        rawDBEventTableConfig.getRawEventTableDetails().getTransactionKeyName()));
            }
        }

        logger.debug("Generated total " + finalRawDBEventList.size() + " RawDBEvents");
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
        logger.debug("Setting the LastAccessedTransactionID: " + lastAccessedId);
        return lastAccessedId;
    }

}
