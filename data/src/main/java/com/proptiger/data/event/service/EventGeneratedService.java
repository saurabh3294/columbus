package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventAttributeConfig;
import com.proptiger.data.event.generator.model.DBRawEventOperationConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.DBRawEventTableLogDao;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.service.LocalityService;

@Service
public class EventGeneratedService {
    private static Logger           logger = LoggerFactory.getLogger(LocalityService.class);

    @Autowired
    private EventGeneratedDao       eventGeneratedDao;

    @Autowired
    private EventTypeMappingService eventTypeMappingService;

    @Autowired
    private DBRawEventTableLogDao   dbRawEventTableLogDao;
	
	@Autowired
    private EventTypeService 		eventTypeService;

    // TODO: Make this transactional
    public void persistEvents(List<EventGenerated> eventGenerateds, DBRawEventTableLog dbRawEventTableLog) {
        eventGeneratedDao.save(eventGenerateds);
        dbRawEventTableLogDao.updateDateAttributeValueById(
                dbRawEventTableLog.getId(),
                dbRawEventTableLog.getDateAttributeValue());
    }

    public List<EventGenerated> getRawEvents() {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao.findByEventStatusOrderByCreatedDateAsc(EventGenerated.EventStatus.Raw);
        setEventTypesOnListEventGenerated(listEventGenerateds);
        return listEventGenerateds;
    }

    public List<EventGenerated> getProcessedEvents() {
        List<EventGenerated> listEventGenerateds =  eventGeneratedDao.findByEventStatusAndExpiryDateLessThanEqualOrderByCreatedDateAsc(
                EventGenerated.EventStatus.Processed,
                new Date());
        setEventTypesOnListEventGenerated(listEventGenerateds);
        
        return listEventGenerateds;
    }

    public List<EventGenerated> getProcessedEventsToBeMerged() {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao.findByEventStatusAndExpiryDateGreaterThanOrderByCreatedDateAsc(
                EventGenerated.EventStatus.Processed,
                new Date());
        
        setEventTypesOnListEventGenerated(listEventGenerateds);
        
        return listEventGenerateds;
    }
    
    public Integer getRawEventCount() {
        return eventGeneratedDao.getEventCountByEventStatus(EventStatus.Raw);
    }

    @Transactional
    // TODO to handle the status of update queries. Currently, reverting them
    // back to their old value.
    public void updateEventsOnOldEventStatus(Map<EventStatus, List<EventGenerated>> updateEventGeneratedByOldValue) {
        Integer numberOfRowsAffected;
        for (Map.Entry<EventStatus, List<EventGenerated>> entry : updateEventGeneratedByOldValue.entrySet()) {
            for (EventGenerated eventGenerated : entry.getValue()) {
                numberOfRowsAffected = eventGeneratedDao.updateEventStatusByIdAndOldStatus(eventGenerated
                        .getEventStatus(), entry.getKey().name(), eventGenerated.getId());
                logger.info("Event with Id" + eventGenerated.getId()
                        + " was being updated from Old Status : "
                        + entry.getKey()
                        + " to New Status : "
                        + eventGenerated.getEventStatus()
                        + ". The number Of rows affected : "
                        + numberOfRowsAffected);
                // Row was not updated.
                if (numberOfRowsAffected < 1) {
                    // reverting the changes in the model.
                    eventGenerated.setEventStatus(entry.getKey());
                }
            }
        }
    }

    public Iterable<EventGenerated> saveOrUpdateEvents(Iterable<EventGenerated> events) {
        return eventGeneratedDao.save(events);
    }

    public EventGenerated saveOrUpdateOneEvent(EventGenerated event) {
        return eventGeneratedDao.save(event);
    }

    public List<EventGenerated> generateEventFromRawDBEvent(RawDBEvent rawDBEvent) {
        List<EventGenerated> eventGeneratedList = new ArrayList<EventGenerated>();
        DBRawEventOperationConfig dbRawEventOperationConfig = rawDBEvent.getDbRawEventOperationConfig();

        if (DBOperation.INSERT.equals(dbRawEventOperationConfig.getDbOperation())) {
            generateEvents(rawDBEvent, dbRawEventOperationConfig.getListEventTypes(), null);
        }
        else if (DBOperation.DELETE.equals(dbRawEventOperationConfig.getDbOperation())) {
            generateEvents(rawDBEvent, dbRawEventOperationConfig.getListEventTypes(), null);
        }
        else if (DBOperation.UPDATE.equals(dbRawEventOperationConfig.getDbOperation())) {
            for (String attributeName : rawDBEvent.getNewDBValueMap().keySet()) {
                DBRawEventAttributeConfig dbRawEventAttributeConfig = dbRawEventOperationConfig
                        .getDBRawEventAttributeConfig(attributeName);
                if (dbRawEventAttributeConfig != null) {
                    generateEvents(rawDBEvent, dbRawEventAttributeConfig.getListEventTypes(), attributeName);
                }
            }
        }

        return eventGeneratedList;
    }

    private List<EventGenerated> generateEvents(
            RawDBEvent rawDBEvent,
            List<EventType> eventTypeList,
            String attributeName) {

        List<EventGenerated> eventGeneratedList = new ArrayList<EventGenerated>();

        for (EventType eventType : eventTypeList) {
            EventTypePayload payload = eventType.getEventTypeConfig().getEventTypePayloadObject();
            payload.setTransactionKeyName(rawDBEvent.getDbRawEventTableLog().getTransactionKeyName());
            payload.setTransactionId(rawDBEvent.getTransactionKeyValue());
            payload.setPrimaryKeyName(rawDBEvent.getDbRawEventTableLog().getPrimaryKeyName());
            payload.setPrimaryKeyValue(rawDBEvent.getPrimaryKeyValue());
            payload.setTransactionDateKeyName(rawDBEvent.getDbRawEventTableLog().getDateAttributeName());
            payload.setTransactionDateKeyValue(rawDBEvent.getTransactionDate());
            payload.populatePayloadValues(rawDBEvent, attributeName);

            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGeneratedList.add(eventGenerated);
        }

        return eventGeneratedList;
    }

}
