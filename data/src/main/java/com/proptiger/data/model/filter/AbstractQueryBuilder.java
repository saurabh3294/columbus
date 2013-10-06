/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.util.LongToDateConverter;
import com.proptiger.data.util.StringToDateConverter;

/**
 * This class provides methods to build data store query. Individual data store
 * type must implement methods to provide specific implementation supported by
 * particular data store (mysql/oracle/solr/lucene) vendor.
 * 
 * 
 * @author Rajeev Pandey
 * 
 * @param <T>
 */
public abstract class AbstractQueryBuilder<T> {
    private static TypeConverter typeConverter = new SimpleTypeConverter();
    private static LongToDateConverter longToDateConverter = new LongToDateConverter();
    private static StringToDateConverter stringToDateConverter = new StringToDateConverter();
    
    public void buildQuery(Selector selector, Integer userId) {
        buildSelectClause(selector);
        buildOrderByClause(selector);
        buildFilterClause(selector, userId);
    }

    protected abstract void buildSelectClause(Selector selector);

    protected abstract void buildOrderByClause(Selector selector);

    protected abstract void buildJoinClause(Selector selector);

    protected abstract void buildLimitClause(Selector selector);

    /**
     * Override this method if where clause can not be called multiple times
     * like in Spring Data JPA criteria query
     * 
     * @param selector
     */
    @SuppressWarnings("unchecked")
    protected void buildFilterClause(Selector selector, Integer userId) {

        if (selector != null && selector.getFilters() != null) {
            Map<String, List<Map<String, Map<String, Object>>>> filters = selector.getFilters();
            List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());

            if (andFilters != null && filters.size() == 1) {
                for (Map<String, Map<String, Object>> andFilter : andFilters) {
                    for (String operator : andFilter.keySet()) {

                        Map<String, Object> fieldNameValueMap = andFilter.get(operator);

                        switch (Operator.valueOf(operator)) {

                        case equal:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                List<Object> valuesList = new ArrayList<Object>();
                                Field field = FieldsMapLoader.getField(getModelClass(), jsonFieldName);

                                Object object = fieldNameValueMap.get(jsonFieldName);
                                if (object instanceof List) {
                                    for (Object obj: (List<?>) object) {
                                        valuesList.add(convert(obj, field));
                                    }
                                } else {
                                    valuesList.add(convert(object, field));
                                }

                                addEqualsFilter(jsonFieldName, valuesList);
                            }
                            break;

                        case range:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                Field field = FieldsMapLoader.getField(getModelClass(), jsonFieldName);
                                Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
                                addRangeFilter(jsonFieldName, convert(obj.get(Operator.from.name()), field),
                                        convert(obj.get(Operator.to.name()), field));
                            }
                            break;

                        case geoDistance:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
                                addGeoFilter(jsonFieldName, (Double) obj.get(Operator.distance.name()),
                                        (Double) obj.get(Operator.lat.name()), (Double) obj.get(Operator.lon.name()));
                            }
                            break;

                        default:
                            throw new IllegalArgumentException("Operator not supported yet");
                        }
                    }
                }
            }
        }
    }

    private Object convert(Object obj, Field field) {
        if (obj == null) {
            return null;
        }

        if (field.getType().equals(Date.class)) {
            Date date = null;
            if (obj instanceof Long) {
                date = longToDateConverter.convert((Long)obj);
            }
            else if (obj instanceof String) {
                date = stringToDateConverter.convert((String)obj);
            }
            else {
                date = (Date)typeConverter.convertIfNecessary(obj, field.getType());                
            }

            return new ISO8601DateFormat().format(date);
        }

        return typeConverter.convertIfNecessary(obj, field.getType());
    }

    protected abstract void buildGroupByClause(Selector selector);

    protected abstract void addEqualsFilter(String fieldName, List<Object> values);

    protected abstract void addRangeFilter(String fieldName, Object from, Object to);

    protected abstract void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude);

    protected abstract Class<T> getModelClass();

}
