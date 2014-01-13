package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.jpa.JPATypedQueryVisitor;

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
public class MySqlQueryBuilder<T> extends AbstractQueryBuilder<T> {

    private static ConcurrentMap<Class<?>, ConcurrentMap<String, String>> fieldToDaoFieldMap = new ConcurrentHashMap<>();

    private static void loadDaoFieldsMap(Class<?> clazz) {
        ConcurrentMap<String, String> fieldsMap = new ConcurrentHashMap<>();
        for (Entry<String, Field> entry : FieldsMapLoader.getFieldMap(clazz).entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getName())) {
                fieldsMap.put(entry.getKey(), entry.getValue().getName());
            }
        }

        System.out.println(fieldsMap);
        fieldToDaoFieldMap.put(clazz, fieldsMap);
    }

    private static Map<String, String> getDaoFieldsMap(Class<?> clazz) {
        if (!fieldToDaoFieldMap.containsKey(clazz)) {
            loadDaoFieldsMap(clazz);
        }

        return fieldToDaoFieldMap.get(clazz);
    }

    @Deprecated
    private CriteriaBuilder builder;
    private Class<T> domainClazz;
    private CriteriaQuery<T> query;

    @Deprecated
    private Root<T> root;
    private EntityManager entityManager;
    private TypedQuery<T> typedQuery;

    public MySqlQueryBuilder(CriteriaBuilder builder, Class<T> clazz) {
        this.builder = builder;
        domainClazz = clazz;
        query = builder.createQuery(domainClazz);
        root = query.from(domainClazz);

    }

    public MySqlQueryBuilder(EntityManager entityManager, Class<T> clazz) {
        domainClazz = clazz;
        this.entityManager = entityManager;
    }

    public void addEqualsFilter(String fieldName, List<Object> values) {
        if (values != null) {
            if (values.size() == 1) {
                // equal clause
            } else if (values.size() > 1) {
                query.where(root.get(fieldName).in(values));
            }
        }

    }

    public void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude) {
        // TODO Auto-generated method stub

    }

    public CriteriaQuery<T> getQuery() {
        return this.query;
    }

    @Override
    protected void buildSelectClause(Selector selector) {
        query.select(root);
    }

    @Override
    protected void buildOrderByClause(Selector selector) {
        List<Order> orderByList = new ArrayList<Order>();
        if (selector != null && selector.getSort() != null) {
            for (SortBy sortBy : selector.getSort()) {
                switch (sortBy.getSortOrder()) {
                case ASC:
                    orderByList.add((builder.asc(root.get(sortBy.getField()))));
                    break;
                case DESC:
                    orderByList.add(builder.desc(root.get(sortBy.getField())));
                    break;
                default:
                    break;
                }
            }
        }
        query.orderBy(orderByList);
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
        query = query.where(predicateList.toArray(new Predicate[] {}));
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
            predicate = builder.equal(root.get(jsonFieldName), valuesList.get(0));
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

    @Override
    protected void buildLimitClause(FIQLSelector selector) {
        if (selector != null) {
            setTypedQuery(entityManager.createQuery(query).setFirstResult(selector.getStart())
                    .setMaxResults(selector.getRows()));
        } else {
            setTypedQuery(entityManager.createQuery(query));
        }
    }

    @Override
    protected void buildFilterClause(FIQLSelector selector) {
        if (selector != null && selector.getFilters() != null && !selector.getFilters().isEmpty()) {
            SearchCondition<T> searchCondition = new FiqlParser<T>(domainClazz).parse(selector.getFilters());
            JPATypedQueryVisitor<T> jpaCriteriaQueryVisitor = new JPATypedQueryVisitor<>(entityManager, domainClazz, getDaoFieldsMap(domainClazz));
            searchCondition.accept(jpaCriteriaQueryVisitor);
            query = jpaCriteriaQueryVisitor.getCriteriaQuery();
        }
    }

    @Override
    protected void buildOrderByClause(FIQLSelector selector) {
        if (selector != null && selector.getSort() != null) {
            for (String fieldName : selector.getSort().split(",")) {
                Order order = null;
                Root<T> rootLocal = query.from(domainClazz);
                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
                if (fieldName.startsWith("-")) {
                    order = criteriaBuilder.desc(rootLocal.get(FieldsMapLoader.getField(domainClazz, fieldName.substring(1)).getName()));
                } else {
                    order = criteriaBuilder.asc(rootLocal.get(FieldsMapLoader.getField(domainClazz, fieldName).getName()));
                }

                query.orderBy(order);
            }
        }
    }

    public TypedQuery<T> getTypedQuery() {
        return typedQuery;
    }

    public void setTypedQuery(TypedQuery<T> typedQuery) {
        this.typedQuery = typedQuery;
    }
}
