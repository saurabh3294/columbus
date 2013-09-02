/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mandeep
 *
 */
public class FieldsMapLoader {
    static ConcurrentHashMap<Class<?>, Map<String, Field>> fieldsMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();

    public static String getDaoFieldName(Class<?> clazz, String name) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        Annotation fieldAnnotation = fieldsMap.get(clazz).get(name).getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
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
            if (annotation != null) {
                fieldsMap.putIfAbsent(clazz, new ConcurrentHashMap<String, Field>());
                fieldsMap.get(clazz).put((String) AnnotationUtils.getAnnotationAttributes(annotation).get("value"), field);
            }
        }
    }
}
