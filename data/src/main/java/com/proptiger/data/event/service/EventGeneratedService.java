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
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.event.util.DBEventMapper;
import com.proptiger.data.model.event.payload.DefaultEventTypePayload;
import com.proptiger.data.service.LocalityService;

@Service
public class EventGeneratedService {
    private static Logger     logger = LoggerFactory.getLogger(LocalityService.class);

    @Autowired
    private EventGeneratedDao eventGeneratedDao;

    @Autowired
    private DBEventMapper     dbEventMapper;

    public void persistEvents(List<EventGenerated> eventGenerateds) {
        eventGeneratedDao.save(eventGenerateds);
    }

    public List<EventGenerated> getRawEvents() {
        return eventGeneratedDao.findByStatusOrderByCreatedDateAsc(EventGenerated.EventStatus.Raw.name());
    }

    public List<EventGenerated> getProcessedEvents() {
        return eventGeneratedDao.findByStatusAndExpiryDateLessThanEqualOrderByCreatedDateAsc(
                EventGenerated.EventStatus.Processed.name(),
                new Date());
    }

    public List<EventGenerated> getProcessedEventsToBeMerged() {
        return eventGeneratedDao.findByStatusAndExpiryDateGreaterThanOrderByCreatedDateAsc(
                EventGenerated.EventStatus.Processed.name(),
                new Date());
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
                        .getEventStatus().name(), entry.getKey().name(), eventGenerated.getId());
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
            eventTypeList = dbEventMapper.getEventTypesForInsertDBOperation(
                    rawDBEvent.getHostName(),
                    rawDBEvent.getDbName(),
                    rawDBEvent.getTableName());

        }
        else if (DBOperation.DELETE.equals(rawDBEvent.getDbOperation())) {
            eventTypeList = dbEventMapper.getEventTypesForDeleteDBOperation(
                    rawDBEvent.getHostName(),
                    rawDBEvent.getDbName(),
                    rawDBEvent.getTableName());

        }
        else if (DBOperation.UPDATE.equals(rawDBEvent.getDbOperation())) {
            eventTypeList = dbEventMapper.getEventTypesForUpdateDBOperation(
                    rawDBEvent.getHostName(),
                    rawDBEvent.getDbName(),
                    rawDBEvent.getTableName(),
                    rawDBEvent.getDbValueMap().keySet());
        }
        else {
            eventTypeList = new ArrayList<EventType>();
        }

        for (EventType eventType : eventTypeList) {
            DefaultEventTypePayload payload = new DefaultEventTypePayload();
            // payload.setIdMap(rawDBEvent.getIdMap());

            // TODO: set old and new value
            // payload.setOldValue(oldValue);
            // payload.setNewValue(newValue);

            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGeneratedList.add(eventGenerated);
        }

        return eventGeneratedList;
    }

}
