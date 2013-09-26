/**
 * 
 */
package com.proptiger.data.model.filter;

import com.proptiger.data.pojo.Selector;




/**
 * @author mandeep
 * 
 */
public class FieldsQueryBuilder {
    public static void applyFields(QueryBuilder queryBuilder, Selector selector) {
        if (selector == null || selector.getFields() == null) {
            return;
        }

        for (String fieldName : selector.getFields()) {
            queryBuilder.addField(fieldName);
        }
    }
}
