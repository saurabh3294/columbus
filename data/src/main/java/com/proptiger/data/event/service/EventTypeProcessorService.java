package com.proptiger.data.event.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawEventToEventTypeMapping;
import com.proptiger.data.event.repo.EventTypeProcessorDao;
import com.proptiger.data.util.DateUtil;

@Service
public class EventTypeProcessorService {

    @Autowired
    private EventTypeProcessorDao             eventTypeProcessorDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    public Double getPriceChangeOldValue(EventGenerated eventGenerated, Date effeDate) {
        // Getting the First Day of the Month.
        Date eventCreatedDate = eventGenerated.getEventTypePayload().getTransactionDateKeyValue();
        Date firstDayOfMonth = DateUtil.getFirstDayOfCurrentMonth(eventCreatedDate);

        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        DBRawEventTableLog dbRawEventTableLog = eventTypeMapping.getDbRawEventTableLog();

        Map<String, Object> filtersMap = dbRawEventTableLog.getFilterMap();
        // getting the old value of the 1 month before latest value.
        filtersMap.put("effective_date", DateUtil.shiftMonths(effeDate, -1));

        Double OldPrice = (Double) eventTypeProcessorDao.getOldValueOfEventTypeOnLastMonth(
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getTableName(),
                dbRawEventTableLog.getPrimaryKeyName(),
                eventGenerated.getEventTypePayload().getPrimaryKeyValue(),
                eventTypeMapping.getAttributeName(),
                dbRawEventTableLog.getTransactionKeyName(),
                eventGenerated.getEventTypePayload().getTransactionId(),
                dbRawEventTableLog.getDateAttributeName(),
                firstDayOfMonth,
                dbRawEventTableLog.getFilterMap());

        return OldPrice;
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
