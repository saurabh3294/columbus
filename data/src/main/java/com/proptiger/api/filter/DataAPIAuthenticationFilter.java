package com.proptiger.api.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.util.Constants;

/**
 * This filter is authentication user, it user have already logged in then it
 * will allow to pass the request otherwise return the 403 response
 * 
 * @author Rajeev Pandey
 *
 */
public class DataAPIAuthenticationFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(DataAPIAuthenticationFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponce = (HttpServletResponse) response;
		httpResponce.addHeader("Access-Control-Allow-Origin", "*");
		Subject currentUser = SecurityUtils.getSubject();
		if (!currentUser.isAuthenticated()) {
			try{
				DelegatingSubject delegatingSubject = (DelegatingSubject) currentUser;
				if(delegatingSubject != null && delegatingSubject.getHost() != null){
					logger.error("Unauthenticated API call from host {}",delegatingSubject.getHost());
				}
			}catch(Exception e){
				logger.error("Could not cast to DelegatingSubject- "+e.getMessage());
			}
			PrintWriter out = response.getWriter();
			ProAPIErrorResponse res = new ProAPIErrorResponse(
					ResponseCodes.AUTHENTICATION_ERROR,
					ResponseErrorMessages.AUTHENTICATION_ERROR);
			ObjectMapper mapper = new ObjectMapper();
			out.println(mapper.writeValueAsString(res));
		}
		else{
			UserInfo userInfo = (UserInfo) currentUser.getSession().getAttribute(Constants.LOGIN_INFO_OBJECT_NAME);
			if(userInfo.isAdmin()){
				logger.debug("Request from admin user {}", userInfo.getUserIdentifier());
				/*
				 * This request is considered as special request from admin to modify some other 
				 * user's data on behalf on Admin
				 * 
				 * So need to set other user's userid in session managed UserInfo object, so that
				 * all other controllers will get other user's id from session
				 */
				HttpServletRequest httpRequest = ((HttpServletRequest)request);
	            Enumeration<String> parameterNames = httpRequest.getParameterNames();
	            boolean found = false;
	            Integer userIdOnBehalfOfAdmin = null;
				while (parameterNames.hasMoreElements()) {
					String[] values = httpRequest
							.getParameterValues(Constants.REQ_PARAMETER_FOR_USER_ID);
					if(values != null && values.length > 0){
						try {
							userIdOnBehalfOfAdmin = Integer.parseInt(values[0]);
							found = true;
							break;
						} catch (NumberFormatException e) {
							logger.error("Working on behalf of other user failed");
							logger.error("NumberFormatException", e);
						}
					}

				}
	            if(found){
	            	/*
	            	 * Admin is trying to do something on behalf of other user, so modify user info
	            	 * object in session that will be assessed by all controllers, and will do work
	            	 * for this user
	            	 */
	            	logger.debug("Changing admin user info with user id {}", userIdOnBehalfOfAdmin);
	            	userInfo.setUserIdentifier(userIdOnBehalfOfAdmin);
	            }
	            else{
	            	//that means admin is not trying to do thing on befalf of some other user
	            	//set user identifer back to admin
	            	userInfo.setUserIdentifier(Constants.ADMIN_USER_ID);
	            }
			}
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	@Override
	public void destroy() {
	}
}
