package com.proptiger.data.util;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Aspect
@Component
public class ResponseCaching {
	@Autowired
	private Caching caching;
	
	/*
	 * This method will be called to check and get the cache response.
	 */
	@Around("execution(* com.proptiger.data.mvc.*.*(..))")
	public Object getResponse(ProceedingJoinPoint jp) throws Throwable {
		Class<?> proxyMethodReturnType = getProxyMethodReturnType(jp);
	
		Object response = getResponse( getCacheKey(jp), proxyMethodReturnType);
		if(response == null)
			return jp.proceed();
		
		return response;
	}
	
	/*
	 * This method will be called when method returns the response successfully. Then that
	 * data will be saved in the cache.
	 */
	@AfterReturning(pointcut="execution(* com.proptiger.data.mvc.*.*(..))", returning="retVal")
	public void setResponse(JoinPoint jp, Object retVal) throws Throwable {
		caching.saveResponse(getCacheKey(jp), retVal);
	}

	/*
	 * This method will be called when some error is thrown during any of advices execution.
	 */
	@AfterThrowing(pointcut="execution(* com.proptiger.data.mvc.*.*(..))", throwing="ex")
	public <T> void setResponse(JoinPoint jp, Exception ex) {
		System.out.println(ToStringBuilder.reflectionToString(ex));
	}

	/*
	 * This method will get data from cache. If returned data is null then
	 * that cache key will be invalidated. This has to be done as for getting
	 * the data from cache , Cacheable annotation is used. This annotation
	 * will be save and get data. Hence, null checking and cache eviction is
	 * necessary.
	 */
	private <T> T getResponse(String key, Class<T> returnType){
		T savedResponse = caching.getSavedResponse(key, returnType);
				
		if(savedResponse == null)
			caching.deleteResponseFromCache(key);
		
		return savedResponse;
	}
	
	/*
	 * Constructs the cache key.
	 * 
	 */
	private String getCacheKey(JoinPoint jp){
		String encodeKey = "";
		String key=jp.getSignature().toString() + ":ARG:";
		
		Object[] args = jp.getArgs();
		
		for(int i=0; i < args.length; i++)
		{
			if(args[i]!=null)
				key += "i"+args[i].toString();
		}
	
		try{
			encodeKey = new HMAC_Client().calculateMD5(key);
		}catch(Exception e){
			return key;
		}
		//System.out.println("KEY: "+key);
		//System.out.println("######## KEY ####### :"+encodeKey);
		return encodeKey;
	}
	
	/*
	 * Get the class of the response object passed in the advice.
	 */
	private Class<?> getProxyMethodReturnType(JoinPoint jp){
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getReturnType();
		
	}
	
/*	private void print(JoinPoint jp){
		//System.out.println(" REQUEST "+ gson.toJson(request));
		Object[] args = jp.getArgs();
		for(int i=0; i<args.length; i++)
		{
			if(args[i] != null)
				System.out.println("ARG "+i+" : "+args[i].toString());
		}
		System.out.println("Target: "+jp.getTarget().toString());
		System.out.println("KIND: "+jp.getKind().toString());
		System.out.println("SIGNATURE: "+jp.getSignature().toString());
		System.out.println("PJP  "+jp.toString());
		System.out.println("THIS :"+ jp.getThis().toString());
		System.out.println(" Return TYPE CLASS = " + getProxyMethodReturnType(jp));
		
	}*/

}
