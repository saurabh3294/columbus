/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 * 
 */
@Component
public class FilterQueryBuilder {
	
    @Autowired
    private ConversionService typeConvertor;

    private static Logger logger = LoggerFactory.getLogger(FilterQueryBuilder.class);

    @SuppressWarnings("unchecked")
    public void applyFilter(AbstractQueryBuilder queryBuilder, Selector selector, Class<?> modelClass) {/*
       
        if (selector == null || selector.getFilters() == null) {
            return;
        }
        Object filterString = selector.getFilters();
       
       //Map<AND/OR, List<Map<Operator, Map<fieldName, Values> > > >
        Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;
        
        List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());
        
        if (andFilters != null && filters.size() == 1) {
            for (Map<String, Map<String, Object>> andFilter : andFilters) {
                for (String operator : andFilter.keySet()) {
                	
                    Map<String, Object> fieldNameValueMap = andFilter.get(operator);
                    
					switch (Operator.valueOf(operator)) {
                    
                    case equal:
                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            List<Object> valuesList = new ArrayList<Object>();
                            for (Object obj : (List<Object>) fieldNameValueMap.get(jsonFieldName)) {
                                valuesList.add(typeConvertor.convert(obj, field.getType()));
                            }
                            queryBuilder.addEqualsFilter(jsonFieldName, valuesList);
                        }
                        break;

                    case range:
                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
                            queryBuilder.addRangeFilter(jsonFieldName, typeConvertor.convert(obj.get(Operator.from.name()), field.getType()),
                                                                      typeConvertor.convert(obj.get(Operator.to.name()), field.getType()));
                        }
                        break;
                    
                    case geoDistance:
                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
                            Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
                            queryBuilder.addGeoFilter(jsonFieldName, (Double) obj.get(Operator.distance.name()),
                                                                    (Double) obj.get(Operator.lat.name()),
                                                                    (Double) obj.get(Operator.lon.name()));
                        }
                        break;

                    default:
                        // throw exception
                        break;
                    }
                }
            }
        }
    */}
    
    
}
