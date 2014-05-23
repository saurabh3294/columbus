package com.proptiger.data.init;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.DefaultKeyGenerator;

/**
 * Extending DefaultKeyGenerator to include method names and class names
 * 
 * @author azi
 * 
 */
public class CustomDefaultKeyGenerator extends DefaultKeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getName() + method.getName() + super.generate(target, method, params);
    }

}
