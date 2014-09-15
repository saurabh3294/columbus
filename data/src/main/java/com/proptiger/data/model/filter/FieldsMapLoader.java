/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author mandeep
 * 
 */
public class FieldsMapLoader {
    static private ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> fieldsMap        = new ConcurrentHashMap<>();
    static private ConcurrentHashMap<Field, FieldMetaInfo>               fieldMetaInfoMap = new ConcurrentHashMap<>();

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

    public static FieldMetaInfo getFieldMeta(Class<?> clazz, String fieldName) {
        if (fieldName.contains(".")) {
            fieldName = fieldName.split("\\.")[0];
        }
        if (!fieldsMap.containsKey(clazz)) {
            loadClassFields(clazz);
        }
        Field field = fieldsMap.get(clazz).get(fieldName);
        FieldMetaInfo fieldMetaInfo = new FieldMetaInfo();
        if (field != null) {
            if (fieldMetaInfoMap.get(field) != null) {
                fieldMetaInfo = fieldMetaInfoMap.get(field);
            }
            else {
                if (field.getAnnotation(Transient.class) != null) {
                    fieldMetaInfo.setTransientField(true);
                }
                if (field.getAnnotation(Column.class) != null) {
                    fieldMetaInfo.setDirectFieldInModel(true);
                }
                fieldMetaInfo.setFieldInModel(true);
                fieldMetaInfoMap.putIfAbsent(field, fieldMetaInfo);
            }

        }
        return fieldMetaInfo;
    }

    /**
     * Contains fields meta info like 1. if field is direct field 2. if field
     * is transient 3. if field present in model class
     * 
     * @author Rajeev Pandey
     *
     */
    public static class FieldMetaInfo {
        private boolean isTransientField;
        /*
         * this signifies if a field is direct column in corresponding table
         * associated with this model
         */
        private boolean isDirectFieldInModel;
        private boolean isFieldInModel;

        public boolean isTransientField() {
            return isTransientField;
        }

        public void setTransientField(boolean isTransientField) {
            this.isTransientField = isTransientField;
        }

        public boolean isDirectFieldInModel() {
            return isDirectFieldInModel;
        }

        public void setDirectFieldInModel(boolean isDirectFieldInModel) {
            this.isDirectFieldInModel = isDirectFieldInModel;
        }

        public boolean isFieldInModel() {
            return isFieldInModel;
        }

        public void setFieldInModel(boolean isFieldInModel) {
            this.isFieldInModel = isFieldInModel;
        }

    }
}
