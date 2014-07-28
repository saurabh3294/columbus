package com.proptiger.data.event.enums;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.processor.PhotoChangeProcessor;
import com.proptiger.data.event.processor.PriceChangeProcessor;
import com.proptiger.data.model.event.payload.DefaultEventTypePayload;
import com.proptiger.data.model.event.payload.EventTypePayload;

// TODO remove the Types ENUM. make it dynamic.
public enum Types {
    PortfolioPriceChange("portfolio_price_change", DefaultEventTypePayload.class, PriceChangeProcessor.class, null), PortfolioPhotoAdded(
            "portfolio_photo_added", DefaultEventTypePayload.class, PhotoChangeProcessor.class, null);

    private String                            name;
    private Class<? extends EventTypePayload> dataClassName;
    private Class<? extends DBEventProcessor> processorClassName;
    private EventTypeIdConstants[]            idNames;
    private DBEventProcessor                  processorObject;
    private EventTypePayload                  eventTypePayloadObject;

    @Autowired
    private ApplicationContext                applicationContext;

    static {
        PortfolioPriceChange.setIdNames(new EventTypeIdConstants[] { EventTypeIdConstants.PropertyId });
        PortfolioPhotoAdded.setIdNames(new EventTypeIdConstants[] { EventTypeIdConstants.PropertyId });
    }

    @PostLoad
    // TODO to handle it without applicationContext
    public void setObject() {
        this.processorObject = applicationContext.getBean(this.processorClassName);
        this.eventTypePayloadObject = applicationContext.getBean(this.dataClassName);
    }

    Types(
            String name,
            Class<? extends EventTypePayload> dataClassName,
            Class<? extends DBEventProcessor> procClass,
            EventTypeIdConstants[] idNames) {

        this.name = name;
        this.dataClassName = dataClassName;
        this.processorClassName = procClass;
        this.idNames = idNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}