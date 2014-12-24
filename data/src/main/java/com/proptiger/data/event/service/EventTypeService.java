package com.proptiger.data.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.event.EventType;
import com.proptiger.data.event.model.DefaultEventTypeConfig;
import com.proptiger.data.event.repo.EventTypeDao;

@Service
public class EventTypeService {
    private static Logger      logger = LoggerFactory.getLogger(EventTypeService.class);

    @Autowired
    private EventTypeDao       eventTypeDao;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Return the EventType for a given event id after populating config
     * 
     * @param eventTypeId
     * @return
     */
    public EventType getEventTypeByEventTypeId(int eventTypeId) {
        EventType eventType = eventTypeDao.findOne(eventTypeId);
        populateConfig(eventType);

        return eventType;
    }

    /**
     * Populates the config for a given event type
     * 
     * @param eventType
     * @return
     */
    public EventType populateConfig(EventType eventType) {
        String configName = eventType.getName();
        if (eventType.getOverwriteConfigName() != null) {
            configName = eventType.getOverwriteConfigName();
        }
        DefaultEventTypeConfig savedEventTypeConfig = DefaultEventTypeConfig.getEventTypeConfig(configName);
        logger.debug("Found eventTypeConfig " + savedEventTypeConfig
                + " for configName "
                + configName
                + " and eventName "
                + eventType.getName()
                + " in eventTypeConfig mapping");

        if (savedEventTypeConfig == null) {
            logger.debug("EventType " + eventType.getName()
                    + " do not have mapping of Event Type Config. Using Defaults.");
            savedEventTypeConfig = new DefaultEventTypeConfig();
        }
        setEventTypeConfigObjectAttributes(savedEventTypeConfig);
        eventType.setEventTypeConfig(savedEventTypeConfig);
        return eventType;
    }

    private void setEventTypeConfigObjectAttributes(DefaultEventTypeConfig eventTypeConfig) {
        eventTypeConfig.setProcessorObject(applicationContext.getBean(eventTypeConfig.getProcessorClassName()));
        try {
            eventTypeConfig.setEventTypePayloadObject(eventTypeConfig.getDataClassName().newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        eventTypeConfig.setEventVerificationObject(applicationContext.getBean(eventTypeConfig
                .getVerificationClassName()));
    }
}
