/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author mandeep
 *
 */
public class FieldsMapLoader {
    static Map<Class<?>, Map<String, String>> fieldsMap = new ConcurrentHashMap<Class<?>, Map<String,String>>();

    public static String getFieldName(Class<?> clazz, String name) {
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }

        return fieldsMap.get(clazz).get(name);
    }

    private static void loadClassFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(JsonProperty.class);
            Annotation fieldAnnotation = field.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
            if (annotation != null) {
                if (!fieldsMap.containsKey(clazz)) {
                    fieldsMap.put(clazz, new ConcurrentHashMap<String, String>());
                }

                fieldsMap.get(clazz).put((String) AnnotationUtils.getAnnotationAttributes(annotation).get("value"), (String) AnnotationUtils.getAnnotationAttributes(fieldAnnotation).get("value"));
            }
        }
    }
}
