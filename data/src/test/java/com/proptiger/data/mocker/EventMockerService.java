package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.core.enums.event.DBOperation;
import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventType;
import com.proptiger.core.model.event.RawDBEvent;
import com.proptiger.core.model.event.RawEventTableDetails;
import com.proptiger.core.model.event.EventGenerated.EventStatus;
import com.proptiger.core.model.event.generator.model.RawDBEventOperationConfig;
import com.proptiger.data.event.model.DefaultEventTypeConfig;

/**
 * 
 * @author sahil
 * 
 */
@Service
public class EventMockerService {
    
    public static final List<String> EVENT_TYPES = new ArrayList<String>();
    
    static {
        EVENT_TYPES.add("mockEventTypeName");
    }

    public EventGenerated getMockEventGenerated() {       
        EventType eventType = getMockEventType();
      
        EventGenerated eventGenerated = new EventGenerated();
        eventGenerated.setId(142);
        eventGenerated.setEventType(eventType);
        eventGenerated.setEventTypeId(eventType.getId());
        eventGenerated.setEventTypePayload(getMockEventTypePayload());
        eventGenerated.setEventStatus(EventStatus.Verified);
        eventGenerated.setEventTypeUniqueKey("uniqueKey");
        return eventGenerated;
    }
    
    public RawDBEvent getMockInsertRawDBEvent() {       
          
        RawEventTableDetails tableLog = new RawEventTableDetails();
        tableLog.setId(1);
        tableLog.setHostName("hostName");
        tableLog.setDateAttributeName("dateAttributeName");
        tableLog.setDbName("dbName");
        tableLog.setLastTransactionKeyValue(1L);
        tableLog.setPrimaryKeyName("primaryKeyName");
        tableLog.setTransactionKeyName("transactionKeyName");
     
        List<EventType> eventTypeList = new ArrayList<EventType>();
        eventTypeList.add(getMockEventType());
        
        RawDBEventOperationConfig operationConfig = new RawDBEventOperationConfig();
        operationConfig.setDbOperation(DBOperation.INSERT);
        operationConfig.setListEventTypes(eventTypeList);
        
        RawDBEvent rawDBEvent = new RawDBEvent();
        rawDBEvent.setRawEventTableDetails(tableLog);
        rawDBEvent.setRawDBEventOperationConfig(operationConfig);
        rawDBEvent.setTransactionKeyValue(100);
        rawDBEvent.setPrimaryKeyValue(500);
        rawDBEvent.setTransactionDate(new Date());
        return rawDBEvent;
    }

    public EventType getMockEventType() {
        DefaultEventTypeConfig config = new DefaultEventTypeConfig();
        try {
            config.setEventTypePayloadObject(config.getDataClassName().newInstance());
        }
        catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EventType eventType = new EventType();
        eventType.setId(356);
        eventType.setName(EVENT_TYPES.get(0));
        eventType.setEventTypeConfig(config);
        return eventType;
    }

    public EventTypePayload getMockEventTypePayload() {
        DefaultEventTypePayload eventTypePayload = new DefaultEventTypePayload();
        eventTypePayload.setOldValue(3000);
        eventTypePayload.setNewValue(3200);
        eventTypePayload.setTransactionKeyName("mockTxnKeyName");
        eventTypePayload.setTransactionId(38712);
        eventTypePayload.setTransactionDateKeyName("mockTxnDateName");
        eventTypePayload.setTransactionDateKeyValue(new Date());
        eventTypePayload.setPrimaryKeyName("mockPrimaryKeyName");
        eventTypePayload.setPrimaryKeyValue(923478);
        return eventTypePayload;
    }

}
