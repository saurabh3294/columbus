package com.proptiger.data.event.generator;

import java.util.ArrayList;
import java.util.Date;
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
    private RawDBEventService       rawDBEventService;

    public List<RawDBEvent> getRawDBEvents() {

        List<RawDBEvent> finalRawDBEventList = new ArrayList<RawDBEvent>();
        List<DBRawEventTableConfig> dbRawEventTableConfigs = eventTypeMappingService.getDbRawEventTableConfigs();

        for (DBRawEventTableConfig dbRawEventTableConfig : dbRawEventTableConfigs) {
            List<RawDBEvent> rawDBEvents = rawDBEventService.getRawDBEvents(dbRawEventTableConfig);
            finalRawDBEventList.addAll(rawDBEvents);

            // Updating the dateAttribute value after generating the rawDBEvents
            DBRawEventTableLog dbRawEventTableLog = dbRawEventTableConfig.getDbRawEventTableLog();
            dbRawEventTableLog.setDateAttributeValue(getLastAccessedDate(rawDBEvents, dbRawEventTableConfig
                    .getDbRawEventTableLog().getDateAttributeName()));
        }

        return finalRawDBEventList;
    }

    private Date getLastAccessedDate(List<RawDBEvent> rawDBEvents, String dateAttributeName) {
        Date lastAccessedDate = new Date();
        for (RawDBEvent rawDBEvent : rawDBEvents) {
            Date rawDBEventDate = (Date) rawDBEvent.getNewDBValueMap().get(dateAttributeName);
            if (lastAccessedDate == null || lastAccessedDate.before(rawDBEventDate)) {
                lastAccessedDate = rawDBEventDate;
            }
        }
        return lastAccessedDate;
    }

}
