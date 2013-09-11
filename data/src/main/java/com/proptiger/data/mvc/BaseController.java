/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.proptiger.exception.ProAPIException;

/**
 * @author mandeep
 *
 */
public class BaseController {
	private static Logger logger = LoggerFactory.getLogger(BaseController.class);
	
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
            throw new ProAPIException("Could not serialize response", e);
        }
    }
    public <T> T parseJsonToObject(String content, Class<T> valueType){
		// TODO Auto-generated method stub
		try {
			if(content != null){
				return mapper.readValue(content, valueType);
			}
			return null;
		} catch (Exception e) {
			throw new ProAPIException("Could not parse request", e);
		}
	}
}
