package com.proptiger.data.event.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.core.model.BaseModel;
import com.proptiger.data.event.enums.EventTypeEnum;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.model.payload.EventTypePayload;
import com.proptiger.data.event.model.payload.NewsEventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.processor.DefaultDBEventProcessor;
import com.proptiger.data.event.processor.LocalityNewsProcessor;
import com.proptiger.data.event.processor.PriceChangeProcessor;
import com.proptiger.data.event.processor.ProjectNewsProcessor;
import com.proptiger.data.event.verification.DBEventVerification;
import com.proptiger.data.event.verification.DefaultDBEventVerification;
import com.proptiger.data.event.verification.PriceChangeVerification;

public class EventTypeConfig extends BaseModel {

    private static final long                    serialVersionUID      = 5353549466505297871L;

    private static Map<String, EventTypeConfig>  eventTypeConfigMap    = new HashMap<String, EventTypeConfig>();

    static {

        eventTypeConfigMap.put(EventTypeEnum.PortfolioPriceChange.getName(), new EventTypeConfig(
                DefaultEventTypePayload.class,
                PriceChangeProcessor.class,
                PriceChangeVerification.class));

        eventTypeConfigMap.put(EventTypeEnum.PortfolioProjectNews.getName(), new EventTypeConfig(
                NewsEventTypePayload.class,
                ProjectNewsProcessor.class,
                DefaultDBEventVerification.class));

        eventTypeConfigMap.put(EventTypeEnum.PortfolioLocalityNews.getName(), new EventTypeConfig(
                NewsEventTypePayload.class,
                LocalityNewsProcessor.class,
                DefaultDBEventVerification.class));
    }

    private Class<? extends EventTypePayload>    dataClassName         = DefaultEventTypePayload.class;
    private Class<? extends DBEventProcessor>    processorClassName    = DefaultDBEventProcessor.class;
    private Class<? extends DBEventVerification> verificationClassName = DefaultDBEventVerification.class;

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

    }

    public static EventTypeConfig getEventTypeConfig(String configName) {
        return eventTypeConfigMap.get(configName);
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