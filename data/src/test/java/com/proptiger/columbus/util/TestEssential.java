package com.proptiger.columbus.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.core.annotations.Essential;
import com.proptiger.core.annotations.Essential.TestType;
import com.proptiger.core.util.ReflectionUtils;

@Component
public class TestEssential<T> {

    public List<ResultEssential> testEssentialFields(T object) {
        List<Field> essentialFieldsList = ReflectionUtils.getFieldsWithAnnotation(object, Essential.class);
        List<ResultEssential> listResult = new ArrayList<ResultEssential>();

        for (Field field : essentialFieldsList) {
            listResult.addAll(testEssentialField(field, object));
        }
        return listResult;
    }

    private List<ResultEssential> testEssentialField(Field field, T object) {
        boolean status = false;
        Annotation annotation = field.getDeclaredAnnotation(Essential.class);
        Essential essential = (Essential) annotation;
        List<ResultEssential> listResultEssential = new ArrayList<ResultEssential>();
        TestType[] values = essential.value();

        for (TestType value : values) {
            switch (value) {
                case NOT_NULL:
                    status = ReflectionUtils.checkNotNull(field, object);
                    listResultEssential.add(new ResultEssential(status, field.getName(), TestType.NOT_NULL));
                    break;
                case NOT_EMPTY:
                    status = ReflectionUtils.checkNotEmpty(field, object);
                    listResultEssential.add(new ResultEssential(status, field.getName(), TestType.NOT_EMPTY));
                    break;
            }
        }
        return listResultEssential;
    }
}
