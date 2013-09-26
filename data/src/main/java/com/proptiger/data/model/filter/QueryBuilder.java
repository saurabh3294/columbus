/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;
import java.util.Set;

import com.proptiger.data.pojo.SortBy;

/**
 * @author mandeep
 * 
 */
public interface QueryBuilder {
    void addEqualsFilter(String fieldName, List<Object> values);

    void addRangeFilter(String fieldName, Object from, Object to);

    void addSort(Set<SortBy> sortBySet);

    void addField(String fieldName);

    void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude);
    
    
}
