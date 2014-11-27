package com.proptiger.data.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.event.service.RawDBEventService;
import com.proptiger.data.event.service.RawEventToEventTypeMappingService;
import com.proptiger.data.mocker.EventMockerService;
import com.proptiger.data.service.AbstractTest;

public class EventGeneratedServiceTest extends AbstractTest {

    @Autowired
    private EventGeneratedService             eventGeneratedService;

    @Autowired
    private RawDBEventService                 rawDBEventService;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private EventMockerService                eventMockerService;

    @Test
    public void testGenerateEventFromInsertRawDBEvent() {
        RawDBEvent rawDBEvent = eventMockerService.getMockInsertRawDBEvent();
        List<EventGenerated> eventGeneratedList = eventGeneratedService.generateEventFromRawDBEvent(rawDBEvent);
        Assert.assertEquals(eventGeneratedList.size(), rawDBEvent.getRawDBEventOperationConfig().getListEventTypes()
                .size());
    }

}