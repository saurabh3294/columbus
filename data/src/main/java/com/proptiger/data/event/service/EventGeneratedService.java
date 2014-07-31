package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.DBRawEventToEventTypeMappingDao;
import com.proptiger.data.event.repo.EventGeneratedDao;

@Service
public class EventGeneratedService {

    @Autowired
    private EventGeneratedDao eventGeneratedDao;

    @Autowired
    private DBRawEventToEventTypeMappingDao dbEventMappingDao;

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
            payload.setIdName(rawDBEvent.getIdName());
            payload.setIdValue(rawDBEvent.getIdValue());
            payload.populatePayloadValues(rawDBEvent, attributeName);

            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGeneratedList.add(eventGenerated);
        }

        return eventGeneratedList;
    }

}
