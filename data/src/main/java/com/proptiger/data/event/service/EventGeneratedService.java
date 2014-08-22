package com.proptiger.data.event.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.generator.model.DBRawEventAttributeConfig;
import com.proptiger.data.event.generator.model.DBRawEventOperationConfig;
import com.proptiger.data.event.model.DBRawEventTableLog;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.repo.DBRawEventTableLogDao;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.event.repo.RawEventToEventTypeMappingDao;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.util.Serializer;

@Service
public class EventGeneratedService {
    private static Logger                     logger     = LoggerFactory.getLogger(EventGeneratedService.class);

    @Autowired
    private EventGeneratedDao                 eventGeneratedDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private DBRawEventTableLogDao             dbRawEventTableLogDao;

    @Autowired
    private RawEventToEventTypeMappingDao     dbEventMappingDao;

    @Autowired
    private EventTypeService                  eventTypeService;

    @Autowired
    private ApplicationContext                applicationContext;

    private Gson                              serializer = new Gson();

    @Transactional
    public void persistEvents(List<EventGenerated> eventGenerateds, DBRawEventTableLog dbRawEventTableLog) {
        logger.info(eventGenerateds.size() + " Events Being Persisting ");

        applicationContext.getBean(this.getClass()).saveOrUpdateEvents(eventGenerateds);
        
        logger.info(" Events Saved .");
        
        dbRawEventTableLogDao.updateLastTransactionKeyValueById(
                dbRawEventTableLog.getId(),
                dbRawEventTableLog.getLastTransactionKeyValue());
        
        logger.info(" Updated the Last Transaction Value " + dbRawEventTableLog.getLastTransactionKeyValue()
                + " for table Config "
                + dbRawEventTableLog.getId());
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

    public List<EventGenerated> getVerifiedEventsFromDate(Date fromDate) {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusAndUpdatedDateGreaterThanOrderByUpdatedDateAsc(
                        EventGenerated.EventStatus.Verfied,
                        fromDate);
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

    public Long getRawEventCount() {
        return eventGeneratedDao.getEventCountByEventStatus(EventStatus.Raw);
    }

    public EventGenerated getLatestEventGenerated() {
        logger.debug("Finding latest event generated");
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(0, 1);
        List<EventGenerated> listEventGenerateds = eventGeneratedDao.getLatestEventGenerated(pageable);
        logger.debug("Latest Event generated: " + listEventGenerateds);
        
        if (listEventGenerateds == null || listEventGenerateds.isEmpty()) {
            return null;
        }
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds.get(0);
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

    @Transactional
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

    @Transactional
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
        logger.info(" Generate the Events from Raw Event " + rawDBEvent.getTransactionKeyValue());

        List<EventGenerated> eventGeneratedList = new ArrayList<EventGenerated>();
        DBRawEventOperationConfig dbRawEventOperationConfig = rawDBEvent.getDbRawEventOperationConfig();

        if (DBOperation.INSERT.equals(dbRawEventOperationConfig.getDbOperation())) {
            generateEvents(rawDBEvent, dbRawEventOperationConfig.getListEventTypes(), null, eventGeneratedList);
        }
        else if (DBOperation.DELETE.equals(dbRawEventOperationConfig.getDbOperation())) {
            generateEvents(rawDBEvent, dbRawEventOperationConfig.getListEventTypes(), null, eventGeneratedList);
        }
        else if (DBOperation.UPDATE.equals(dbRawEventOperationConfig.getDbOperation())) {

            for (String attributeName : rawDBEvent.getNewDBValueMap().keySet()) {
                logger.debug(" Attribute Name " + attributeName);

                DBRawEventAttributeConfig dbRawEventAttributeConfig = dbRawEventOperationConfig
                        .getDBRawEventAttributeConfig(attributeName);
                if (dbRawEventAttributeConfig != null) {
                    logger.debug(" List of Events Mapped from Attribute Name " + Serializer
                            .toJson(dbRawEventAttributeConfig.getListEventTypes()));

                    generateEvents(
                            rawDBEvent,
                            dbRawEventAttributeConfig.getListEventTypes(),
                            attributeName,
                            eventGeneratedList);
                }
            }
        }
        logger.info(" Number of Events Generated are: " + eventGeneratedList.size());

        return eventGeneratedList;
    }

    private List<EventGenerated> generateEvents(
            RawDBEvent rawDBEvent,
            List<EventType> eventTypeList,
            String attributeName,
            List<EventGenerated> eventGeneratedList) {
        logger.info(" Generate Events ");

        for (EventType eventType : eventTypeList) {
            logger.debug(" Event Type " + eventType.getName());
            // TODO to seperate the payload set and new event generated in
            // seperate methods.
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
            eventGenerated.setEventTypeUniqueKey(rawDBEvent.getPrimaryKeyValue().toString());
            eventGenerated.setEventTypeId(eventType.getId());
            eventGeneratedList.add(eventGenerated);

            logger.debug(Serializer.toJson(eventGenerated));
        }
        return eventGeneratedList;
    }

    private void populateEventsDataAfterLoad(List<EventGenerated> listEventGenerated) {
        for (EventGenerated eventGenerated : listEventGenerated) {
            setEventTypeOnEventGenerated(eventGenerated);

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
        logger.debug(" Payload Serialization for Event " + eventGenerated.getEventTypeUniqueKey());

        eventGenerated.setData(serializer.toJson(eventGenerated.getEventTypePayload()));
    }

}
