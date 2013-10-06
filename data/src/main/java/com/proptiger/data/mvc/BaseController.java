/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.proptiger.exception.ProAPIException;

/**
 * This class provdes some utility functions to serialize and deserialize response 
 * @author mandeep
 * @author Rajeev Pandey
 *
 */
public abstract class BaseController {
	private ObjectMapper mapper = new ObjectMapper();

	public BaseController() {
	    mapper.setDateFormat(new ISO8601DateFormat());
	    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	}

    protected Object filterFields(Object object, Set<String> fields) {
		try {
			Set<String> fieldSet = new HashSet<String>();
			FilterProvider filterProvider = new SimpleFilterProvider()
					.addFilter("fieldFilter", SimpleBeanPropertyFilter
							.serializeAllExcept(fieldSet));

			if (fields != null) {
				filterProvider = new SimpleFilterProvider().addFilter(
						"fieldFilter",
						SimpleBeanPropertyFilter.filterOutAllExcept(fields));
			}

			return mapper.readTree(mapper.writer(filterProvider).writeValueAsString(object));

		}
        catch (Exception e) {
            throw new ProAPIException("Could not serialize response", e);
        }
    }
    /**
     * This method filters out all fields that in not in fields set
     * @param list
     * @param fields
     * @return
     */
    protected <T> List<Map<String, Object>> filterOutAllExcept(List<T> list, Set<String> fields) {
		try {
			List<Map<String, Object>> result = new ArrayList<>();
			for(T val: list){
				Map<String, Object> map = mapper.convertValue(val, new TypeReference<HashMap<String,String>>(){});
				if(fields != null && fields.size() > 0){
					Iterator<String> it = map.keySet().iterator();
					while(it.hasNext()){
						String key = it.next();
						if(!fields.contains(key)){
							it.remove();
						}
					}
				}
				result.add(map);
			}
			return result;
		}
        catch (Exception e) {
            throw new ProAPIException("Could not serialize response", e);
        }
    }
    
    
    /**
     * This method parses the json String to specified java class type
     * @param content
     * @param valueType
     * @return
     */
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
