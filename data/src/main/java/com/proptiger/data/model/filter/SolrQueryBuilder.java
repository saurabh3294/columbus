/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.util.StringUtils;

import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;

/**
 * This class is responcible to create all clauses of solr query
 * 
 * @author mandeep
 * @author Rajeev Pandey
 *
 * @param <T>
 *
 */
/**
 
 */
public class SolrQueryBuilder<T> extends AbstractQueryBuilder<T> {
    
    private SolrQuery solrQuery;
    private Class<T> modelClass;
    
    public SolrQueryBuilder(SolrQuery solrQuery, Class<T> clazz) {
        this.solrQuery = solrQuery;
        this.modelClass = clazz;
    }
    
    @Override
    protected void addEqualsFilter(String fieldName, List<Object> values) {
    	String colName = getColumnName(fieldName);
        String quote = "";
        if (values.get(0) instanceof String) {
            quote = "\"";
        }

        String string = StringUtils.arrayToDelimitedString(values.toArray(), quote + " OR " + quote );
        solrQuery.addFilterQuery("{!tag=" + colName + "}" + colName + ":(" + quote + string + quote + ")");
    }

    @Override
    protected void addRangeFilter(String fieldName, Object from, Object to) {
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
    protected  void addGeoFilter(String fieldName, double distance, double latitude, double longitude) {
    	String colName = getColumnName(fieldName);
        solrQuery.addFilterQuery("{!geofilt}");
        solrQuery.add("pt", latitude + "," + longitude);
        solrQuery.add("sfield", colName);
        solrQuery.add("d", String.valueOf(distance));
    }

    private String getColumnName(String jsonFieldName){
    	return FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName);
    }

	@Override
	protected void buildSelectClause(Selector selector) {
		
		for(String jsonFieldName: selector.getFields()){
			String colName = getColumnName(jsonFieldName);
	        solrQuery.addField(colName);
		}
		
	}

	@Override
	protected void buildOrderByClause(Selector selector) {
		
		for(SortBy sortBy : selector.getSort()){
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
	protected void buildJoinClause(Selector selector) {
		
	}

	@Override
	protected void buildGroupByClause(Selector selector) {
		
	}

	@Override
	protected Class<T> getModelClass() {
		return this.modelClass;
	}
}
