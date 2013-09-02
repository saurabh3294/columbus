/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author mandeep
 * 
 */
public class SortQueryBuilder {
    private static Gson gson = new Gson();
    public static enum SortOrder {
        asc, desc;
    }

    public static void applySort(QueryBuilder queryBuilder, String sortString, Class<?> modelClass) {
        if (sortString == null || sortString.isEmpty()) {
            return;
        }
        
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        List<Map<String, String>> sortCriteria = gson.fromJson(sortString, type);
        for (Map<String, String> sortCriterion : sortCriteria) {
            for (String fieldName : sortCriterion.keySet()) {
                queryBuilder.addSort(FieldsMapLoader.getDaoFieldName(modelClass, fieldName), SortOrder.valueOf(sortCriterion.get(fieldName)));
            }
        }
    }
}
