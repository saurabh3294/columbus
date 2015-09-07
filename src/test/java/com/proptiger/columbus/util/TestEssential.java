package com.proptiger.columbus.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.core.annotations.Essential;
import com.proptiger.core.annotations.Essential.TestType;
import com.proptiger.core.annotations.ResultEssential;

@Component
public class TestEssential {

    public List<ResultEssential> testEssentialFields(Object T) {

        List<Field> fieldList = getAllFields(T);
        List<Field> essentialFields = getEssentialFields(fieldList);
        List<ResultEssential> result = new ArrayList<ResultEssential>();

        for (Field field : essentialFields) {
            result.addAll(testEssentialField(field, T));
        }
        return result;
    }

    // Returns all the fields (including inherited) of the given object
    public List<Field> getAllFields(Object T) {
        List<Field> fieldList = new ArrayList<Field>();
        Class tempclass = T.getClass();
        while (tempclass != null) {
            fieldList.addAll(Arrays.asList(tempclass.getDeclaredFields()));
            tempclass = tempclass.getSuperclass();
        }
        return fieldList;
    }

    // Returns fields with @Essential set on them
    public List<Field> getEssentialFields(List<Field> fields) {
        List<Field> essentialFields = new ArrayList<Field>();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Essential) {
                    essentialFields.add(field);
                }
            }
        }
        return essentialFields;
    }

    private List<ResultEssential> testEssentialField(Field field, Object T) {
        boolean status = false;
        Annotation annotation = field.getDeclaredAnnotation(Essential.class);
        Essential essential = (Essential) annotation;
        List<ResultEssential> resultEssential = new ArrayList<ResultEssential>();
        TestType[] values = essential.value();

        for (TestType value : values) {
            switch (value) {
                case NOT_NULL:
                    status = checkNotNull(field, T);
                    resultEssential.add(new ResultEssential(status, field.getName(), TestType.NOT_NULL));
                    break;
                case NOT_EMPTY:
                    status = checkNotEmpty(field, T);
                    resultEssential.add(new ResultEssential(status, field.getName(), TestType.NOT_EMPTY));
                    break;
            }
        }
        return resultEssential;
    }

    private boolean checkNotNull(Field field, Object T) {
        try {
            if (field.get(T) != null) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkNotEmpty(Field field, Object T) {
        try {
            Object obj = field.get(T);
            if (obj instanceof String) {
                

                String value = (String) obj;
                if (value.trim() != "") {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (obj instanceof List)  {
                if(!((List) obj).isEmpty()){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
