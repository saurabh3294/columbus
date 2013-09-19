/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.util.List;

import com.proptiger.data.pojo.SortOrder;

/**
 * @author mandeep
 * 
 */
public interface QueryBuilder {
    void addEqualsFilter(String fieldName, List<Object> values);

    void addRangeFilter(String fieldName, Object from, Object to);

    void addSort(String fieldName, SortOrder sortOrder);

    void addField(String fieldName);

    Class<? extends Annotation> getAnnotationClassForColumnName();

    void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude);
}
