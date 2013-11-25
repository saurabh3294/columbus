package com.proptiger.data.util;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseCaching {
	@Autowired
	private Caching caching;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Around("execution(* com.proptiger.data.mvc.*.*(..))")
	public Object getResponse(ProceedingJoinPoint jp) throws Throwable {
		Class<?> proxyMethodReturnType = getProxyMethodReturnType(jp);
	
		Object response = getResponse( getCacheKey(jp), proxyMethodReturnType);
		if(response == null)
			return jp.proceed();
		
		return response;
	}

	/*@Around("execution(* com.proptiger.data.mvc.*.*(..))")
	public Object getResponse(ProceedingJoinPoint jp) throws Throwable {
		Object response = caching.getSavedResponse(getCacheKey(jp), getProxyMethodReturnType(jp));

		if (response == null)
			return jp.proceed();

		return response;
	}	*/

	@AfterReturning(pointcut="execution(* com.proptiger.data.mvc.*.*(..))", returning="retVal")
	public void setResponse(JoinPoint jp, Object retVal) throws Throwable {
		//System.out.println("*****************************After*****************************");
		//System.out.println(ToStringBuilder.reflectionToString(retVal));
		Class<?> className = getProxyMethodReturnType(jp);
		
		//System.out.println(retVal.getClass().getName());
		//System.out.println("***********************************END AFTER **********************************");
		caching.saveResponse(getCacheKey(jp), retVal);//getProxyMethodReturnType(jp).cast(retVal));
	}

	@AfterThrowing(pointcut="execution(* com.proptiger.data.mvc.*.*(..))", throwing="ex")
	public <T> void setResponse(JoinPoint jp, Exception ex) {
		//System.out.println("*****************************AfterThrowing*****************************");
		System.out.println(ToStringBuilder.reflectionToString(ex));
		//System.out.println("***********************************END AFTER Throwing**********************************");
	}

	
	private <T> T getResponse(String key, Class<T> returnType){
		T savedResponse = caching.getSavedResponse(key, returnType);
				
		if(savedResponse == null)
			caching.deleteResponseFromCache(key);
		else
			System.out.println("Class Name : "+savedResponse.getClass().getName());
		
		return savedResponse;
	}
	
	private String getCacheKey(JoinPoint jp){
		String encodeKey = "";
		String key=jp.getSignature().toString() + ":ARG:";
		
		Object[] args = jp.getArgs();
		//System.out.println(" LENGTH "+args.length);
		//System.out.println(" data "+args.toString());
		for(int i=0; i<args.length-1; i++)
		{
			//System.out.println("i"+i+" arguments ");
			//System.out.println(args[i].toString());
			key += "i"+args[i].toString();
		}
		
		try{
			encodeKey = new HMAC_Client().calculateMD5(key);
		}catch(Exception e){
			return key;
		}
		//System.out.println("######## KEY ####### :"+encodeKey);
		return encodeKey;
	}
	
	private Class<?> getProxyMethodReturnType(JoinPoint jp){
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		return method.getReturnType();
		
	}
	
//	private void print(JoinPoint jp){
//		//System.out.println(" REQUEST "+ gson.toJson(request));
//		Object[] args = jp.getArgs();
//		for(int i=0; i<args.length; i++)
//			System.out.println("ARG "+i+" : "+args[i].toString());
//		System.out.println("Target: "+jp.getTarget().toString());
//		System.out.println("KIND: "+jp.getKind().toString());
//		System.out.println("SIGNATURE: "+jp.getSignature().toString());
//		System.out.println("PJP  "+jp.toString());
//		System.out.println("THIS :"+ jp.getThis().toString());
//		System.out.println(" Return TYPE CLASS = " + getProxyMethodReturnType(jp));
//		
//	}

}
