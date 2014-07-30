package com.proptiger.data.event.enums;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.processor.PhotoChangeProcessor;
import com.proptiger.data.event.processor.PriceChangeProcessor;
import com.proptiger.data.event.verification.DBEventVerification;
import com.proptiger.data.event.verification.PriceChangeVerification;
import com.proptiger.data.model.event.payload.DefaultEventTypePayload;
import com.proptiger.data.model.event.payload.EventTypePayload;

// TODO remove the Types ENUM. make it dynamic.
public class EventTypeConfig {
    /*
     * PortfolioPriceChange("portfolio_price_change",
     * DefaultEventTypePayload.class, PriceChangeProcessor.class, null,
     * PriceChangeVerification.class),
     * PortfolioPhotoAdded("portfolio_photo_added",
     * DefaultEventTypePayload.class, PhotoChangeProcessor.class, null,
     * DBEventVerification.class);
     */
    public static Map<String, EventTypeConfig>  eventTypeConfig;
    static {
        // PortfolioPriceChange.setIdNames(new EventTypeIdConstants[] {
        // EventTypeIdConstants.PropertyId });
        // PortfolioPhotoAdded.setIdNames(new EventTypeIdConstants[] {
        // EventTypeIdConstants.PropertyId });
        eventTypeConfig = new HashMap<String, EventTypeConfig>();
        
        eventTypeConfig.put("portfolio_price_change", new EventTypeConfig(
                DefaultEventTypePayload.class,
                PriceChangeProcessor.class,
                null,
                PriceChangeVerification.class));
        
        eventTypeConfig.put("portfolio_photo_added", new EventTypeConfig(
                DefaultEventTypePayload.class,
                PhotoChangeProcessor.class,
                null,
                DBEventVerification.class));

    }

    private Class<? extends EventTypePayload>    dataClassName         = DefaultEventTypePayload.class;
    private Class<? extends DBEventProcessor>    processorClassName    = PriceChangeProcessor.class;
    private Class<? extends DBEventVerification> verificationClassName = DBEventVerification.class;
    private EventTypeIdConstants[]               idNames;
    private DBEventProcessor                     processorObject;
    private EventTypePayload                     eventTypePayloadObject;
    private DBEventVerification                  eventVerificationObject;

    @Autowired
    private ApplicationContext                   applicationContext;

    // TODO to handle it without applicationContext
    public void setObject() {
        this.processorObject = applicationContext.getBean(this.processorClassName);
        this.eventTypePayloadObject = applicationContext.getBean(this.dataClassName);
        this.eventVerificationObject = applicationContext.getBean(this.verificationClassName);
    }

    EventTypeConfig(
            Class<? extends EventTypePayload> dataClassName,
            Class<? extends DBEventProcessor> procClass,
            EventTypeIdConstants[] idNames,
            Class<? extends DBEventVerification> verifiClassName) {

        this.dataClassName = dataClassName;
        this.processorClassName = procClass;
        this.idNames = idNames;
        this.verificationClassName = verifiClassName;
        setObject();
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

    public EventTypeIdConstants[] getIdNames() {
        return idNames;
    }

    public void setIdNames(EventTypeIdConstants[] idNames) {
        this.idNames = idNames;
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