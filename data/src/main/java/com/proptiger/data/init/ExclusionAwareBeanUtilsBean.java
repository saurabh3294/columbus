package com.proptiger.data.init;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.proptiger.core.annotations.ExcludeFromBeanCopy;
import com.proptiger.data.model.filter.FieldsMapLoader;

public class ExclusionAwareBeanUtilsBean extends NullAwareBeanUtilsBean {
    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException,
            InvocationTargetException {
        Field field = FieldsMapLoader.getField(dest.getClass(), name);

        if (field != null && field.getAnnotation(ExcludeFromBeanCopy.class) == null) {
            super.copyProperty(dest, name, value);
        }
        else {
            return;
        }
    }
}
