/**
 * 
 */
package com.proptiger.data.model.filter;

import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 * 
 */
public class SortQueryBuilder {

    public static void applySort(QueryBuilder queryBuilder, Selector selector) {
        if (selector == null || selector.getSort() == null) {
            return;
        }
        
		queryBuilder.addSort(selector.getSort());
    }
}
