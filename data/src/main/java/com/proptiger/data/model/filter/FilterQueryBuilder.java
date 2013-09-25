/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

/**
 * @author mandeep
 * 
 */
@Component
public class FilterQueryBuilder {
    @Autowired
    private ConversionService typeConvertor;

    private static Logger logger = Logger.getLogger(FilterQueryBuilder.class);

    @SuppressWarnings("unchecked")
    public void applyFilter(QueryBuilder queryBuilder, Object filterString, Class<?> modelClass) {
        logger.error(filterString);
        if (filterString == null) {
            return;
        }

        Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;
        
        List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.AND);
        
        if (andFilters != null && filters.size() == 1) {
            for (Map<String, Map<String, Object>> andFilter : andFilters) {
                for (String operator : andFilter.keySet()) {
                    switch (Operator.valueOf(operator)) {
                    
                    case EQUAL:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            List<Object> objects = new ArrayList<Object>();
                            for (Object obj : (List<Object>) andFilter.get(operator).get(jsonFieldName)) {
                                objects.add(typeConvertor.convert(obj, field.getType()));
                            }
                            queryBuilder.addEqualsFilter(daoFieldName, objects);
                        }
                        break;

                    case RANGE:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            Map<String, Object> obj = (Map<String, Object>) andFilter.get(operator).get(jsonFieldName);
                            queryBuilder.addRangeFilter(daoFieldName, typeConvertor.convert(obj.get(Operator.FROM), field.getType()),
                                                                      typeConvertor.convert(obj.get(Operator.TO), field.getType()));
                        }
                        break;

                    case GEODISTANCE:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());

                            Map<String, Object> obj = (Map<String, Object>) andFilter.get(operator).get(jsonFieldName);
                            queryBuilder.addGeoFilter(daoFieldName, (Double) obj.get(Operator.DISTANCE),
                                                                    (Double) obj.get(Operator.LAT),
                                                                    (Double) obj.get(Operator.LON));
                        }
                        break;

                    default:
                        // throw exception
                        break;
                    }
                }
            }
        }
    }
    
    
}
