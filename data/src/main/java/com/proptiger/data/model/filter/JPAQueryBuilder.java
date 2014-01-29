package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.jpa.JPACriteriaQueryVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;

/**
 * This class provides implementation to build query specific to my sql
 * database.
 * 
 * @author Rajeev Pandey
 * 
 * @param <T>
 */
public class JPAQueryBuilder<T extends BaseModel> extends AbstractQueryBuilder<T> {
    private static ConcurrentMap<Class<?>, ConcurrentMap<String, String>> jsonNameToFieldNameMap = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(JPAQueryBuilder.class);

    private static enum FUNCTIONS {
        SUM, MIN, MAX, AVG, COUNT;
    };

    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private Root<T> root;
    private Class<T> domainClazz;
    private TypedQuery<Tuple> typedQuery;
    private CriteriaQuery<Tuple> criteriaQuery;

    /**
     * Loads a mapping between JSON serialized and field names for attributes of
     * our model
     * 
     * @param clazz
     */
    private static void loadJsonNameToFieldNameMap(Class<?> clazz) {
        ConcurrentMap<String, String> fieldsMap = new ConcurrentHashMap<>();
        for (Entry<String, Field> entry : FieldsMapLoader.getFieldMap(clazz).entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getName())) {
                fieldsMap.put(entry.getKey(), entry.getValue().getName());
            }

            // XXX - temporary fix as CXF FIQL parser is lowercasing all fields
            // :-(
            if (!entry.getKey().toLowerCase().equals(entry.getValue().getName())) {
                fieldsMap.put(entry.getKey().toLowerCase(), entry.getValue().getName());
            }
        }

        jsonNameToFieldNameMap.put(clazz, fieldsMap);
    }

    private static Map<String, String> getJsonNameToFieldNameMap(Class<?> clazz) {
        if (!jsonNameToFieldNameMap.containsKey(clazz)) {
            loadJsonNameToFieldNameMap(clazz);
        }

        return jsonNameToFieldNameMap.get(clazz);
    }

    // Constructor
    public JPAQueryBuilder(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
        domainClazz = clazz;
        criteriaQuery = criteriaBuilder.createTupleQuery();
        root = criteriaQuery.from(domainClazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void buildFilterClause(FIQLSelector selector) {
        if (selector != null && selector.getFilters() != null && !selector.getFilters().isEmpty()) {
            FiqlParser<T> fiqlParser = new FiqlParser<T>(domainClazz);
            SearchCondition<T> searchCondition = fiqlParser.parse(selector.getFilters());
            JPACriteriaQueryVisitor<T, Tuple> jpaCriteriaQueryVisitor = new JPACriteriaQueryVisitor<>(entityManager,
                    domainClazz, Tuple.class, getJsonNameToFieldNameMap(domainClazz));
            searchCondition.accept(jpaCriteriaQueryVisitor);
            criteriaQuery = jpaCriteriaQueryVisitor.getCriteriaQuery();
            for (Root<?> root : criteriaQuery.getRoots()) {
                if (root.getJavaType().equals(domainClazz)) {
                    this.root = (Root<T>) root;
                }
            }
        }
    }

    @Override
    protected void buildGroupByClause(FIQLSelector selector) {
        if (selector != null && selector.getGroup() != null && !selector.getGroup().isEmpty()) {
            for (String fieldName : selector.getGroup().split(",")) {
                List<Expression<?>> groupByList = new LinkedList<>(criteriaQuery.getGroupList());
                groupByList.add(root.get(fieldName));
                criteriaQuery.groupBy(groupByList);
                if (criteriaQuery.getSelection() != null) {
                    addSelection(fieldName, root.get(fieldName).alias(fieldName));
                } else {
                    criteriaQuery.select(criteriaBuilder.tuple(root.get(fieldName).alias(fieldName)));
                }
            }
        } else {
            root.alias("root");
            criteriaQuery.select(criteriaBuilder.tuple(root));
            criteriaQuery.groupBy(root);
        }
    }

    @Override
    protected void buildSelectClause(FIQLSelector selector) {
        if (selector != null && selector.getFields() != null && !selector.getFields().isEmpty()) {
            for (String fieldName : selector.getFields().split(",")) {
                try {
                    String prefix = StringUtils.splitByCharacterTypeCamelCase(fieldName)[0];
                    String actualFieldName = StringUtils.uncapitalize(fieldName.substring(prefix.length()));
                    Selection<?> selection = null;
                    switch (FUNCTIONS.valueOf(prefix.toUpperCase())) {
                    case MAX:
                        Expression<Number> maxExpression = root.get(actualFieldName);
                        selection = criteriaBuilder.max(maxExpression);
                        break;
                    case AVG:
                        Expression<Double> avgExpression = root.get(actualFieldName);
                        selection = criteriaBuilder.avg(avgExpression);
                        break;
                    case COUNT:
                        Expression<Number> countExpression = root.get(actualFieldName);
                        selection = criteriaBuilder.count(countExpression);
                        break;
                    case MIN:
                        Expression<Number> minExpression = root.get(actualFieldName);
                        selection = criteriaBuilder.min(minExpression);
                        break;
                    case SUM:
                        Expression<Number> sumExpression = root.get(actualFieldName);
                        selection = criteriaBuilder.sum(sumExpression);
                        break;
                    default:
                        throw new UnsupportedOperationException("Missing support for " + prefix + " function");
                    }

                    addSelection(fieldName, selection.alias(fieldName));
                } catch (UnsupportedOperationException e) {
                    logger.error(e.getMessage(), e);
                } catch (Exception e) {
                }
            }
            
            selector.setFields(selector.getFields() + ",extraAttributes");
        }
    }

    private void addSelection(String fieldName, Selection<?> selection) {
        List<Selection<?>> selections = new LinkedList<Selection<?>>();
        if (criteriaQuery.getSelection().isCompoundSelection()) {
            selections = new LinkedList<Selection<?>>(criteriaQuery.getSelection().getCompoundSelectionItems());
        }

        selections.add(selection);
        criteriaQuery.multiselect(selections);
    }

    @Override
    protected void buildOrderByClause(FIQLSelector selector) {
        if (selector != null && selector.getSort() != null) {
            for (String fieldName : selector.getSort().split(",")) {
                Order order = null;
                if (fieldName.startsWith("-")) {
                    order = criteriaBuilder.desc(root.get(FieldsMapLoader.getField(domainClazz, fieldName.substring(1))
                            .getName()));
                } else {
                    order = criteriaBuilder.asc(root.get(FieldsMapLoader.getField(domainClazz, fieldName).getName()));
                }

                criteriaQuery.orderBy(order);
            }
        }
    }

    @Override
    protected void buildLimitClause(FIQLSelector selector) {
        if (selector != null) {
            typedQuery = entityManager.createQuery(criteriaQuery).setFirstResult(selector.getStart())
                    .setMaxResults(selector.getRows());
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery);
        }
    }

    @Override
    public List<T> retrieveResults() {
        // Case where its simple query without any group by
        if (criteriaQuery.getGroupList().isEmpty()) {
            if (!criteriaQuery.getSelection().isCompoundSelection()) {
                List<T> results = new ArrayList<>();
                for (Tuple tuple : typedQuery.getResultList()) {
                    results.add(tuple.get(0, domainClazz));
                }

                return results;
            }
        }

        List<T> results = new ArrayList<>();
        for (Tuple tuple : typedQuery.getResultList()) {
            try {
                T result = domainClazz.newInstance();
                for (TupleElement<?> tupleElement : tuple.getElements()) {
                    if (tupleElement.getJavaType().equals(domainClazz)) {
                        result = tuple.get(tupleElement.getAlias(), domainClazz);
                    } else {
//                        try {
//                            BeanUtils.copyProperty(result, tupleElement.getAlias(), tuple.get(tupleElement));
//                        } catch (Exception e) {
                            result.getExtraAttributes().put(tupleElement.getAlias(), tuple.get(tupleElement));
//                        }
                    }
                }

                results.add(result);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Could not fetch attributes for tuple: " + tuple, e);
            }
        }

        return results;
    }

    @Override
    public long retrieveCount() {
        criteriaQuery.select(criteriaBuilder.tuple(criteriaBuilder.count(root)));
        return (long) entityManager.createQuery(criteriaQuery).getResultList().get(0).get(0);
    }

    public void addEqualsFilter(String fieldName, List<Object> values) {
        if (values != null) {
            if (values.size() == 1) {
                // equal clause
            } else if (values.size() > 1) {
                criteriaQuery.where(root.get(fieldName).in(values));
            }
        }

    }

    public void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude) {
        // TODO Auto-generated method stub

    }

    public CriteriaQuery<Tuple> getQuery() {
        return this.criteriaQuery;
    }

    @Override
    protected void buildSelectClause(Selector selector) {
        criteriaQuery.multiselect(root);
    }

    @Override
    protected void buildOrderByClause(Selector selector) {
        List<Order> orderByList = new ArrayList<Order>();
        if (selector != null && selector.getSort() != null) {
            for (SortBy sortBy : selector.getSort()) {
                switch (sortBy.getSortOrder()) {
                case ASC:
                    orderByList.add((criteriaBuilder.asc(root.get(sortBy.getField()))));
                    break;
                case DESC:
                    orderByList.add(criteriaBuilder.desc(root.get(sortBy.getField())));
                    break;
                default:
                    break;
                }
            }
        }
        criteriaQuery.orderBy(orderByList);
    }

    @Override
    protected void buildJoinClause(Selector selector) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void buildFilterClause(Selector selector, Integer userId) {
        List<Predicate> predicateList = new ArrayList<>();
        if (selector != null && selector.getFilters() != null) {
            Object filterString = selector.getFilters();
            // Map<AND/OR, List<Map<Operator, Map<fieldName, Values> > > >
            Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;

            List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());

            if (andFilters != null && filters.size() == 1) {
                for (Map<String, Map<String, Object>> andFilter : andFilters) {
                    for (String operator : andFilter.keySet()) {

                        Map<String, Object> fieldNameValueMap = andFilter.get(operator);

                        switch (Operator.valueOf(operator)) {

                        case in:
                        case equal:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                List<Object> valuesList = new ArrayList<Object>();
                                valuesList.addAll((List<Object>) fieldNameValueMap.get(jsonFieldName));
                                predicateList.add(getEqualsOrInPredicate(jsonFieldName, valuesList));
                            }
                            break;
                        case range:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {

                                Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);

                                if (obj != null) {
                                    predicateList.add(getRangePredicate(jsonFieldName, obj));
                                }
                                addRangeFilter(jsonFieldName, obj.get(Operator.from.name()),
                                        obj.get(Operator.to.name()));
                            }
                            break;

                        case geoDistance:
                            for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);

                                addGeoFilter(jsonFieldName, (Double) obj.get(Operator.distance.name()),
                                        (Double) obj.get(Operator.lat.name()), (Double) obj.get(Operator.lon.name()));
                            }
                            break;

                        default:
                            throw new IllegalArgumentException("Operator not supported yet");
                        }
                    }
                }
            }

        }
        // creating user based filtering
        if (userId != null) {
            predicateList.add(root.get("userId").in(userId));
        }
        criteriaQuery = criteriaQuery.where(predicateList.toArray(new Predicate[] {}));
    }

    private Predicate getRangePredicate(String jsonFieldName, Map<String, Object> obj) {
        Predicate predicate = null;

        Object from = obj.get(Operator.from.name());
        Object to = obj.get(Operator.to.name());

        if (from != null && to != null) {
        } else if (from != null) {

        } else if (to != null) {

        }
        return predicate;
    }

    private Predicate getEqualsOrInPredicate(String jsonFieldName, List<Object> valuesList) {
        if (valuesList == null || valuesList.size() == 0) {
            throw new IllegalArgumentException("Invalid number of values for operator");
        }
        Predicate predicate = null;
        if (valuesList.size() == 1) {
            predicate = criteriaBuilder.equal(root.get(jsonFieldName), valuesList.get(0));
        } else {
            predicate = root.get(jsonFieldName).in(valuesList);
        }

        return predicate;
    }

    @Override
    protected void buildGroupByClause(Selector selector) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<T> getModelClass() {
        // TODO Auto-generated method stub
        return this.domainClazz;
    }

    public void addRangeFilter(String fieldName, Object from, Object to) {

    }

    @Override
    protected void buildLimitClause(Selector selector) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addNotEqualsFilter(String fieldName, List<Object> values) {
        // TODO Auto-generated method stub

    }

}
