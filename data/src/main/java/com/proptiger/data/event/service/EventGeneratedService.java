package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventAttributeConfig;
import com.proptiger.data.event.generator.model.DBRawEventOperationConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.repo.EventTypeMappingDao;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.event.repo.DBRawEventTableLogDao;

@Service
public class EventGeneratedService {
    private static Logger           logger     = LoggerFactory.getLogger(LocalityService.class);

    @Autowired
    private EventGeneratedDao       eventGeneratedDao;

    @Autowired
    private EventTypeMappingService eventTypeMappingService;

    @Autowired
    private DBRawEventTableLogDao   dbRawEventTableLogDao;

    @Autowired
    private EventTypeMappingDao     dbEventMappingDao;

    @Autowired
    private EventTypeService        eventTypeService;

    private Gson                    serializer = new Gson();

    public void persistEvents(List<EventGenerated> eventGenerateds, DBRawEventTableLog dbRawEventTableLog) {
        saveOrUpdateEvents(eventGenerateds);
        dbRawEventTableLogDao.updateDateAttributeValueById(
                dbRawEventTableLog.getId(),
                dbRawEventTableLog.getDateAttributeValue());
    }

    public List<EventGenerated> getRawEvents() {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusOrderByCreatedDateAsc(EventGenerated.EventStatus.Raw);
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds;
    }

    public List<EventGenerated> getProcessedEvents() {

        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusAndExpiryDateLessThanEqualOrderByCreatedDateAsc(
                        EventGenerated.EventStatus.Processed,
                        new Date());
        populateEventsDataAfterLoad(listEventGenerateds);

        return listEventGenerateds;
    }

    public List<EventGenerated> getProcessedEventsToBeMerged() {

        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusAndExpiryDateGreaterThanOrderByCreatedDateAsc(
                        EventGenerated.EventStatus.Processed,
                        new Date());
        populateEventsDataAfterLoad(listEventGenerateds);

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
                numberOfRowsAffected = eventGeneratedDao.updateEventStatusByIdAndOldStatus(
                        eventGenerated.getEventStatus(),
                        entry.getKey(),
                        eventGenerated.getId());
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
        Iterator<EventGenerated> iterator = events.iterator();
        while (iterator.hasNext()) {
            populateEventsDataBeforeSave(iterator.next());
        }
        eventGeneratedDao.save(events);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        return events;
    }

    public EventGenerated saveOrUpdateOneEvent(EventGenerated event) {
        populateEventsDataBeforeSave(event);
        eventGeneratedDao.save(event);
        /*
         * Not returning the save object received from JPA as it will empty the
         * transient fields.
         */
        return event;
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

    private void populateEventsDataAfterLoad(List<EventGenerated> listEventGenerated) {
        for (EventGenerated eventGenerated : listEventGenerated) {
            setEventTypeOnEventGenerated(eventGenerated);

            System.out.println(new Gson().toJson(eventGenerated));
            System.out.println(" DATA class name " + eventGenerated.getEventType().getEventTypeConfig()
                    .getDataClassName().getName());
            eventGenerated.setEventTypePayload((EventTypePayload) new Gson().fromJson(
                    eventGenerated.getData(),
                    eventGenerated.getEventType().getEventTypeConfig().getDataClassName()));
        }
    }

    private void setEventTypeOnEventGenerated(EventGenerated eventGenerated) {
        EventType eventType = eventTypeService.getEventTypeByEventTypeId(eventGenerated.getEventTypeId());
        eventGenerated.setEventType(eventType);
    }

    private void populateEventsDataBeforeSave(EventGenerated eventGenerated) {
        logger.info("\n SAVE BEING CALLED \n");
        eventGenerated.setData(serializer.toJson(eventGenerated.getEventTypePayload()));
        logger.info(" EVENT ID " + eventGenerated.getId() + " DATA " + eventGenerated.getData() + "\n");
    }

}
