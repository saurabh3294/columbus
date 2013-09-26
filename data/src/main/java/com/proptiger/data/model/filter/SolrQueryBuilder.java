/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.util.StringUtils;

import com.proptiger.data.pojo.SortBy;

/**
 * @author mandeep
 *
 */
public class SolrQueryBuilder<T> implements QueryBuilder {
    
    private SolrQuery solrQuery;
    private Class<T> modelClass;
    
    public SolrQueryBuilder(SolrQuery solrQuery, Class<T> clazz) {
        this.solrQuery = solrQuery;
        this.modelClass = clazz;
    }
    
    /* (non-Javadoc)
     * @see com.proptiger.data.model.filter.QueryBuilder#addEqualsFilter(java.lang.String, java.lang.String[])
     */
    @Override
    public void addEqualsFilter(String fieldName, List<Object> values) {
    	String colName = getColumnName(fieldName);
        String quote = "";
        if (values.get(0) instanceof String) {
            quote = "\"";
        }

        String string = StringUtils.arrayToDelimitedString(values.toArray(), quote + " OR " + quote );
        solrQuery.addFilterQuery("{!tag=" + colName + "}" + colName + ":(" + quote + string + quote + ")");
    }

    /* (non-Javadoc)
     * @see com.proptiger.data.model.filter.QueryBuilder#addRangeFilter(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addRangeFilter(String fieldName, Object from, Object to) {
    	String colName = getColumnName(fieldName);
        if (from == null) {
            from = "*";
        }

        if (to == null) {
            to = "*";
        }
        
        if (colName != null) {
            solrQuery.addFilterQuery("{!tag=" + colName + "}" + colName + ":[" + from + " TO " + to + "]");
        }
    }

    @Override
    public void addSort(Set<SortBy> sortBySet) {
    	for(SortBy sortBy : sortBySet){
    		String colName = getColumnName(sortBy.getField());
            switch (sortBy.getSortOrder()) {
            case ASC:
                solrQuery.addSort(colName, ORDER.asc);
                break;
            case DESC:
                solrQuery.addSort(colName, ORDER.desc);
                break;
            default:
                solrQuery.addSort(colName, ORDER.asc);
                break;
            }
    	}
    	
    }

    @Override
    public void addField(String fieldName) {
    	String colName = getColumnName(fieldName);
        solrQuery.addField(colName);
    }
   

    @Override
    public void addGeoFilter(String fieldName, double distance, double latitude, double longitude) {
    	String colName = getColumnName(fieldName);
        solrQuery.addFilterQuery("{!geofilt}");
        solrQuery.add("pt", latitude + "," + longitude);
        solrQuery.add("sfield", colName);
        solrQuery.add("d", String.valueOf(distance));
    }

    private String getColumnName(String jsonFieldName){
    	return FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName);
    }
}
