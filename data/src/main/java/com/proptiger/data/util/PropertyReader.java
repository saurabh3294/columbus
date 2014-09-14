package com.proptiger.data.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * This class is responsible to read property files and exposes those properties
 * with getRequiredProperty method. This class is exposed as MBeans to update
 * some property value at run time.
 * 
 * 
 * @author Rajeev Pandey
 * 
 */

@Component
@ManagedResource(objectName = "com.proptiger.data.init:name=propertyReaderMBean", description = "Property Reader")
public class PropertyReader {

    @Autowired
    private static GenericConversionService conversionService;

    private Logger                          logger = LoggerFactory.getLogger(getClass());
    private static Map<String, String>      propertyDataMap;

    /**
     * Initializing the property key value map. Using Map as key value store so
     * that can be exposed to JMX to update values at run time
     * 
     * @throws ConfigurationException
     */
    @PostConstruct
    public void init() throws ConfigurationException {
        conversionService = new DefaultConversionService();
        propertyDataMap = new HashMap<String, String>();
        PropertiesConfiguration configurer = new PropertiesConfiguration("application.properties");
        Iterator<?> keysIt = configurer.getKeys();
        while (keysIt.hasNext()) {
            String key = (String) keysIt.next();
            String value = configurer.getString(key);
            propertyDataMap.put(key, value);
        }
    }

    /**
     * @param key
     * @return
     */
    @ManagedOperation(description = "Get value from property file")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "key", description = "property file key") })
    public String getRequiredProperty(String key) {
        if (key != null) {
            String value = propertyDataMap.get(key);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalStateException("required key" + key + " not found");
    }

    /**
     * This method will try to convert value to provided Class type
     * 
     * @param key
     * @param requiredType
     * @return
     */
    public static <T> T getRequiredPropertyAsType(String key, Class<T> requiredType) {
        if (key != null) {
            String value = propertyDataMap.get(key);
            if (value != null) {
                return conversionService.convert(value, requiredType);
            }
        }
        throw new IllegalStateException("required key" + key + " not found");
    }

    /**
     * 
     * @param key
     * @return
     */
    public static int getRequiredPropertyAsInt(String key) {
        return getRequiredPropertyAsType(key, Integer.class);
    }

    /**
     * gets property as boolean
     * 
     * @param key
     * @return
     */
    public static boolean getRequiredPropertyAsBoolean(String key) {
        return getRequiredPropertyAsType(key, Boolean.class);
    }

    /**
     * gets property as string
     * 
     * @param key
     * @return
     */
    public static String getRequiredPropertyAsString(String key) {
        return getRequiredPropertyAsType(key, String.class);
    }

    /**
     * Updating value of a key in map through JMX
     * 
     * @param key
     * @param value
     */
    @ManagedOperation(description = "Update property value")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "key", description = "property file key"),
            @ManagedOperationParameter(name = "value", description = "value of key") })
    public void updateRequiredProperty(String key, String value) {
        if (key != null && value != null && !key.isEmpty() && !value.isEmpty()) {
            if (propertyDataMap.get(key) != null) {
                logger.debug(
                        "Property value update for key {},old value {}, new value {}",
                        key,
                        propertyDataMap.get(key),
                        value);
                propertyDataMap.put(key, value);
                return;
            }
        }
        throw new IllegalStateException("required key" + key + " not found");
    }
}
