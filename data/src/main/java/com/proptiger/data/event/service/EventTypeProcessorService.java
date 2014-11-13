package com.proptiger.data.event.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawEventTableDetails;
import com.proptiger.data.event.model.RawEventToEventTypeMapping;
import com.proptiger.data.event.repo.EventTypeProcessorDao;

@Service
public class EventTypeProcessorService {
    private static Logger                     logger                      = LoggerFactory
                                                                                  .getLogger(EventTypeProcessorService.class);

    private static final String               PRICE_CHANGE_EFFECTIVE_DATE = "effective_date";

    @Autowired
    private EventTypeProcessorDao             eventTypeProcessorDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    public Double getPriceChangeOldValue(EventGenerated eventGenerated, Date effeDate) {
        logger.info(" Getting the Old Price Value for Price Change Event. " + eventGenerated.getEventTypePayload()
                .getTransactionId());

        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        RawEventTableDetails dbRawEventTableLog = eventTypeMapping.getRawEventTableDetails();

        Number OldPrice = (Number) eventTypeProcessorDao.getOldValueOfEventTypeOnLastMonth(
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getDbName(),
                dbRawEventTableLog.getTableName(),
                dbRawEventTableLog.getPrimaryKeyName(),
                eventGenerated.getEventTypePayload().getPrimaryKeyValue(),
                eventTypeMapping.getAttributeName(),
                dbRawEventTableLog.getTransactionKeyName(),
                eventGenerated.getEventTypePayload().getTransactionId(),
                PRICE_CHANGE_EFFECTIVE_DATE,
                effeDate,
                dbRawEventTableLog.getFilterMap());

        if (OldPrice != null)
            return OldPrice.doubleValue();
        else
            return null;
    }

    public Map<String, Object> getEventTransactionRow(EventGenerated eventGenerated) {
        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        RawEventTableDetails dbRawEventTableLog = eventTypeMapping.getRawEventTableDetails();
        Map<String, Object> transactionRow = rawDBEventService.getRawEventTransactionRow(
                dbRawEventTableLog,
                eventGenerated.getEventTypePayload().getTransactionId());

        return transactionRow;
    }
}
