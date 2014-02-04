/**
 * 
 */
package com.proptiger.data.util;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.cxf.jaxrs.ext.search.ConditionType;
import org.apache.cxf.jaxrs.ext.search.jpa.JPACriteriaQueryVisitor;

/**
 * @author mandeep
 * @param <E>
 * 
 */
public class CustomJPATypedQueryVisitor<T, E> extends JPACriteriaQueryVisitor<T, E> {
    private CriteriaBuilder builder;

    public CustomJPATypedQueryVisitor(EntityManager em, Class<T> tClass, Class<E> queryClass,
            Map<String, String> fieldMap) {
        super(em, tClass, queryClass, fieldMap);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Predicate doBuildPredicate(ConditionType ct, Path path, Class<?> valueClazz, Object value) {

        Class<? extends Comparable> clazz = (Class<? extends Comparable>) valueClazz;
        Expression<? extends Comparable> exp = path;

        Predicate pred = null;
        switch (ct) {
        case GREATER_THAN:
            pred = builder.greaterThan(exp, clazz.cast(value));
            break;
        case EQUALS:
            if (clazz.equals(String.class)) {
                String theValue = value.toString();
                if (theValue.contains("*")) {
                    theValue = ((String) value).replaceAll("\\*", "");
                }
                pred = builder.like((Expression<String>) exp, "%" + theValue + "%");
            } else {
                pred = builder.equal(exp, clazz.cast(value));
            }
            break;
        case NOT_EQUALS:
            pred = builder.notEqual(exp, clazz.cast(value));
            break;
        case LESS_THAN:
            pred = builder.lessThan(exp, clazz.cast(value));
            break;
        case LESS_OR_EQUALS:
            pred = builder.lessThanOrEqualTo(exp, clazz.cast(value));
            break;
        case GREATER_OR_EQUALS:
            pred = builder.greaterThanOrEqualTo(exp, clazz.cast(value));
            break;
        default:
            break;
        }
        return pred;
    }
}
