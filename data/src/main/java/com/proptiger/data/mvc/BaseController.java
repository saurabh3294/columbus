/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.Selector;
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

			return mapper.readValue(mapper.writer(filterProvider).writeValueAsString(object), object.getClass());
			//return mapper.readTree(mapper.writer(filterProvider).writeValueAsString(object));

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
				Map map = mapper.convertValue(val, Map.class);
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
     * This method filters out all fields that in not in fields set
     * @param list
     * @param fields
     * @return
     */
	protected <T> Map<String, Object> filterOutAllExcept(T val, Set<String> fields) {
		try {
			Map<String, Object> map = mapper.convertValue(val, Map.class);
			if (fields != null && fields.size() > 0) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (!fields.contains(key)) {
						it.remove();
					}
				}
			}
			return map;
		} catch (Exception e) {
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
    
    public <T> ProAPIResponse postProcess(T val, int count, Selector selector){
    	if(selector != null && selector.getFields() != null){
    		return new ProAPISuccessCountResponse(filterOutAllExcept(
    				val, selector.getFields()), count);
    	}
    	return new ProAPISuccessCountResponse(val, count);
    }
    public <T> ProAPIResponse postProcess(List<T> val, int count, Selector selector){
    	if(selector != null && selector.getFields() != null){
    		return new ProAPISuccessCountResponse(filterOutAllExcept(
    				val, selector.getFields()), count);
    	}
    	return new ProAPISuccessCountResponse(val, count);
    }
}
