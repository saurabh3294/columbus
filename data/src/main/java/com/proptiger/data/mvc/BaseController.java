/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * @author mandeep
 *
 */
public class BaseController {
    protected Object filterFields(Object object, String[] fields) {
        try {
            Set<String> fieldSet = new HashSet<String>();
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("fieldFilter", SimpleBeanPropertyFilter.serializeAllExcept(fieldSet));

            ObjectMapper objectMapper = new ObjectMapper();
            if (fields != null) {
                for (String field : fields) {
                    fieldSet.add(field);
                }

                filterProvider = new SimpleFilterProvider().addFilter("fieldFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet));
            }

            return objectMapper.writer(filterProvider).writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize " + object, e);
        }
    }
}
