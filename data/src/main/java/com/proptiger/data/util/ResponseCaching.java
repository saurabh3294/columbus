package com.proptiger.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.exception.ProAPIException;

@Aspect
@Component
public class ResponseCaching {
    @Autowired
    private Caching       caching;

    private static Logger logger = LoggerFactory.getLogger(ResponseCaching.class);

    /*
     * This method will be called to check and get the cache response.
     */
    @Around("execution(* com.proptiger.*.mvc.*.*(..))")
    public Object getResponse(ProceedingJoinPoint jp) throws Throwable {
        Class<?> proxyMethodReturnType = getProxyMethodReturnType(jp);

        Object response = getResponse(getCacheKey(jp), proxyMethodReturnType);
        if (response == null)
            return jp.proceed();

        return response;
    }

    /*
     * This method will be called when method returns the response successfully.
     * Then that data will be saved in the cache.
     */
    @AfterReturning(
            pointcut = "execution(* com.proptiger.*.mvc.*.*(..)) && !execution(* com.proptiger.*.mvc.portfolio.*(..))",
            returning = "retVal")
    public void setResponse(JoinPoint jp, Object retVal) throws Throwable {
        // if response is not valid, then response will not be saved.
        Class<?> className = retVal.getClass();
        if (className == ProAPIException.class) {
            return;
        }
        else if (className == APIResponse.class) {
            APIResponse response = (APIResponse) retVal;
            // this check is required if someone returned error normally from a
            // controller method
            if (response.getError() != null) {
                return;
            }
        }

        String key = getCacheKey(jp);
        if (!isCacheEnabled(jp) || getResponse(key, getProxyMethodReturnType(jp)) != null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        logger.info(" CACHING URL: " + request.getRequestURI() + " key: " + key + " response: " + retVal);
        caching.saveResponse(getCacheKey(jp), retVal);
    }

    /*
     * This method will get data from cache. If returned data is null then that
     * cache key will be invalidated. This has to be done as for getting the
     * data from cache , @Cacheable annotation is used. This annotation will be
     * save and get data. Hence, null checking and cache eviction is necessary.
     */
    private <T> T getResponse(String key, Class<T> returnType) {
        T savedResponse = caching.getSavedResponse(key, returnType);

        if (savedResponse == null) {
            caching.deleteResponseFromCache(key);
        }
        return savedResponse;
    }

    /*
     * Constructs the cache key.
     */
    private String getCacheKey(JoinPoint jp) {
        String encodeKey = "";
        String key = jp.getSignature().toString() + ":ARG:";

        Object[] args = jp.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null)
                key += ":i:" + args[i].toString();
        }

        try {
            encodeKey = new HMAC_Client().calculateMD5(key);
        }
        catch (Exception e) {
            return key;
        }

        return encodeKey;
    }

    /*
     * Get the class of the response object passed in the advice.
     */
    private Class<?> getProxyMethodReturnType(JoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        return method.getReturnType();

    }

    /*
     * This method will check the Disable Caching Annotation in the target Class
     * or target Method. If it is present then caching will not be done.
     */
    private boolean isCacheEnabled(JoinPoint jp) {
        // checking Disable Caching Annotation on a Controller Class.
        Object target = jp.getTarget();
        Class<? extends Object> targetClass = target.getClass();
        Annotation classAnnotation = targetClass.getAnnotation(DisableCaching.class);
        if (classAnnotation != null)
            return false;

        // checking DisableCaching Annotation in Method
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = null;
        try {
            method = targetClass.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            Annotation methodAnnotation = method.getAnnotation(DisableCaching.class);
            if (methodAnnotation != null)
                return false;
        }
        catch (Exception e) {
        }

        if (!isValidUrlForCache())
            return false;
        return true;
    }

    private boolean isValidUrlForCache() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (request != null) {
            String url = request.getRequestURI();
            if (url.matches("/user/")) {
                return false;
            }
        }
        return true;
    }

}
