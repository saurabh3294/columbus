/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.lucene.LuceneQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;

/**
 * This class is responsible to create all clauses of solr query
 * 
 * @author Mandeep
 * @author Rajeev Pandey
 * 
 * @param <T>
 *
 */
/**
 
 */
public class SolrQueryBuilder<T> extends AbstractQueryBuilder<T> {

    private static ConcurrentMap<Class<?>, ConcurrentMap<String, String>> fieldToDaoFieldMap = new ConcurrentHashMap<>();

    private static void loadDaoFieldsMap(Class<?> clazz) {
        ConcurrentMap<String, String> fieldsMap = new ConcurrentHashMap<>();
        for (Entry<String, Field> entry : FieldsMapLoader.getFieldMap(clazz).entrySet()) {
            Annotation fieldAnnotation = entry.getValue().getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
            String daoFieldName = null;
            
            if (fieldAnnotation == null) {
                daoFieldName = entry.getValue().getName();
            }
            else {
                daoFieldName = (String) AnnotationUtils.getAnnotationAttributes(fieldAnnotation).get("value");
            }
            
            fieldsMap.put(entry.getKey(), daoFieldName);
        }

        fieldToDaoFieldMap.put(clazz, fieldsMap);
    }

    private static Map<String, String> getDaoFieldsMap(Class<?> clazz) {
        if (!fieldToDaoFieldMap.containsKey(clazz)) {
            loadDaoFieldsMap(clazz);
        }
        
        return fieldToDaoFieldMap.get(clazz);
    }

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
        if (values.size() >0 && values.get(0) instanceof String) {
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
        
        solrQuery.add("pt", latitude + "," + longitude);
        solrQuery.add("sfield", colName);
        // if valid distance value then apply the field.
        if(distance!=0)
        {
        	solrQuery.addFilterQuery("{!geofilt}");
        	solrQuery.add("d", String.valueOf(distance));
        }
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
        if (selector != null) {
            Paging paging = selector.getPaging();
            if(paging != null) {
                this.solrQuery.setStart(paging.getStart());

                if (paging.getRows() > 0) {
                    this.solrQuery.setRows(paging.getRows());
                }
            }
        }
    }

    @Override
    protected void buildLimitClause(FIQLSelector selector) {
        solrQuery.setStart(selector.getStart());
        solrQuery.setRows(selector.getRows());
    }

    @Override
    protected void buildFilterClause(FIQLSelector selector) {
        if (selector != null && selector.getFilters() != null && !selector.getFilters().isEmpty()) {
            FiqlParser<SearchBean> fiqlParser = new FiqlParser<SearchBean>(SearchBean.class, Collections.singletonMap(FiqlParser.SUPPORT_SINGLE_EQUALS, Boolean.TRUE.toString()));
            SearchCondition<SearchBean> searchCondition = fiqlParser.parse(selector.getFilters());
            LuceneQueryVisitor<SearchBean> luceneQueryVisitor = new LuceneQueryVisitor<SearchBean>(getDaoFieldsMap(modelClass));
            luceneQueryVisitor.visit(searchCondition);
            org.apache.lucene.search.Query termQuery = luceneQueryVisitor.getQuery();
            solrQuery.addFilterQuery(termQuery.toString());
        }
    }

    @Override
    protected void buildOrderByClause(FIQLSelector selector) {
        if (selector != null && selector.getSort() != null) {
            for (String fieldName: selector.getSort().split(",")) {
                ORDER order = ORDER.asc;
                if (fieldName.startsWith("-")) {
                    order = ORDER.desc;
                    fieldName = fieldName.substring(1);
                }

                solrQuery.addSort(getDaoFieldsMap(modelClass).get(fieldName), order);
            }
        }
    }
}
