/**
 * 
 */
package com.proptiger.data.model.filter;




/**
 * @author mandeep
 * 
 */
public class FieldsQueryBuilder {
    public static void applyFields(QueryBuilder queryBuilder, String fieldString, Class<?> modelClass) {
        if (fieldString == null || fieldString.isEmpty()) {
            return;
        }

        for (String fieldName : fieldString.split(",")) {
            queryBuilder.addField(FieldsMapLoader.getDaoFieldName(modelClass, fieldName));
        }
    }
}
