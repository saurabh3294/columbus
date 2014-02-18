/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mandeep
 * 
 */
public class FieldsMapLoader {
    static private ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> fieldsMap = new ConcurrentHashMap<>();

    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        return fieldsMap.get(clazz);
    }

    public static String getDaoFieldName(Class<?> clazz, String name) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        Field field = fieldsMap.get(clazz).get(name);
        Annotation fieldAnnotation = field.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);

        if (fieldAnnotation == null) {
            return field.getName();
        }

        return (String) AnnotationUtils.getAnnotationAttributes(fieldAnnotation).get("value");
    }

    public static Field getField(Class<?> clazz, String name) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        return fieldsMap.get(clazz).get(name);
    }

    private static void loadClassFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(JsonProperty.class);
            fieldsMap.putIfAbsent(clazz, new ConcurrentHashMap<String, Field>());

            if (annotation != null) {
                fieldsMap.get(clazz).putIfAbsent(
                        (String) AnnotationUtils.getAnnotationAttributes(annotation).get("value"),
                        field);
            }
            else {
                fieldsMap.get(clazz).putIfAbsent(field.getName(), field);
            }
        }
    }
}
