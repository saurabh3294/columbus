/**
 * 
 */
package com.proptiger.data.model.filter;

/**
 * @author mandeep
 *
 */
public interface QueryBuilder {
    void addEqualsFilter(String fieldName, String[] value);
    void addRangeFilter(String fieldName, String from, String to);
}
