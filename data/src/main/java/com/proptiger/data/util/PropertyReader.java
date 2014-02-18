package com.proptiger.data.util;

import javax.annotation.Resource;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This class is responsible to read property files annotated with @PropertySource
 * annotation and keep that in Environment object and exposes those properties
 * with getRequiredProperty method.
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
@PropertySource("classpath:application.properties")
public class PropertyReader {

    @Resource
    private Environment env;

    /**
     * @param key
     * @return
     */
    public String getRequiredProperty(String key) {
        return env.getRequiredProperty(key);
    }
}
