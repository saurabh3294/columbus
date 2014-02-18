/**
 * 
 */
package com.proptiger.data.init;

import java.util.HashSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * @author mandeep
 * 
 */
public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        Hibernate4Module hm = new Hibernate4Module();
        hm.disable(Feature.FORCE_LAZY_LOADING);
        this.registerModule(hm);

        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.setFailOnUnknownId(false).addFilter(
                "fieldFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>()));
        this.setFilters(filterProvider);
    }
}
