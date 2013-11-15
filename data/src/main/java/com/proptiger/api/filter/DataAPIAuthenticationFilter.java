package com.proptiger.api.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
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
		Subject currentUser = SecurityUtils.getSubject();
		UserInfo user = (UserInfo) currentUser.getSession().getAttribute(Constants.LOGIN_INFO_OBJECT_NAME);
		if (!currentUser.isAuthenticated()) {
			PrintWriter out = response.getWriter();
			ProAPIErrorResponse res = new ProAPIErrorResponse(
					ResponseCodes.AUTHENTICATION_ERROR,
					ResponseErrorMessages.AUTHENTICATION_ERROR);
			ObjectMapper mapper = new ObjectMapper();
			out.println(mapper.writeValueAsString(res));
		}
		else{
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
