package com.proptiger.data.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Aspect
@Component
public class ResponseCaching {
	@Autowired
	Caching caching;
	
	@Around("execution(* com.proptiger.data.mvc.*.*(..))")
	public Object getResponse(ProceedingJoinPoint jp) throws Throwable {
		System.out.println(" AROUND ADVICE ");
		//print(jp);
		
		Object response = getResponse(getCacheKey(jp));
		if(response == null)
			return jp.proceed();
		
		//System.out.println(" END ADVICE");
		return response;
	}
	
	@AfterReturning(pointcut="execution(* com.proptiger.data.mvc.*.*(..))", returning="retVal")
	public Object setResponse(JoinPoint jp, Object retVal) throws Throwable{
		//System.out.println("AFTER ADVICE");
		//print(jp);
		
		caching.saveResponse(getCacheKey(jp), retVal);
		
		//System.out.println(retVal);
		//System.out.println(" END ADVICE");
		return new Object();
	}
	
	private void print(JoinPoint jp){
		//System.out.println(" REQUEST "+ gson.toJson(request));
		Object[] args = jp.getArgs();
		for(int i=0; i<args.length; i++)
			System.out.println("ARG "+i+" : "+args[i].toString());
		System.out.println("Target: "+jp.getTarget().toString());
		System.out.println("KIND: "+jp.getKind().toString());
		System.out.println("SIGNATURE: "+jp.getSignature().toString());
		System.out.println("PJP  "+jp.toString());
		System.out.println("THIS :"+ jp.getThis().toString());
		
	}
	
	private String getCacheKey(JoinPoint jp){
		String encodeKey = "";
		String key=jp.getSignature().toString() + ":ARG:";
		
		Object[] args = jp.getArgs();
		for(int i=0; i<args.length; i++)
			key += "i"+args[i].toString();
		
		try{
			encodeKey = new HMAC_Client().calculateMD5(key);
		}catch(Exception e){
			return key;
		}
		System.out.println("######## KEY ####### :"+encodeKey);
		return encodeKey;
	}
	
	private Object getResponse(String key){
		Object savedResponse = caching.getSavedResponse(key);
		
		try{
			if(savedResponse == null)
				caching.deleteResponseFromCache(key);
		}catch(Exception e){}
		
		return savedResponse;
	}
	
	

}
