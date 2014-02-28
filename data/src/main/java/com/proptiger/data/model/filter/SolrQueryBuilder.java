/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import com.proptiger.data.util.Constants;

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
    private Class<T>  modelClass;

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
        if (values.size() > 0 && values.get(0) instanceof String) {
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
        }
        else {
            from = quote + from + quote;
        }

        if (to == null) {
            to = "*";
        }
        else {
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

        /*
         * XXX - GEO DISTANCE HANDLING HAS TO BE DONE IN A CLEANER WAY.
         */
        String documentType = "";
        String[] filterQueries = solrQuery.getFilterQueries();
        for (int i = 0; i < filterQueries.length; i++) {
            if (filterQueries[i].startsWith("DOCUMENT_TYPE")) {
                documentType = filterQueries[i].substring("DOCUMENT_TYPE:".length());
                solrQuery.add("fl", "* __" + documentType + "_GEO_DISTANCE__:geodist()");
                break;
            }
        }

        // if valid distance value then apply the field.
        if (distance != 0) {
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
            if (paging != null) {
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
            FiqlParser<SearchBean> fiqlParser = new FiqlParser<SearchBean>(SearchBean.class);
            SearchCondition<SearchBean> searchCondition = fiqlParser.parse(selector.getFilters());
            LuceneQueryVisitor<SearchBean> luceneQueryVisitor = new LuceneQueryVisitor<SearchBean>(
                    getDaoFieldsMap(modelClass));
            luceneQueryVisitor.visit(searchCondition);
            org.apache.lucene.search.Query termQuery = luceneQueryVisitor.getQuery();
            solrQuery.addFilterQuery(termQuery.toString());
        }
    }

    @Override
    protected void buildOrderByClause(FIQLSelector selector) {
        if (selector != null && selector.getSort() != null) {
            for (String fieldName : selector.getSort().split(",")) {
                ORDER order = ORDER.asc;
                if (fieldName.startsWith("-")) {
                    order = ORDER.desc;
                    fieldName = fieldName.substring(1);
                }

                solrQuery.addSort(getDaoFieldsMap(modelClass).get(fieldName), order);
            }
        }
    }

    @Override
    protected void buildSelectClause(FIQLSelector selector) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void buildGroupByClause(FIQLSelector selector) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<T> retrieveResults() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long retrieveCount() {
        return 0;
    }

    @Override
    protected void validateSelector(Selector selector) {
        if (selector == null)
            return;
        /**
         * In case of Solr Dynamic Field like geoDistance sort, that Dynamic
         * Field like Geo Distance filter is required.
         */
        Set<SortBy> sort = selector.getSort();
        Map<String, List<Map<String, Map<String, Object>>>> filters = selector.getFilters();

        if (sort == null) {
            return;
        }

        Set<String> dynamicFieldsFound = new HashSet<>();
        for (SortBy sortBy : sort) {
            if (Constants.solrDynamicFields.containsKey(sortBy.getField())) {
                dynamicFieldsFound.add(sortBy.getField());

            }
        }

        if (dynamicFieldsFound.size() < 1) {
            return;
        }

        Iterator<String> it = dynamicFieldsFound.iterator();

        if (filters != null) {
            for (Map.Entry<String, List<Map<String, Map<String, Object>>>> operator : filters.entrySet()) {
                for (Map<String, Map<String, Object>> fields : operator.getValue()) {
                    while (it.hasNext()) {
                        dynamicFieldsFound.remove(it.next());
                    }
                    if (dynamicFieldsFound.size() < 1) {
                        return;
                    }
                }
            }
        }

        throw new IllegalArgumentException(
                dynamicFieldsFound.toString() + " sort should be present only when their filter is applied.");
    }
}
