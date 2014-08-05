package com.proptiger.data.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.enums.EventTypeConfig;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.repo.EventTypeDao;

@Service
public class EventTypeService {

    @Autowired
    private EventTypeDao       eventTypeDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Cacheable(value = "event")
    public EventType getEventTypeByEventTypeId(int eventTypeId) {
        EventType eventType = eventTypeDao.findOne(eventTypeId);
        populateConfig(eventType);

        return eventType;
    }

    private void populateConfig(EventType eventType) {
        String configName = eventType.getName();
        if (eventType.getOverwriteConfigName() != null) {
            configName = eventType.getOverwriteConfigName();
        }
        EventTypeConfig savedEventTypeConfig = EventTypeConfig.eventTypeConfigMap.get(configName);
        // TODO to handle the case when there is no mapping of name in the
        // config.
        // Code execution should not be stopped as a proper logging of error has
        // to be done.
        if (savedEventTypeConfig == null) {

        }
        setEventTypeConfigObjectAttributes(savedEventTypeConfig);
        eventType.setEventTypeConfig(savedEventTypeConfig);
    }

    private void setEventTypeConfigObjectAttributes(EventTypeConfig eventTypeConfig) {
        eventTypeConfig.setProcessorObject(applicationContext.getBean(eventTypeConfig.getProcessorClassName()));
        try {
            eventTypeConfig.setEventTypePayloadObject(eventTypeConfig.getDataClassName().newInstance());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        eventTypeConfig.setEventVerificationObject(applicationContext.getBean(eventTypeConfig
                .getVerificationClassName()));
    }
}
