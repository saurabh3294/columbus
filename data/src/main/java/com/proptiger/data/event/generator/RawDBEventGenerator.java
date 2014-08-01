package com.proptiger.data.event.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.service.EventTypeMappingService;
import com.proptiger.data.event.service.RawDBEventService;
import com.proptiger.data.event.service.TableDataMappingService;

/**
 * Generates the Raw Events from DB
 * 
 * @author sahil
 * 
 */

public class RawDBEventGenerator {

    private EventTypeMappingService eventTypeMappingService;
    private TableDataMappingService tableDataMappingService;
    private RawDBEventService       rawDBEventService;

    public List<RawDBEvent> getRawDBEvents() {

        List<RawDBEvent> finalRawDBEventList = new ArrayList<RawDBEvent>();
        List<DBRawEventTableConfig> dbRawEventTableConfigs = eventTypeMappingService.getDBRawEventTableConfigs();
        dbRawEventTableConfigs = tableDataMappingService.polulateLastAccessedDate(dbRawEventTableConfigs);

        for (DBRawEventTableConfig dbRawEventTableConfig : dbRawEventTableConfigs) {
            List<RawDBEvent> rawDBEvents = rawDBEventService.getRawDBEvents(
                    dbRawEventTableConfig.getTableName(),
                    dbRawEventTableConfig.getDateAttributeName(),
                    dbRawEventTableConfig.getDateAttributeValue());

            finalRawDBEventList.addAll(rawDBEvents);
            dbRawEventTableConfig.setDateAttributeValue(getLastAccessedDate(
                    rawDBEvents,
                    dbRawEventTableConfig.getDateAttributeName()));
        }

        tableDataMappingService.updateTableDateMap(dbRawEventTableConfigs);
        return finalRawDBEventList;
    }

    private String getLastAccessedDate(List<RawDBEvent> rawDBEvents, String dateAttributeName) {
        Date lastAccessedDate = null;
        for (RawDBEvent rawDBEvent : rawDBEvents) {
            // TODO: Get the new date from the map
            Date rawDBEventDate = (Date) rawDBEvent.getDbValueMap().get(dateAttributeName);
            if (lastAccessedDate == null || lastAccessedDate.before(rawDBEventDate)) {
                lastAccessedDate = rawDBEventDate;
            }
        }
        return lastAccessedDate.toString();
    }

    public void populateRawDBEventData(RawDBEvent rawDBEvent) {
        if (DBOperation.INSERT.equals(rawDBEvent.getDbOperation())) {
            populateInsertRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getDbOperation())) {
            populateDeleteRawDBEventData(rawDBEvent);
        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getDbOperation())) {
            populateUpdateRawDBEventData(rawDBEvent);
        }
    }

    public void populateInsertRawDBEventData(RawDBEvent rawDBEvent) {

    }

    public void populateDeleteRawDBEventData(RawDBEvent rawDBEvent) {

    }

    public void populateUpdateRawDBEventData(RawDBEvent rawDBEvent) {

    }
}
