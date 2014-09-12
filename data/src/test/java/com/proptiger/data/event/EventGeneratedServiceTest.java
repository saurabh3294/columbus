package com.proptiger.data.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.event.generator.model.DBRawEventTableConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.event.service.RawDBEventService;
import com.proptiger.data.event.service.RawEventToEventTypeMappingService;
import com.proptiger.data.service.AbstractTest;

public class EventGeneratedServiceTest extends AbstractTest {

    @Autowired
    private EventGeneratedService             eventGeneratedService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

//    @Test
//    public void testGenerateEventFromRawDBEvent() {
//        List<DBRawEventTableConfig> dbRawEventTableConfigs = eventTypeMappingService.getDbRawEventTableConfigs();
//        Assert.assertNotNull(dbRawEventTableConfigs);
//        Assert.assertEquals(dbRawEventTableConfigs.size(), 2);
//
//        Map<String, DBRawEventTableConfig> dbRawEventTableConfigMap = new HashMap<String, DBRawEventTableConfig>();
//        for (DBRawEventTableConfig dbRawEventTableConfig : dbRawEventTableConfigs) {
//            DBRawEventTableLog dbRawEventTableLog = dbRawEventTableConfig.getDbRawEventTableLog();
//            String key = dbRawEventTableLog.getDbName() + "." + dbRawEventTableLog.getTableName();
//            dbRawEventTableConfigMap.put(key, dbRawEventTableConfig);
//        }
//
//        DBRawEventTableConfig dbRawEventTableConfig;
//        List<RawDBEvent> rawDBEvents = new ArrayList<RawDBEvent>();
//
//        dbRawEventTableConfig = dbRawEventTableConfigMap.get("cms._t_listing_prices");
//        Assert.assertNotNull(dbRawEventTableConfig);
//        rawDBEvents.addAll(rawDBEventService.getRawDBEvents(dbRawEventTableConfig));
//        Assert.assertEquals(rawDBEvents.size(), 2);
//
//        dbRawEventTableConfig = dbRawEventTableConfigMap.get("proptiger._t_Image");
//        Assert.assertNotNull(dbRawEventTableConfig);
//        rawDBEvents.addAll(rawDBEventService.getRawDBEvents(dbRawEventTableConfig));
//        Assert.assertEquals(rawDBEvents.size(), 4);
//
//        List<EventGenerated> actualEventGeneratedList = new ArrayList<EventGenerated>();
//        for (RawDBEvent rawDBEvent : rawDBEvents) {
//            rawDBEvent = rawDBEventService.populateRawDBEventData(rawDBEvent);
//            actualEventGeneratedList.addAll(eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent));
//        }
//
//        List<EventGenerated> expectedEventGeneratedList = eventGeneratedService.getRawEvents();
//        Assert.assertEquals(actualEventGeneratedList.size(), expectedEventGeneratedList.size());
//    }

}
