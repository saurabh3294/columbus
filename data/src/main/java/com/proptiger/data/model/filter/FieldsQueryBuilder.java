/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.Set;




/**
 * @author mandeep
 * 
 */
public class FieldsQueryBuilder {
    public static void applyFields(QueryBuilder queryBuilder, Set<String> fieldString, Class<?> modelClass) {
        if (fieldString == null || fieldString.size() == 0) {
            return;
        }

        for (String fieldName : fieldString) {
            queryBuilder.addField(FieldsMapLoader.getDaoFieldName(modelClass, fieldName));
        }
    }
}
