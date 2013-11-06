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
    public void addNotEqualsFilter(String fieldName, List<Object> values) {
        String colName = getColumnName(fieldName);
        String quote = "";
        if (values.get(0) instanceof String) {
            quote = "\"";
        }

        String string = StringUtils.arrayToDelimitedString(values.toArray(), quote + " OR " + quote);
        solrQuery.addFilterQuery("-" + colName + ":(" + quote + string + quote + ")");
    }
    
    @Override
    public void addEqualsFilter(String fieldName, List<Object> values) {
        String colName = getColumnName(fieldName);
        String quote = "";
        if (values.get(0) instanceof String) {
            quote = "\"";
        }

        String string = StringUtils.arrayToDelimitedString(values.toArray(), quote + " OR " + quote);
        solrQuery.addFilterQuery("{!tag=" + colName + "}" + colName + ":(" + quote + string + quote + ")");
    }

    @Override
    public void addRangeFilter(String fieldName, Object from, Object to) {
        String colName = getColumnName(fieldName);

        String quote = "";
        if (from instanceof String || to instanceof String) {
            quote = "\"";
        }

        if (from == null) {
            from = "*";
        } else {
            from = quote + from + quote;
        }

        if (to == null) {
            to = "*";
        } else {
            to = quote + to + quote;
        }

        if (colName != null) {
            solrQuery.addFilterQuery("{!tag=" + colName + "}" + colName + ":[" + from + " TO " + to + "]");
        }
    }

    @Override
    public void addGeoFilter(String fieldName, double distance, double latitude, double longitude) {
        String colName = getColumnName(fieldName);
        solrQuery.addFilterQuery("{!geofilt}");
        solrQuery.add("pt", latitude + "," + longitude);
        solrQuery.add("sfield", colName);
        solrQuery.add("d", String.valueOf(distance));
    }

    private String getColumnName(String jsonFieldName) {
        return FieldsMapLoader.getDaoFieldName(modelClass, jsonFieldName);
    }

    @Override
    protected void buildSelectClause(Selector selector) {
        return;
        // if(selector != null && selector.getFields() != null){
        // for(String jsonFieldName: selector.getFields()){
        // String colName = getColumnName(jsonFieldName);
        // solrQuery.addField(colName);
        // }
        // }
    }

    @Override
    public void buildOrderByClause(Selector selector) {
        if (selector != null && selector.getSort() != null) {
            for (SortBy sortBy : selector.getSort()) {
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
    }

    @Override
    public void buildJoinClause(Selector selector) {

    }

    @Override
    public void buildGroupByClause(Selector selector) {

    }

    @Override
    public Class<T> getModelClass() {
        return this.modelClass;
    }

    @Override
    public void buildLimitClause(Selector selector) {
        // TODO Auto-generated method stub

    }
}
