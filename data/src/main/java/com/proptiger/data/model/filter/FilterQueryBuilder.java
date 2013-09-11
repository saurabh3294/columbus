/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.model.Property;

/**
 * @author mandeep
 * 
 */
public class FilterQueryBuilder {
    private static Gson gson = new Gson();
    private static TypeConverter typeConvertor = new SimpleTypeConverter();
    private static enum Operator {
        and, range, equal, from, to;
    }

    @SuppressWarnings("unchecked")
    public static void applyFilter(QueryBuilder queryBuilder, String filterString, Class<?> modelClass) {
        if (filterString == null || filterString.isEmpty()) {
            return;
        }
        
        Type type = new TypeToken<Map<String, List<Map<String, Map<String, Object>>>>>() {}.getType();
        Map<String, List<Map<String, Map<String, Object>>>> filters = gson.fromJson(filterString, type);
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

                    default:
                        // throw exception
                        break;
                    }
                }
            }
        }
    }
}
