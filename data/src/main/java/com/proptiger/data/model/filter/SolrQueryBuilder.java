/**
 * 
 */
package com.proptiger.data.model.filter;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.util.StringUtils;

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
    public void addEqualsFilter(String fieldName, String[] value) {
        // TODO Auto-generated method stub
        if (value != null) {
            String string = StringUtils.arrayToDelimitedString(value, "\" OR \"");
            solrQuery.addFilterQuery("{!tag=" + fieldName + "}" + fieldName + ":(\"" + string + "\")");
        }
    }

    /* (non-Javadoc)
     * @see com.proptiger.data.model.filter.QueryBuilder#addRangeFilter(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addRangeFilter(String fieldName, String from, String to) {
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
}
