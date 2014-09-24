package com.proptiger.data.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static <T> void copyFieldsInSameObject(
            T object,
            String fieldNameSrc,
            String fieldNameDest) throws Exception {
        
        if(fieldNameSrc.equals(fieldNameDest)){
            return;
        }
        
        Class<?> clazz = object.getClass();
        Field srcField = null, destField = null;
        try {
            srcField = clazz.getDeclaredField(fieldNameSrc);
            destField = clazz.getDeclaredField(fieldNameDest);
            destField.setAccessible(true);
            srcField.setAccessible(true);
            destField.set(object, srcField.get(object));
        }
        catch (NoSuchFieldException | SecurityException ex) {
            throw ex;
        }
    }
}
