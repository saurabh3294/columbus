package com.proptiger.data.init;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import com.google.gson.Gson;

/**
 * Implementing Custom Default KeyGenerator
 * 
 * @author azi
 * 
 */
public class CustomDefaultKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        int hashCode = 17;
        hashCode = 31 * hashCode + target.getClass().getName().hashCode();
        hashCode = 31 * hashCode + method.getName().toString().hashCode();

        for (Object object : params) {
            int jsonHashCode = new Gson().toJson(object).hashCode();
            hashCode = 31 * hashCode + jsonHashCode;
        }
        return Integer.valueOf(hashCode);
    }
}
