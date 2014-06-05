package com.proptiger.data.init.comparator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;

import com.proptiger.data.trend.external.dto.BuilderTrend;
import com.proptiger.exception.ProAPIException;

/**
 * Deneric comparator for sorting pojo list on a field
 * 
 * @author azi
 * 
 * @param <T>
 */
public class GenericComparator<T> implements Comparator<T> {
    private Field   field;
    private boolean isAscending = true;

    public GenericComparator(String fieldName) {
        try {
            field = BuilderTrend.class.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException | SecurityException e) {
            throw new ProAPIException(e);
        }
    }

    public GenericComparator(String fieldName, boolean isAscending) {
        try {
            this.isAscending = isAscending;
            field = BuilderTrend.class.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException | SecurityException e) {
            throw new ProAPIException(e);
        }
    }

    @Override
    public int compare(T o1, T o2) {
        int result = 0;
        try {
            Object value1 = PropertyUtils.getProperty(o1, field.getName());
            Object value2 = PropertyUtils.getProperty(o2, field.getName());
            if (field.getType().equals(String.class)) {
                result = value1.toString().compareTo(value2.toString());
            }
            else if (field.getType().equals(Integer.class) || field.getType().equals(Integer.TYPE)) {
                result = Integer.valueOf(value1.toString()).compareTo(Integer.valueOf(value2.toString()));
            }
            else if (field.getType().equals(Double.class) || field.getType().equals(Double.TYPE)) {
                // XXX This part is not tested yet
                result = Double.valueOf(value1.toString()).compareTo(Double.valueOf(value2.toString()));
            }

            if (isAscending) {
                return result;
            }
            else {
                return -1 * result;
            }
        }
        catch (IllegalArgumentException | IllegalAccessException | ClassCastException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new ProAPIException(e);
        }
    }
}