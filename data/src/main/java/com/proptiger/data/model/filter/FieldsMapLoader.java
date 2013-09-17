/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mandeep
 *
 */
public class FieldsMapLoader {
    static ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> fieldsMap = new ConcurrentHashMap<Class<?>, ConcurrentMap<String, Field>>();
      
    public static String getDaoFieldName(Class<?> clazz, String name, Class<? extends Annotation> annotationClazzForColumnName) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        Annotation fieldAnnotation = fieldsMap.get(clazz).get(name).getAnnotation(annotationClazzForColumnName);
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
                fieldsMap.get(clazz).putIfAbsent((String) AnnotationUtils.getAnnotationAttributes(annotation).get("value"), field);
            }
            else {
                fieldsMap.get(clazz).putIfAbsent(field.getName(), field);
            }
        }
    }
}
