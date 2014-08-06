package com.proptiger.data.event.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawEventToEventTypeMapping;
import com.proptiger.data.event.repo.EventTypeProcessorDao;

@Service
public class EventTypeProcessorService {

    @Autowired
    private EventTypeProcessorDao   eventTypeProcessorDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    public Double getPriceChangeOldValue(EventGenerated eventGenerated) {
        // Getting the First Day of the Month.
        Date eventCreatedDate = eventGenerated.getEventCreatedDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(eventCreatedDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);

        RawEventToEventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        DBRawEventTableLog dbRawEventTableLog = eventTypeMapping.getDbRawEventTableLog();

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
                cal.getTime());

        return OldPrice;
    }
}
