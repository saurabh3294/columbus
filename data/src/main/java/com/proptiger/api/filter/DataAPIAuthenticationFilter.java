package com.proptiger.api.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.util.CacheClientUtil;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.AuthenticationException;

/**
 * This filter is authentication user, it user have already logged in then it
 * will allow to pass the request otherwise return the 403 response
 * 
 * @author Rajeev Pandey
 *
 */
public class DataAPIAuthenticationFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(DataAPIAuthenticationFilter.class);
	/*
	 * To enable and disable the authentication, modify in shiro.ini
	 */
	private boolean enabled = true;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = ((HttpServletRequest)request);
		String userIpAddress = httpRequest.getRemoteAddr();
		UserInfo userInfo = null;
		
		Cookie[] cookies = httpRequest.getCookies();
		String sessionId = null;
		if(cookies != null){
			for(Cookie c: cookies){
				if(c.getName().equals(Constants.PHPSESSID_KEY)){
					sessionId = c.getValue();
					break;
				}
			}
		}
		logger.debug("PHPSESSIONID from request cookie {}",sessionId);
		String jsessionIdPassed = null;
		Integer userIdOnBehalfOfAdmin = null;
		String[] jsessionIdsVal = httpRequest
				.getParameterValues(Constants.JSESSIONID);
		if (jsessionIdsVal != null && jsessionIdsVal.length > 0) {
			jsessionIdPassed = jsessionIdsVal[0];
			
		}

		String[] values = httpRequest
				.getParameterValues(Constants.REQ_PARAMETER_FOR_USER_ID);
		if (values != null && values.length > 0) {
			try {
				userIdOnBehalfOfAdmin = Integer.parseInt(values[0]);
			} catch (NumberFormatException e) {
				logger.error("Invalid user Id in url, {}",e.getMessage());
				writeErrorToResponse(response,
						ResponseCodes.BAD_REQUEST,
						ResponseErrorMessages.INVALID_FORMAT_IN_REQUEST, userIpAddress);
				return;
			}
		}
		if(enabled){
			/*
			 * If authentication enabled then only logged in user will be served, and user details
			 * will be picked from memcache
			 */
			if (jsessionIdPassed == null && sessionId == null) {
				logger.error("Authentication error session id null");
				writeErrorToResponse(response, ResponseCodes.UNAUTHORIZED,
						ResponseErrorMessages.AUTHENTICATION_ERROR, userIpAddress);
				return;
			}
			else if (sessionId != null && jsessionIdPassed != null
					&& !sessionId.equals(jsessionIdPassed)) {
				logger.error("Admin Authentication error session id null");
				writeErrorToResponse(response, ResponseCodes.UNAUTHORIZED,
						ResponseErrorMessages.AUTHENTICATION_ERROR, userIpAddress);
				return;
			}
			else if(jsessionIdPassed != null){
				/*
				 * If this is the case then user may be admin
				 */
				logger.debug("Taking session id from url");
				sessionId = jsessionIdPassed;
			}
			try {
				userInfo = getUserInfoFromMemcache(sessionId);
				if (userInfo.getUserIdentifier()
						.equals(Constants.ADMIN_USER_ID)) {
					userInfo.setAdmin(true);
					if (userIdOnBehalfOfAdmin != null) {
						// If user id is present in request parameter then admin
						// might try to
						// do something on behalf of other user
						logger.debug("Admin user {} doing on behalf of user {}",userInfo.getUserIdentifier(), userIdOnBehalfOfAdmin);
						userInfo.setUserIdentifier(userIdOnBehalfOfAdmin);
					}
				}
			} catch (Exception e1) {
				logger.error("User not found, {}",e1.getMessage());
				writeErrorToResponse(response, ResponseCodes.UNAUTHORIZED,
						ResponseErrorMessages.AUTHENTICATION_ERROR, userIpAddress);
				return;
			}
		}
		else {
			/*
			 * Find user id in request URL, and serve based on that user id
			 */
			StringBuffer path = httpRequest.getRequestURL();
			Pattern userIdPattern = Pattern.compile("user/(.*?\\d)/");
			Matcher matcher = userIdPattern.matcher(path);
			Integer userId = null;
			while (matcher.find()) {
				try {
					userId = Integer.parseInt(matcher.group(1));
				} catch (NumberFormatException e) {
					logger.error("Invalid user Id in request url {}",e.getMessage());
					writeErrorToResponse(response, ResponseCodes.UNAUTHORIZED,
							ResponseErrorMessages.AUTHENTICATION_ERROR, userIpAddress);
					return;
				}
			}
			if(userId == null){
				/*
				 * Auth is disabled and the api does not have user id in url, then
				 * get user id in request parameter, this will not hanle admin case
				 */
				userId = userIdOnBehalfOfAdmin;
			}
			if(userId == null){
				logger.error("Invalid user Id in url, {}",userId);
				writeErrorToResponse(response,
						ResponseCodes.BAD_REQUEST,
						ResponseErrorMessages.INVALID_FORMAT_IN_REQUEST, userIpAddress);
				return;
			}
			logger.debug("Skipping authentication, serve request for user id {}",userId);
			userInfo = new UserInfo();
			userInfo.setUserIdentifier(userId);
			if (userInfo.getUserIdentifier()
					.equals(Constants.ADMIN_USER_ID)) {
				userInfo.setAdmin(true);
				if (userIdOnBehalfOfAdmin != null) {
					// If user id is present in request parameter then admin
					// might try to
					// do something on behalf of other user
					logger.debug("Admin user {} doing on behalf of user {}",userInfo.getUserIdentifier(), userIdOnBehalfOfAdmin);
					userInfo.setUserIdentifier(userIdOnBehalfOfAdmin);
				}
			}
		}
		/*
		 * Set in request session to be accessible in controllers
		 */
		httpRequest.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME,
				userInfo);
		chain.doFilter(request, response);
	}

	private void writeErrorToResponse(ServletResponse response, String code,
			String msg, String userIpAddress) throws IOException,
			JsonProcessingException {
		logger.warn("Unauthenticated call from host {}", userIpAddress);
		PrintWriter out = response.getWriter();
		ProAPIErrorResponse res = new ProAPIErrorResponse(code, msg);
		ObjectMapper mapper = new ObjectMapper();
		out.println(mapper.writeValueAsString(res));
		return;
	}

	/**
	 * Get user id from memcache based on key
	 * @param sessionId
	 * @return
	 */
	private UserInfo getUserInfoFromMemcache(String sessionId) {
		if(sessionId == null){
			throw new AuthenticationException("Session id null");
		}
		UserInfo userInfo = new UserInfo();
		Integer userId = null;
		String userName = null;
		String email = null;
		if(sessionId != null){
			String value = (String) CacheClientUtil.getValue(sessionId);
			if(value != null){
				Pattern userIdPattern = Pattern.compile("USER_ID.+?\"(\\d+?)\".*USERNAME.+?\"([a-z|A-Z].+?)\".*EMAIL.+?\"(.+?)\"");
				Matcher matcher = userIdPattern.matcher(value);
				while(matcher.find()){
					try {
						userId = Integer.parseInt(matcher.group(1));
					} catch (NumberFormatException e) {
						logger.error("Number format exception {}",e.getMessage());
						throw new AuthenticationException("Number format error in memcache for sessionkey "+sessionId);
					}
					userName = matcher.group(2);
					email = matcher.group(3);
					break;
				}
			}
		}
		if(userId == null){
			throw new AuthenticationException("session data not found in memcache for sessionkey "+sessionId);
		}
		else{
			userInfo.setName(userName);
			userInfo.setUserIdentifier(userId);
		}
		return userInfo;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	@Override
	public void destroy() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
