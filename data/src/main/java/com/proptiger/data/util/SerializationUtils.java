package com.proptiger.data.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.init.CustomObjectMapper;

/**
 * 
 * @author azi
 * 
 */
public class SerializationUtils {
    /**
     * utility method to convert object to json
     * 
     * @param object
     * @return
     */
    public static JsonNode objectToJson(Object object) {
        ObjectMapper mapper = new CustomObjectMapper();
        return mapper.valueToTree(object);
    }
}