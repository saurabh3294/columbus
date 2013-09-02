/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;

import com.proptiger.data.model.filter.SortQueryBuilder.SortOrder;

/**
 * @author mandeep
 *
 */
public interface QueryBuilder {
    void addEqualsFilter(String fieldName, List<Object> values);
    void addRangeFilter(String fieldName, Object from, Object to);
    void addSort(String fieldName, SortOrder valueOf);
    void addField(String fieldName);
}
