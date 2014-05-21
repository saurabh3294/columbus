/**
 * 
 */
package com.proptiger.data.mvc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ProAPIException;

/**
 * This class provdes some utility functions to serialize and deserialize
 * response
 * 
 * @author mandeep
 * @author Rajeev Pandey
 * 
 */
@SessionAttributes({ Constants.LOGIN_INFO_OBJECT_NAME })
public abstract class BaseController {
    private ObjectMapper                mapper         = new ObjectMapper();
    private static Hibernate4Module     hm             = null;
    private static SimpleFilterProvider filterProvider = null;
    private static Logger               logger         = LoggerFactory.getLogger(BaseController.class);

    static {
        hm = new Hibernate4Module();
        hm.disable(Feature.FORCE_LAZY_LOADING);
    }

    public BaseController() {
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.registerModule(hm);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setFilters(filterProvider);
        filterProvider = new SimpleFilterProvider();
        filterProvider.setFailOnUnknownId(false);
    }

    protected Object filterFieldsFromSelector(Object object, FIQLSelector selector) {
        String fieldsString = null;

        if (selector != null) {
            fieldsString = selector.getFields();
        }

        if (fieldsString != null && !fieldsString.isEmpty()) {
            return filterFields(object, new HashSet<>(Arrays.asList(fieldsString.split(","))));
        }

        return filterFields(object, null);
    }

    /**
     * 
     * @param response
     *            is a list of model objects
     * @param sel
     *            is FIQL selector
     * @return is a map of Objects and Objects
     */
    protected <T> Object groupFieldsAsPerSelector(List<T> response, FIQLSelector selector) {
        FIQLSelector sel;
        sel = selector.clone();
        if (sel == null || sel.getGroup() == null || sel.getGroup().isEmpty())
            return response;

        String groupBy = sel.getGroup().split(",")[0];
        Map<Object, Object> result = new HashMap<>();

        try {
            for (T item : response) {
                Object groupValue = PropertyUtils.getSimpleProperty(item, groupBy);
                if (groupValue instanceof Date)
                    groupValue = ((Date) groupValue).getTime();
                if (result.get(groupValue) == null) {
                    List<T> newList = new ArrayList<>();
                    result.put(groupValue, newList);
                }
                ((List<T>) result.get(groupValue)).add(item);

            }

            int commaIndex = sel.getGroup().indexOf(',');
            if (commaIndex != -1) {
                sel.setGroup(sel.getGroup().substring(commaIndex + 1));
                for (Object key : result.keySet()) {
                    result.put(key, groupFieldsAsPerSelector((List<T>) result.get(key), sel));
                }
            }
        }
        catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            logger.error("Error grouping results", e);
        }
        return result;
    }

    protected String getCsvFromMapListAndFIQL(List<Map<String, Object>> maps, FIQLSelector selector) {
        ICsvMapWriter mapWriter = null;

        try {
            File file = File.createTempFile("csv", ".csv");

            mapWriter = new CsvMapWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

            String[] headers = (selector.getFields() + "," + selector.getGroup()).split(",");

            mapWriter.writeHeader(headers);

            for (Map<String, Object> map : maps) {
                mapWriter.write(map, headers);
            }

            mapWriter.close();
            FileReader fileReader = new FileReader(file);
            String result = IOUtils.toString(fileReader);
            fileReader.close();
            file.delete();
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    protected Object filterFields(Object object, Set<String> fields) {
        try {
            if (object == null)
                return null;

            Set<String> fieldSet = new HashSet<String>();
            SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter(
                    "fieldFilter",
                    SimpleBeanPropertyFilter.serializeAllExcept(fieldSet));

            if (fields != null && !fields.isEmpty()) {
                filterProvider = new SimpleFilterProvider().addFilter(
                        "fieldFilter",
                        SimpleBeanPropertyFilter.filterOutAllExcept(fields));
            }

            return mapper.readValue(mapper.writer(filterProvider).writeValueAsString(object), object.getClass());
        }
        catch (Exception e) {
            throw new ProAPIException("Could not serialize response", e);
        }
    }

    /**
     * This method parses the json String to specified java class type
     * 
     * @param content
     * @param valueType
     * @return
     */
    public <T> T parseJsonToObject(String content, Class<T> valueType) {
        // TODO Auto-generated method stub
        try {
            if (content != null) {
                return mapper.readValue(content, valueType);
            }
            return null;
        }
        catch (Exception e) {
            throw new ProAPIException("Could not parse request", e);
        }
    }

}
