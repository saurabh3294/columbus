package com.proptiger.data.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class UtilityClass {
    /**
     * Returns non zero max of given 2 numbers - null otherwise
     * 
     * @param a
     * @param b
     * @return
     */
    public static Double max(Double a, Double b) {
        Double c = a;
        if (a == null) {
            c = b;
        }
        else if (b != null) {
            c = Math.max(a, b);
        }

        return c;
    }

    /**
     * Returns non zero min of given 2 numbers - null otherwise
     * 
     * @param a
     * @param b
     * @return
     */
    public static Double min(Double a, Double b) {
        Double c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else if (b != null && b != 0) {
            c = Math.min(a, b);
        }

        return c;
    }

    /**
     * @return {@link Integer} non zero min of given 2 integers - null otherwise
     * 
     * @param a
     *            {@link Integer}
     * @param b
     *            {@link Integer}
     * 
     * @author Azitabh Ajit
     */
    public static Integer min(Integer a, Integer b) {
        Integer c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else if (b != null && b != 0) {
            c = Math.min(a, b);
        }
        return c;
    }

    /**
     * @return {@link Integer} non zero min of list of integers - null otherwise
     * 
     * @param integers
     *            {@link List<Integer>}
     * 
     * @author Azitabh Ajit
     */
    public static Integer min(List<Integer> integers) {
        Integer result = null;

        for (Integer integer : integers) {
            result = min(result, integer);
        }
        return result;
    }

    /**
     * @return {@link Integer} non zero max of given 2 integers - null otherwise
     * 
     * @param a
     *            {@link Integer}
     * @param b
     *            {@link Integer}
     * 
     * @author Azitabh Ajit
     */
    public static Integer max(Integer a, Integer b) {
        Integer c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else if (b != null && b != 0) {
            c = Math.max(a, b);
        }
        return c;
    }

    /**
     * @return {@link Integer} non zero max of list of integers - null otherwise
     * 
     * @param integers
     *            {@link List<Integer>}
     * 
     * @author Azitabh Ajit
     */
    public static Integer max(List<Integer> integers) {
        Integer result = null;

        for (Integer integer : integers) {
            result = max(result, integer);
        }
        return result;
    }

    public static Integer[] getIntArrFromStringArr(String[] strArr) {
        int length = strArr.length;
        Integer[] result = new Integer[length];
        for (int i = 0; i < length; i++) {
            result[i] = Integer.parseInt(strArr[i]);
        }
        return result;
    }

    public static <T> Object groupFieldsAsPerKeys(List<T> items, List<String> groupKeys) {
        List<String> keys = new ArrayList<>();
        for (String groupKey : groupKeys) {
            keys.add(groupKey);
        }

        if (keys.size() == 0) {
            return items;
        }

        String groupBy = keys.get(0);
        keys.remove(0);
        Map<Object, Object> result = new HashMap<>();

        try {
            for (T item : items) {
                Object groupValue = PropertyUtils.getSimpleProperty(item, groupBy);
                if (groupValue instanceof Date)
                    groupValue = ((Date) groupValue).getTime();
                if (result.get(groupValue) == null) {
                    List<T> newList = new ArrayList<>();
                    result.put(groupValue, newList);
                }
                ((List<T>) result.get(groupValue)).add(item);

            }
            for (Object key : result.keySet()) {
                result.put(key, groupFieldsAsPerKeys((List<T>) result.get(key), keys));
            }
        }
        catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * @param list
     * @param count
     * @return first count elements of the list OR whole list if it has less N
     *         elements.
     */
    public static <T> List<T> getFirstNElementsOfList(List<T> list, int count) {
        if (list == null) {
            return null;
        }
        else {
            int finalIndex = Math.min(list.size(), count);
            return (new ArrayList<T>(list.subList(0, finalIndex)));
        }
    }

    /**
     * 
     * @param actualKey
     * @return {@link Object} that can pe put in grouped response as key
     */
    public static Object getResponseGroupKey(Object actualKey) {
        Object result = null;
        if (actualKey instanceof Date) {
            Date date = (Date) actualKey;
            result = date.getTime();
        }
        else {
            result = actualKey;
        }
        return result;
    }
}