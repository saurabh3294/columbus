/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;

/**
 * @author mandeep
 *
 */
public interface QueryBuilder {
    void addEqualsFilter(String fieldName, List<Object> values);
    void addRangeFilter(String fieldName, Object from, Object to);
}
