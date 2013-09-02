/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.util.StringUtils;

import com.proptiger.data.model.filter.SortQueryBuilder.SortOrder;

/**
 * @author mandeep
 *
 */
public class SolrQueryBuilder implements QueryBuilder {
    private SolrQuery solrQuery;

    public SolrQueryBuilder(SolrQuery solrQuery) {
        this.solrQuery = solrQuery;
    }
    
    /* (non-Javadoc)
     * @see com.proptiger.data.model.filter.QueryBuilder#addEqualsFilter(java.lang.String, java.lang.String[])
     */
    @Override
    public void addEqualsFilter(String fieldName, List<Object> values) {
        String quote = "";
        if (values.get(0) instanceof String) {
            quote = "\"";
        }

        String string = StringUtils.arrayToDelimitedString(values.toArray(), quote + " OR " + quote );
        solrQuery.addFilterQuery("{!tag=" + fieldName + "}" + fieldName + ":(" + quote + string + quote + ")");
    }

    /* (non-Javadoc)
     * @see com.proptiger.data.model.filter.QueryBuilder#addRangeFilter(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addRangeFilter(String fieldName, Object from, Object to) {
        if (from == null) {
            from = "*";
        }

        if (to == null) {
            to = "*";
        }
        
        if (fieldName != null) {
            solrQuery.addFilterQuery("{!tag=" + fieldName + "}" + fieldName + ":[" + from + " TO " + to + "]");
        }
    }

    @Override
    public void addSort(String fieldName, SortOrder valueOf) {
        switch (valueOf) {
        case asc:
            solrQuery.addSort(fieldName, ORDER.asc);
            break;
        case desc:
            solrQuery.addSort(fieldName, ORDER.desc);
            break;
        default:
            solrQuery.addSort(fieldName, ORDER.asc);
            break;
        }
    }

    @Override
    public void addField(String fieldName) {
        solrQuery.addField(fieldName);
    }
}
