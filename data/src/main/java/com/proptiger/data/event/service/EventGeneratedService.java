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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.pojo.LimitOffsetPageRequest;
import com.proptiger.data.event.generator.model.RawDBEventAttributeConfig;
import com.proptiger.data.event.generator.model.RawDBEventOperationConfig;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.RawDBEvent;
import com.proptiger.data.event.model.RawEventTableDetails;
import com.proptiger.data.event.model.EventGenerated.EventStatus;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.repo.EventGeneratedDao;
import com.proptiger.data.event.repo.RawEventTableDetailsDao;
import com.proptiger.data.event.repo.RawEventToEventTypeMappingDao;
import com.proptiger.data.notification.model.Subscriber;
import com.proptiger.data.notification.model.Subscriber.SubscriberName;
import com.proptiger.data.notification.service.SubscriberConfigService;
import com.proptiger.data.util.Serializer;

@Service
public class EventGeneratedService {

    private static Logger                     logger = LoggerFactory.getLogger(EventGeneratedService.class);

    @Autowired
    private EventGeneratedDao                 eventGeneratedDao;

    @Autowired
    private RawEventToEventTypeMappingService eventTypeMappingService;

    @Autowired
    private RawEventTableDetailsDao           rawEventTableDetailsDao;

    @Autowired
    private RawEventToEventTypeMappingDao     dbEventMappingDao;

    @Autowired
    private EventTypeService                  eventTypeService;

    @Autowired
    private ApplicationContext                applicationContext;

    @Autowired
    private SubscriberConfigService           subscriberConfigService;

    /**
     * Persisting EventGenerateds in DB and updating the last read transaction
     * in RawEventTableDetails
     * 
     * @param eventGenerateds
     * @param rawEventTableDetails
     */
    @Transactional
    public void persistEvents(
            List<EventGenerated> eventGenerateds,
            RawEventTableDetails rawEventTableDetails,
            Long transactionId) {

        applicationContext.getBean(this.getClass()).saveOrUpdateEvents(eventGenerateds);

        eventTypeMappingService.updateLastAccessedTransactionId(rawEventTableDetails, transactionId);

        logger.info(" Updated the Last Transaction Value " + transactionId
                + " for table Config "
                + rawEventTableDetails.getId());
    }

    /**
     * Get EventGenerateds from DB which are in RAW state and are ready to be
     * processed
     * 
     * @return
     */
    public List<EventGenerated> getRawEvents() {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusOrderByCreatedAtAsc(EventGenerated.EventStatus.Raw);
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds;
    }

    /**
     * Get EventGenerateds from DB which are in Processed state and whose
     * Holding period has expired
     * 
     * @return
     */
    public List<EventGenerated> getProcessedEvents() {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusAndExpiryDateLessThanEqualOrderByCreatedAtAsc(
                        EventGenerated.EventStatus.Processed,
                        new Date());
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds;
    }

    /**
     * Get EventGenerateds of a particular event type from DB which are in
     * Processed state and are still in Holding period
     * 
     * @return
     */
    public List<EventGenerated> getProcessedEventsToBeMerged(Integer eventTypeId) {
        List<EventGenerated> listEventGenerateds = eventGeneratedDao
                .findByEventStatusAndEventTypeIdAndExpiryDateGreaterThanOrderByCreatedAtAsc(
                        EventGenerated.EventStatus.Processed,
                        eventTypeId,
                        new Date());
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds;
    }

    /**
     * Returns the count of EventGenerated in Raw state in DB
     * 
     * @return
     */
    public Long getRawEventCount() {
        return eventGeneratedDao.getEventCountByEventStatus(EventStatus.Raw);
    }

    /**
     * Updating the status of EventGenerateds to a new status if the current
     * status matches the provided status
     * 
     * @param updateEventGeneratedByOldValue
     */
    @Transactional
    public void updateEventsOnOldEventStatus(Map<EventStatus, List<EventGenerated>> updateEventGeneratedByOldValue) {
        Integer numberOfRowsAffected;
        for (Map.Entry<EventStatus, List<EventGenerated>> entry : updateEventGeneratedByOldValue.entrySet()) {
            for (EventGenerated eventGenerated : entry.getValue()) {
                if (eventGenerated.getMergedEventId() != null) {
                    numberOfRowsAffected = eventGeneratedDao.updateEventStatusAndMergeIdByIdAndOldStatus(
                            eventGenerated.getEventStatus(),
                            entry.getKey(),
                            eventGenerated.getId(),
                            eventGenerated.getMergedEventId());
                }
                else {
                    numberOfRowsAffected = eventGeneratedDao.updateEventStatusByIdAndOldStatus(
                            eventGenerated.getEventStatus(),
                            entry.getKey(),
                            eventGenerated.getId());
                }
                logger.debug("Event with Id" + eventGenerated.getId()
                        + " was being updated from Old Status : "
                        + entry.getKey()
                        + " to New Status : "
                        + eventGenerated.getEventStatus()
                        + ". The number Of rows affected : "
                        + numberOfRowsAffected);

                // TODO to handle the status of update queries. Currently,
                // reverting them
                // back to their old value.

                // Row was not updated.
                if (numberOfRowsAffected < 1) {
                    // reverting the changes in the model.
                    eventGenerated.setEventStatus(entry.getKey());
                }
            }
        }
    }

    /**
     * Saves the events in DB after populating event data
     * 
     * @param events
     * @return
     */
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

    /**
     * Saves the event in DB after populating event data
     * 
     * @param event
     * @return
     */
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

    /**
     * Generate the list of Events for given RawDBEvent
     * 
     * @param rawDBEvent
     * @return
     */
    public List<EventGenerated> generateEventFromRawDBEvent(RawDBEvent rawDBEvent) {
        logger.debug("Generating the Events from Raw Event with transactionId: " + rawDBEvent.getTransactionKeyValue());

        List<EventGenerated> eventGeneratedList = new ArrayList<EventGenerated>();
        RawDBEventOperationConfig rawDBEventOperationConfig = rawDBEvent.getRawDBEventOperationConfig();

        if (rawDBEventOperationConfig.getListEventTypes() != null) {
            logger.info("Generating Events: " + Serializer.toJson(rawDBEventOperationConfig.getListEventTypes())
                    + "  mapped to OperationConfig: "
                    + rawDBEventOperationConfig.getDbOperation()
                    + " for RawEvent with transactionId: "
                    + rawDBEvent.getTransactionKeyValue());
            generateEvents(rawDBEvent, rawDBEventOperationConfig.getListEventTypes(), null, eventGeneratedList);
        }

        for (String attributeName : rawDBEvent.getNewDBValueMap().keySet()) {
            RawDBEventAttributeConfig rawDBEventAttributeConfig = rawDBEventOperationConfig
                    .getRawDBEventAttributeConfig(attributeName);
            if (rawDBEventAttributeConfig != null && rawDBEventAttributeConfig.getListEventTypes() != null) {
                logger.info("Generating Events: " + Serializer.toJson(rawDBEventAttributeConfig.getListEventTypes())
                        + " mapped to attributeConfig: "
                        + attributeName
                        + " for RawEvent with transactionId: "
                        + rawDBEvent.getTransactionKeyValue());

                generateEvents(
                        rawDBEvent,
                        rawDBEventAttributeConfig.getListEventTypes(),
                        attributeName,
                        eventGeneratedList);
            }
        }

        logger.info(" Number of Events Generated are: " + eventGeneratedList.size());

        return eventGeneratedList;
    }

    /**
     * Get specified latest verified EventGenerateds for a given Subscriber. If
     * eventTypeNames are not specified then all latest verified EventGenerateds
     * will be returned
     * 
     * @param subscriberName
     * @param eventTypeNames
     * @return
     */
    public List<EventGenerated> getLatestVerifiedEventGeneratedsBySubscriber(
            SubscriberName subscriberName,
            List<String> eventTypeNames, Pageable pageable) {

        List<EventGenerated> listEventGenerateds = new ArrayList<EventGenerated>();
        if (checkAndSetSubscriberLastEventId(subscriberName)) {
            return listEventGenerateds;
        }

        logger.debug("Finding latest event generated for the Subscriber " + subscriberName);
        Integer maxEventCount = subscriberConfigService.getMaxSubscriberEventTypeCount(subscriberName);
        if(pageable == null){
        	pageable = new LimitOffsetPageRequest(0, maxEventCount);
        }
        

        if (eventTypeNames == null) {
            listEventGenerateds = eventGeneratedDao.getLatestEventGeneratedBySubscriber(
                    EventStatus.Verified,
                    subscriberName,
                    pageable);
        }
        else {
            listEventGenerateds = eventGeneratedDao.getLatestEventGeneratedBySubscriber(
                    EventStatus.Verified,
                    subscriberName,
                    eventTypeNames,
                    pageable);
        }

        logger.debug("Number of Event Generated being picked up: " + listEventGenerateds.size());
        populateEventsDataAfterLoad(listEventGenerateds);

        return listEventGenerateds;
    }

    private boolean checkAndSetSubscriberLastEventId(SubscriberName subscriberName) {
        Subscriber subscriber = subscriberConfigService.getSubscriber(subscriberName);
        if (subscriber.getLastEventGeneratedId() == null) {
            EventGenerated lastEventGenerated = getLastVerifiedEventGenerated();
            if (lastEventGenerated != null) {
                subscriberConfigService.setLastEventGeneratedIdBySubscriber(lastEventGenerated.getId(), subscriber);
            }
            return true;
        }
        return false;
    }

    private List<EventGenerated> generateEvents(
            RawDBEvent rawDBEvent,
            List<EventType> eventTypeList,
            String attributeName,
            List<EventGenerated> eventGeneratedList) {

        for (EventType eventType : eventTypeList) {
            EventTypePayload payload = eventType.getEventTypeConfig().getEventTypePayloadObject();
            payload.setTransactionKeyName(rawDBEvent.getRawEventTableDetails().getTransactionKeyName());
            payload.setTransactionId(rawDBEvent.getTransactionKeyValue());
            payload.setPrimaryKeyName(rawDBEvent.getRawEventTableDetails().getPrimaryKeyName());
            payload.setPrimaryKeyValue(rawDBEvent.getPrimaryKeyValue());
            payload.setTransactionDateKeyName(rawDBEvent.getRawEventTableDetails().getDateAttributeName());
            payload.setTransactionDateKeyValue(rawDBEvent.getTransactionDate());
            payload.populatePayloadValues(rawDBEvent, attributeName);

            EventGenerated eventGenerated = new EventGenerated();
            eventGenerated.setEventType(eventType);
            eventGenerated.setEventTypePayload(payload);
            eventGenerated.setEventTypeUniqueKey(rawDBEvent.getPrimaryKeyValue().toString());
            eventGenerated.setEventTypeId(eventType.getId());
            eventGeneratedList.add(eventGenerated);
        }
        return eventGeneratedList;
    }

    private void populateEventsDataAfterLoad(List<EventGenerated> listEventGenerated) {
        for (EventGenerated eventGenerated : listEventGenerated) {
            setEventTypeOnEventGenerated(eventGenerated);
            eventGenerated.setEventTypePayload((EventTypePayload) Serializer.fromJson(
                    eventGenerated.getData(),
                    eventGenerated.getEventType().getEventTypeConfig().getDataClassName()));
        }
    }

    private void setEventTypeOnEventGenerated(EventGenerated eventGenerated) {
        EventType eventType = eventTypeService.getEventTypeByEventTypeId(eventGenerated.getEventTypeId());
        eventGenerated.setEventType(eventType);
    }

    private void populateEventsDataBeforeSave(EventGenerated eventGenerated) {
        eventGenerated.setData(Serializer.toJson(eventGenerated.getEventTypePayload()));
    }

    private EventGenerated getLastVerifiedEventGenerated() {
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(0, 1);
        List<EventGenerated> listEventGenerateds = eventGeneratedDao.findByEventStatusOrderByUpdatedAtDesc(
                EventStatus.Verified,
                pageable);
        logger.info("Latest Event generated: " + listEventGenerateds);

        if (listEventGenerateds == null || listEventGenerateds.isEmpty()) {
            return null;
        }
        populateEventsDataAfterLoad(listEventGenerateds);
        return listEventGenerateds.get(0);
    }
    
    @Transactional
    public Integer updateEventStatusByEventTypeAndUniqueKey(String eventTypeName, int uniqueKey, EventStatus eventStatus){
    	return eventGeneratedDao.updateEventStatusByEventTypeAndUniqueKey(eventTypeName, uniqueKey + "", eventStatus);
    }

}
