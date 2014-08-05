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
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.EventTypeMappingDao;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.service.LocalityService;

@Service
public class EventGeneratedService {
    private static Logger     logger = LoggerFactory.getLogger(LocalityService.class);

    @Autowired
    private EventGeneratedDao eventGeneratedDao;

    @Autowired
    private EventTypeMappingDao dbEventMappingDao;
    
    @Autowired
    private EventTypeService eventTypeService;

    public void persistEvents(List<EventGenerated> eventGenerateds) {
        eventGeneratedDao.save(eventGenerateds);
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
    // TODO to handle the status of update queries. Currently, reverting them back to their old value.
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
                        + eventGenerated.getEventStatus()+". The number Of rows affected : "+numberOfRowsAffected);
                // Row was not updated.
                if(numberOfRowsAffected < 1){
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
        List<EventType> eventTypeList;

        if (DBOperation.INSERT.equals(rawDBEvent.getDbOperation())) {
            eventTypeList = dbEventMappingDao.getEventTypesForInsertDBOperation(
                    rawDBEvent.getHostName(),
                    rawDBEvent.getDbName(),
                    rawDBEvent.getTableName());
            generateEvents(rawDBEvent, eventTypeList, null);
        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getDbOperation())) {
            eventTypeList = dbEventMappingDao.getEventTypesForDeleteDBOperation(
                    rawDBEvent.getHostName(),
                    rawDBEvent.getDbName(),
                    rawDBEvent.getTableName());
            generateEvents(rawDBEvent, eventTypeList, null);
        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getDbOperation())) {
            for (String attributeName : rawDBEvent.getDbValueMap().keySet()) {
                eventTypeList = dbEventMappingDao.getEventTypesForUpdateDBOperation(
                        rawDBEvent.getHostName(),
                        rawDBEvent.getDbName(),
                        rawDBEvent.getTableName(),
                        attributeName);
                generateEvents(rawDBEvent, eventTypeList, attributeName);
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
            payload.setPrimaryKeyName(rawDBEvent.getIdName());
            payload.setPrimaryKeyValue(rawDBEvent.getIdValue());
            payload.populatePayloadValues(rawDBEvent, attributeName);

            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGeneratedList.add(eventGenerated);
        }

        return eventGeneratedList;
    }

    private void setEventTypesOnListEventGenerated(List<EventGenerated> listEventGenerated){
        for(EventGenerated eventGenerated: listEventGenerated){
            setEventTypeOnEventGenerated(eventGenerated);
        }
    }
    
    private void setEventTypeOnEventGenerated(EventGenerated eventGenerated){
        EventType eventType = eventTypeService.getEventTypeByEventTypeId(eventGenerated.getEventTypeId());
        eventGenerated.setEventType(eventType);
    }
}
