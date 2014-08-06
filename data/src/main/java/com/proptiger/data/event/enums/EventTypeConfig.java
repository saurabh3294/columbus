package com.proptiger.data.event.enums;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.processor.PhotoChangeProcessor;
import com.proptiger.data.event.processor.PriceChangeProcessor;
import com.proptiger.data.event.verification.DBEventVerification;
import com.proptiger.data.event.verification.PriceChangeVerification;

// TODO remove the Types ENUM. make it dynamic.
public class EventTypeConfig {

    public static Map<String, EventTypeConfig>   eventTypeConfigMap;
    static {

        eventTypeConfigMap = new HashMap<String, EventTypeConfig>();

        eventTypeConfigMap.put("portfolio_price_change", new EventTypeConfig(
                DefaultEventTypePayload.class,
                PriceChangeProcessor.class,
                PriceChangeVerification.class));

        eventTypeConfigMap.put("portfolio_photo_added", new EventTypeConfig(
                DefaultEventTypePayload.class,
                PhotoChangeProcessor.class,
                DBEventVerification.class));

    }

    private Class<? extends EventTypePayload>    dataClassName         = DefaultEventTypePayload.class;
    private Class<? extends DBEventProcessor>    processorClassName    = PriceChangeProcessor.class;
    private Class<? extends DBEventVerification> verificationClassName = DBEventVerification.class;
    private DBEventProcessor                     processorObject;
    private EventTypePayload                     eventTypePayloadObject;
    private DBEventVerification                  eventVerificationObject;

    EventTypeConfig(
            Class<? extends EventTypePayload> dataClassName,
            Class<? extends DBEventProcessor> procClass,
            Class<? extends DBEventVerification> verifiClassName) {

        if (dataClassName != null) {
            this.dataClassName = dataClassName;
        }
        if (processorClassName != null) {
            this.processorClassName = procClass;
        }
        if (verifiClassName != null) {
            this.verificationClassName = verifiClassName;
        }
    }

    public EventTypeConfig() {
        // TODO Auto-generated constructor stub
    }

    public Class<? extends EventTypePayload> getDataClassName() {
        return dataClassName;
    }

    public void setDataClassName(Class<? extends EventTypePayload> dataClassName) {
        this.dataClassName = dataClassName;
    }

    public Class<? extends DBEventProcessor> getProcessorClassName() {
        return processorClassName;
    }

    public void setProcessorClassName(Class<? extends DBEventProcessor> processorClassName) {
        this.processorClassName = processorClassName;
    }

    public DBEventProcessor getProcessorObject() {
        return processorObject;
    }

    public void setProcessorObject(DBEventProcessor processorObject) {
        this.processorObject = processorObject;
    }

    public EventTypePayload getEventTypePayloadObject() {
        return eventTypePayloadObject;
    }

    public void setEventTypePayloadObject(EventTypePayload eventTypePayloadObject) {
        this.eventTypePayloadObject = eventTypePayloadObject;
    }

    public Class<? extends DBEventVerification> getVerificationClassName() {
        return verificationClassName;
    }

    public void setVerificationClassName(Class<? extends DBEventVerification> verificationClassName) {
        this.verificationClassName = verificationClassName;
    }

    public DBEventVerification getEventVerificationObject() {
        return eventVerificationObject;
    }

    public void setEventVerificationObject(DBEventVerification eventVerificationObject) {
        this.eventVerificationObject = eventVerificationObject;
    }
}