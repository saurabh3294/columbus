/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.Set;

import com.proptiger.data.pojo.SortBy;

/**
 * @author mandeep
 * 
 */
public class SortQueryBuilder {

    public static void applySort(QueryBuilder queryBuilder, Set<SortBy> sortBy, Class<?> modelClass) {
        if (sortBy == null) {
            return;
        }
        
		for (SortBy sortCriterion : sortBy) {
			queryBuilder.addSort(
					FieldsMapLoader.getDaoFieldName(modelClass,
							sortCriterion.getField(), queryBuilder.getAnnotationClassForColumnName()),	sortCriterion.getSortOrder());
		}
    }
}
