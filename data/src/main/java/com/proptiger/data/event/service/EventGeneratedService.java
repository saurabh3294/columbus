package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.event.util.DBEventMapper;
import com.proptiger.data.model.event.payload.DefaultEventTypePayload;

@Service
public class EventGeneratedService {

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
    
    public List<EventGenerated> getProcessedEvents(){
        return eventGeneratedDao.findByStatusAndExpiryDateLessThanEqualOrderByCreatedDateAsc(EventGenerated.EventStatus.Processed.name(), new Date());
    }
    
    public List<EventGenerated> getProcessedEventsToBeMerged(){
        return eventGeneratedDao.findByStatusAndExpiryDateGreaterThanOrderByCreatedDateAsc(EventGenerated.EventStatus.Processed.name(), new Date());
    }

    public Integer getRawEventCount() {
        return eventGeneratedDao.getEventCountByEventStatus(EventStatus.Raw);
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
            payload.setIdMap(rawDBEvent.getIdMap());
            
            // TODO: set old and new value
            //payload.setOldValue(oldValue);
            //payload.setNewValue(newValue);
            
            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGeneratedList.add(eventGenerated);
        }

        return eventGeneratedList;
    }

}
