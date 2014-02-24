package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.jpa.JPACriteriaQueryVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cloudfront.model.InvalidArgumentException;
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
    private static Logger                                                 logger                 = LoggerFactory
                                                                                                         .getLogger(JPAQueryBuilder.class);

    private static enum FUNCTIONS {
        SUM, MIN, MAX, AVG, COUNT, COUNTDISTINCT, MEDIAN, WAVG;
    };

    private EntityManager        entityManager;
    private CriteriaBuilder      criteriaBuilder;
    private Root<T>              root;
    private Class<T>             domainClazz;
    private TypedQuery<Tuple>    typedQuery;
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
            JPACriteriaQueryVisitor<T, Tuple> jpaCriteriaQueryVisitor = new JPACriteriaQueryVisitor<>(
                    entityManager,
                    domainClazz,
                    Tuple.class,
                    getJsonNameToFieldNameMap(domainClazz));
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
                addToFields(selector, fieldName);
                Expression<?> expression = createExpression(fieldName);
                if (criteriaQuery.getSelection() != null) {
                    addSelection(expression);
                }
                else {
                    criteriaQuery.select(criteriaBuilder.tuple(expression));
                }
            }
        }
        else {
            root.alias("root");
            criteriaQuery.select(criteriaBuilder.tuple(root));
        }
    }

    @Override
    protected void buildSelectClause(FIQLSelector selector) {
        if (selector != null && selector.getFields() != null && !selector.getFields().isEmpty()) {
            for (String fieldName : selector.getFields().split(",")) {
                addSelection(createExpression(fieldName));
            }

            addToFields(selector, "extraAttributes");
        }
    }

    private Expression<?> createExpression(String fieldName) {
        String[] splitWords = StringUtils.splitByCharacterTypeCamelCase(fieldName);
        String prefix = splitWords[0];
        String actualFieldName = StringUtils.uncapitalize(fieldName.substring(prefix.length()));
        Expression<?> expression = null;
        try {
            switch (FUNCTIONS.valueOf(prefix.toUpperCase())) {
                case MAX:
                    Expression<Number> maxExpression = root.get(actualFieldName);
                    expression = criteriaBuilder.max(maxExpression);
                    break;
                case AVG:
                    Expression<Double> avgExpression = root.get(actualFieldName);
                    expression = criteriaBuilder.avg(avgExpression);
                    break;
                case WAVG:
                    String[] fieldNames = actualFieldName.split("On");
                    fieldNames[1] = StringUtils.uncapitalize(fieldNames[1]);
                    Expression<Double> field1 = root.get(fieldNames[0]);
                    Expression<Double> field2 = root.get(fieldNames[1]);
                    expression = criteriaBuilder.quot(
                            criteriaBuilder.sum(criteriaBuilder.prod(field1, field2)),
                            criteriaBuilder.sum(field2));
                    break;
                case MEDIAN:
                    Expression<Double> medianExpression = root.get(actualFieldName);
                    expression = criteriaBuilder.function("median", Double.class, medianExpression);
                    break;
                case COUNT:
                    Expression<Number> countExpression;
                    try {
                        countExpression = root.get(actualFieldName);
                        expression = criteriaBuilder.count(countExpression);
                    }
                    catch (IllegalArgumentException | IllegalStateException e) {
                        String secondPrefix = splitWords[1];
                        if (FUNCTIONS.COUNTDISTINCT.name().equalsIgnoreCase(prefix + secondPrefix)) {
                            actualFieldName = StringUtils
                                    .uncapitalize(actualFieldName.substring(secondPrefix.length()));
                            countExpression = root.get(actualFieldName);
                            expression = criteriaBuilder.countDistinct(countExpression);
                        }
                    }
                    break;
                case MIN:
                    Expression<Number> minExpression = root.get(actualFieldName);
                    expression = criteriaBuilder.min(minExpression);
                    break;
                case SUM:
                    Expression<Number> sumExpression = root.get(actualFieldName);
                    expression = criteriaBuilder.sum(sumExpression);
                    break;
                default:
                    throw new UnsupportedOperationException("Missing support for " + prefix + " function");
            }
        }
        catch (UnsupportedOperationException e) {
            logger.error(e.getMessage(), e);
        }
        catch (Exception e) {
            expression = root.get(fieldName);
        }

        expression.alias(fieldName);
        return expression;
    }

    private void addToFields(FIQLSelector selector, String fieldName) {
        if (selector.getFields() == null || selector.getFields().isEmpty()) {
            selector.setFields(fieldName);
        }
        else {
            selector.addField(fieldName);
        }
    }

    private void addSelection(Selection<?> selection) {
        List<Selection<?>> selections = new LinkedList<Selection<?>>();
        if (criteriaQuery.getSelection().isCompoundSelection()) {
            selections = new LinkedList<Selection<?>>(criteriaQuery.getSelection().getCompoundSelectionItems());
        }

        selections.add(selection);
        criteriaQuery.multiselect(selections);
    }

    @Override
    protected void buildOrderByClause(FIQLSelector selector) {
        if (selector != null && selector.getSort() != null && !selector.getSort().isEmpty()) {
            List<Order> orders = new ArrayList<>();
            for (String fieldName : selector.getSort().split(",")) {
                Order order = null;
                if (fieldName.startsWith("-")) {
                    order = criteriaBuilder.desc(createExpression(fieldName.substring(1)));
                }
                else {
                    order = criteriaBuilder.asc(createExpression(fieldName));
                }

                orders.add(order);
            }

            criteriaQuery.orderBy(orders);
        }
    }

    @Override
    protected void buildLimitClause(FIQLSelector selector) {
        if (selector != null && selector.getStart() != null && selector.getRows() != null) {
            typedQuery = entityManager.createQuery(criteriaQuery).setFirstResult(selector.getStart())
                    .setMaxResults(selector.getRows());
        }
        else {
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
                    }
                    else {
                        if (!set(result, tupleElement.getAlias(), tuple.get(tupleElement))) {
                            result.getExtraAttributes().put(tupleElement.getAlias(), tuple.get(tupleElement));
                        }
                    }
                }

                results.add(result);
            }
            catch (InstantiationException | IllegalAccessException e) {
                logger.error("Could not fetch attributes for tuple: " + tuple, e);
            }
        }

        return results;
    }

    /**
     * Copied from stackoverflow
     * 
     * @param object
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public long retrieveCount() {
        criteriaQuery.select(criteriaBuilder.tuple(criteriaBuilder.count(root)));
        List<Tuple> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        if (criteriaQuery.getGroupList().isEmpty()) {
            return (long) resultList.get(0).get(0);
        }
        else
            return (long) resultList.size();
    }

    public void addEqualsFilter(String fieldName, List<Object> values) {
        if (values != null) {
            if (values.size() == 1) {
                // equal clause
            }
            else if (values.size() > 1) {
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

                                    Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap
                                            .get(jsonFieldName);

                                    if (obj != null) {
                                        predicateList.add(getRangePredicate(jsonFieldName, obj));
                                    }
                                    addRangeFilter(
                                            jsonFieldName,
                                            obj.get(Operator.from.name()),
                                            obj.get(Operator.to.name()));
                                }
                                break;

                            case geoDistance:
                                for (String jsonFieldName : fieldNameValueMap.keySet()) {
                                    Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap
                                            .get(jsonFieldName);

                                    addGeoFilter(
                                            jsonFieldName,
                                            (Double) obj.get(Operator.distance.name()),
                                            (Double) obj.get(Operator.lat.name()),
                                            (Double) obj.get(Operator.lon.name()));
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
        }
        else if (from != null) {

        }
        else if (to != null) {

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
        }
        else {
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

    @Override
    protected void validateSelector(Selector selector) {
        // TODO Auto-generated method stub
    }
}
