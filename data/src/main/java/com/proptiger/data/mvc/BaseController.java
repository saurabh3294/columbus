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
	private ObjectMapper mapper = new ObjectMapper();
    protected Object filterFields(Object object, Set<String> fields) {
        try {
        	Set<String> fieldSet = new HashSet<String>();
			FilterProvider filterProvider = new SimpleFilterProvider()
					.addFilter("fieldFilter", SimpleBeanPropertyFilter
							.serializeAllExcept(fieldSet));

        	
        	if(fields != null){
        		filterProvider = new SimpleFilterProvider()
				.addFilter("fieldFilter",
						SimpleBeanPropertyFilter.filterOutAllExcept(fields));
        	}

			return mapper.writer(filterProvider).writeValueAsString(object);

        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize " + object, e);
        }
    }
    public <T> T parseJsonToObject(String content, Class<T> valueType) throws Exception {
		// TODO Auto-generated method stub
		try {
			if(content != null){
				return mapper.readValue(content, valueType);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
}
