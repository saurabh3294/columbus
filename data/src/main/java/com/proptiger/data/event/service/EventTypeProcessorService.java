package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.util.DateUtil;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawEventToEventTypeMapping;
import com.proptiger.data.event.repo.EventTypeProcessorDao;

@Service
public class EventTypeProcessorService {
    private static Logger                     logger = LoggerFactory.getLogger(EventTypeProcessorService.class);

    @Autowired
    private EventTypeProcessorDao             eventTypeProcessorDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    public Double getPriceChangeOldValue(EventGenerated eventGenerated, Date effeDate) {
        logger.info(" Getting the Old Price Value for Price Change Event. " + eventGenerated.getEventTypePayload()
                .getTransactionId());

        // Getting the First Day of the Month.
        Date eventCreatedDate = eventGenerated.getEventTypePayload().getTransactionDateKeyValue();
        Date firstDayOfMonth = DateUtil.getFirstDayOfCurrentMonth(eventCreatedDate);

        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        DBRawEventTableLog dbRawEventTableLog = eventTypeMapping.getDbRawEventTableLog();

        Map<String, List<Object>> filtersMap = dbRawEventTableLog.getFilterMap();

        // getting the old value of the 1 month before latest value.
        List<Object> list = new ArrayList<Object>();
        logger.debug("effectiveDate: " + effeDate);
        list.add(DateUtil.shiftMonths(effeDate, -1));
        filtersMap.put("effective_date", list);

        Number OldPrice = (Number) eventTypeProcessorDao.getOldValueOfEventTypeOnLastMonth(
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getDbName(),
                dbRawEventTableLog.getTableName(),
                dbRawEventTableLog.getPrimaryKeyName(),
                eventGenerated.getEventTypePayload().getPrimaryKeyValue(),
                eventTypeMapping.getAttributeName(),
                dbRawEventTableLog.getTransactionKeyName(),
                eventGenerated.getEventTypePayload().getTransactionId(),
                dbRawEventTableLog.getDateAttributeName(),
                firstDayOfMonth,
                dbRawEventTableLog.getFilterMap());

        if (OldPrice != null)
            return OldPrice.doubleValue();
        else
            return null;
    }

    public Map<String, Object> getEventTransactionRow(EventGenerated eventGenerated) {
        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        DBRawEventTableLog dbRawEventTableLog = eventTypeMapping.getDbRawEventTableLog();
        Map<String, Object> transactionRow = rawDBEventService.getRawEventTransactionRow(
                dbRawEventTableLog,
                eventGenerated.getEventTypePayload().getTransactionId());

        return transactionRow;
    }
}
