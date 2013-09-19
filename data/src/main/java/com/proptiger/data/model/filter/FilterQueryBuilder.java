/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

/**
 * @author mandeep
 * 
 */
public class FilterQueryBuilder {
    private static TypeConverter typeConvertor = new SimpleTypeConverter();
    private static enum Operator {
        and, range, equal, from, to, geoDistance, lat, lon, distance;
    }

    private static Logger logger = Logger.getLogger(FilterQueryBuilder.class);

    @SuppressWarnings("unchecked")
    public static void applyFilter(QueryBuilder queryBuilder, Object filterString, Class<?> modelClass) {
        logger.error(filterString);
        if (filterString == null) {
            return;
        }

        Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;
        List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());
        if (andFilters != null && filters.size() == 1) {
            for (Map<String, Map<String, Object>> andFilter : andFilters) {
                for (String operator : andFilter.keySet()) {
                    switch (Operator.valueOf(operator)) {
                    case equal:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            List<Object> objects = new ArrayList<Object>();
                            for (Object obj : (List<Object>) andFilter.get(operator).get(jsonFieldName)) {
                                objects.add(typeConvertor.convertIfNecessary(obj, field.getType()));
                            }
                            queryBuilder.addEqualsFilter(daoFieldName, objects);
                        }
                        break;

                    case range:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            Map<String, Object> obj = (Map<String, Object>) andFilter.get(operator).get(jsonFieldName);
                            queryBuilder.addRangeFilter(daoFieldName, typeConvertor.convertIfNecessary(obj.get(Operator.from.name()), field.getType()),
                                                                      typeConvertor.convertIfNecessary(obj.get(Operator.to.name()), field.getType()));
                        }
                        break;

                    case geoDistance:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName, queryBuilder.getAnnotationClassForColumnName());
                            Field field = FieldsMapLoader.getField(modelClass, jsonFieldName);

                            Map<String, Object> obj = (Map<String, Object>) andFilter.get(operator).get(jsonFieldName);
                            queryBuilder.addGeoFilter(daoFieldName, (Double) obj.get(Operator.distance.name()),
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
    }
}
