package com.proptiger.data.init;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Implementing Custom Default KeyGenerator
 * 
 * @author azi
 * 
 */
public class CustomDefaultKeyGenerator implements KeyGenerator {
    private static final int NULL_PARAM_KEY = 53;

    @Override
    public Object generate(Object target, Method method, Object... params) {
        int hashCode = 17;
        hashCode = 31 * hashCode + target.getClass().getName().hashCode();
        hashCode = 31 * hashCode + method.getName().toString().hashCode();

        for (Object object : params) {
            hashCode = 31 * hashCode
                    + (object == null ? NULL_PARAM_KEY : ToStringBuilder.reflectionToString(
                            object,
                            ToStringStyle.SHORT_PREFIX_STYLE).hashCode());
        }
        return Integer.valueOf(hashCode);
    }
}
