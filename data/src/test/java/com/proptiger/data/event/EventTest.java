package com.proptiger.data.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.event.service.RawEventToEventTypeMappingService;
import com.proptiger.data.service.AbstractTest;

public class EventTest extends AbstractTest{
    @Autowired
    private RawEventToEventTypeMappingService rawEventToEventTypeMappingService;
    
//    @Test
//    public void testMap(){
//        rawEventToEventTypeMappingService.constructDbConfig();
//    }
}
