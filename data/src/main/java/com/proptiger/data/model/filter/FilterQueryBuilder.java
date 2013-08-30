/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.model.Property;

/**
 * @author mandeep
 * 
 */
public class FilterQueryBuilder<T, Q extends QueryBuilder> {
    private static Gson gson = new Gson();
    private Class<T> modelClass;
    private static enum Operator {
        and, range, equal, from, to;
    }

    public void applyFilter(Q queryBuilder, String filterString) {
        Type type = new TypeToken<Map<String, List<Map<String, Map<String, Object>>>>>() {
        }.getType();
        Map<String, List<Map<String, Map<String, Object>>>> filters = gson.fromJson(filterString, type);
        List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());
        if (andFilters != null && filters.size() == 1) {
            for (Map<String, Map<String, Object>> andFilter : andFilters) {
                for (String operator : andFilter.keySet()) {
                    switch (Operator.valueOf(operator)) {
                    case equal:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getFieldName(modelClass, jsonFieldName);
                            Object obj = andFilter.get(operator).get(jsonFieldName);
                            if (daoFieldName != null) {
                                if (obj instanceof String[]) {
                                    queryBuilder.addEqualsFilter(daoFieldName, (String[])obj);
                                }
                                else {
                                    // throw exception
                                }
                            }
                        }
                        break;

                    case range:
                        for (String jsonFieldName : andFilter.get(operator).keySet()) {
                            String daoFieldName = FieldsMapLoader.getFieldName(Property.class, jsonFieldName);
                            @SuppressWarnings("unchecked")
                            Map<String, String> obj = (Map<String, String>) andFilter.get(operator).get(jsonFieldName);
                            if (daoFieldName != null) {
                                queryBuilder.addRangeFilter(daoFieldName, obj.get(Operator.from.name()), obj.get(Operator.to.name()));
                            }
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
