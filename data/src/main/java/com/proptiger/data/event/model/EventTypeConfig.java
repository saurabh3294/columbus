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
import com.proptiger.data.event.processor.seo.SeoLocalityChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoLocalityDeleteProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectContentChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectDeleteProcessor;
import com.proptiger.data.event.processor.seo.SeoPropertyChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoPropertyDeleteProcessor;

public class EventTypeConfig extends BaseModel {

    /**
     * 
     */
    private static final long                    serialVersionUID      = 5353549466505297871L;

    private static Map<String, EventTypeConfig>  eventTypeConfigMap    = new HashMap<String, EventTypeConfig>();

    static {

        eventTypeConfigMap.put(EventTypeEnum.PortfolioPriceChange.getName(), new EventTypeConfig(
                DefaultEventTypePayload.class,
                PriceChangeProcessor.class,
                PriceChangeVerification.class));
        eventTypeConfigMap.put("project_url_generation", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoProjectChangeProcessor.class,
                null));
        eventTypeConfigMap.put("property_url_generation", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoPropertyChangeProcessor.class,
                null));
        eventTypeConfigMap.put("project_url_delete", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoProjectDeleteProcessor.class,
                null));
        eventTypeConfigMap.put("property_url_delete", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoPropertyDeleteProcessor.class,
                null));
        eventTypeConfigMap.put("locality_url_generation", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoLocalityChangeProcessor.class,
                null));
        eventTypeConfigMap.put("locality_url_delete", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoLocalityDeleteProcessor.class,
                null));
        eventTypeConfigMap.put("builder_url_generation", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoBuilderChangeProcessor.class,
                null));
        eventTypeConfigMap.put("builder_url_delete", new EventTypeConfig(
                DefaultEventTypePayload.class,
                SeoBuilderDeleteProcessor.class,
                null));
        eventTypeConfigMap.put("project_url_content_change", new EventTypeConfig(
                MultiValueEventTypePayload.class,
                SeoProjectContentChangeProcessor.class,
                null));

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