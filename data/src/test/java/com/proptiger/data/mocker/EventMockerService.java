package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.RawDBEventOperationConfig;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.EventTypeConfig;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.RawEventTableDetails;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.model.payload.EventTypePayload;

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
        EventTypeConfig config = new EventTypeConfig();
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
