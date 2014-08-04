package com.proptiger.data.event.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventTypeMapping;
import com.proptiger.data.event.repo.EventTypeProcessorDao;

@Service
public class EventTypeProcessorService {

    @Autowired
    private EventTypeProcessorDao   eventTypeProcessorDao;

    @Autowired
    private EventTypeMappingService eventTypeMappingService;

    public Double getPriceChangeOldValue(EventGenerated eventGenerated) {
        // Getting the First Day of the Month.
        Date eventCreatedDate = eventGenerated.getEventCreatedDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(eventCreatedDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);

        EventTypeMapping eventTypeMapping = eventTypeMappingService.getMappingByEventTypeId(eventGenerated
                .getEventType().getId());
        DBRawEventTableLog dbRawEventTableLog = eventTypeMapping.getDbRawEventTableLog();

        Double OldPrice = (Double)eventTypeProcessorDao.getOldValueOfEventTypeOnLastMonth(
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getHostName(),
                dbRawEventTableLog.getTableName(),
                dbRawEventTableLog.getPrimaryKeyName(),
                eventGenerated.getEventTypePayload().getIdValue(),
                eventTypeMapping.getAttributeName(),
                dbRawEventTableLog.getTransactionKeyName(),
                eventGenerated.getEventTypePayload().getId(),
                dbRawEventTableLog.getDateAttributeName(),
                cal.getTime());

        return OldPrice;
    }
}
