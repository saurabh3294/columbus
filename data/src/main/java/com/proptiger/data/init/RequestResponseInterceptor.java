package com.proptiger.data.init;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * @author Rajeev Pandey
 *
 */
public class RequestResponseInterceptor  extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		System.out.println("************* PRE HANDLE *******************");
		System.out.println("REQUEST "+ request.getRequestURL());
		System.out.println("RESPONSE "+ response.getContentType());
		
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object handler, ModelAndView modelAndView){
		System.out.println("************* POST HANDLE *******************");
		System.out.println("REQUEST "+ httpRequest.toString());
		System.out.println("RESPONSE "+ httpResponse.getContentType());
		
				
	}
	/*
	private HttpServletResponse getResponse(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse){
		HttpServletResponse httpResponse = getSavedResponse(httpRequest);
		
		if(httpResponse == null)
			deleteResponseFromCache(httpRequest);
		else
			httpServletResponse = httpResponse;
		
		return httpServletResponse;
	}
	
	@Cacheable(key="#{request.getRequestURL()", value="cache")
	private HttpServletResponse getSavedResponse(HttpServletRequest httpServletRequest){
		return null;
	}
	
	@CachePut(key="#{request.getRequestURL()}", value="cache")
	private HttpServletResponse saveResponse(HttpServletRequest request, HttpServletResponse response){
		return response;
	}
	
	@CacheEvict(value="cache", key="#{request.getRequestURL()}")
	private void deleteResponseFromCache(HttpServletRequest httpRequest){
		
	}*/
	
	
}
