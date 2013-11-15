package com.proptiger.data.service.portfolio;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.util.Constants;

@Service
public class LoginService {

	@Autowired
	private ForumUserDao forumUserDao;
	
	private static Logger logger = LoggerFactory.getLogger(LoginService.class);
	
	public UserInfo login(String email, String password, boolean rememberMe){
		Subject currentUser = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo();
		if ( !currentUser.isAuthenticated() ) {
		    UsernamePasswordToken token = new UsernamePasswordToken(email, password);
		    token.setRememberMe(rememberMe);
			try {
				logger.error("Login request for user {}", token);
				currentUser.login(token);
				ForumUser forumUser = forumUserDao.findByEmailIdAndPassword(email, password);
				String sessionId = currentUser.getSession(false).getId()
						.toString();
				userInfo.setEmail(email);
				userInfo.setName(forumUser.getUsername());
				userInfo.setSessionId(sessionId);
				userInfo.setUserIdentifier(forumUser.getUserId());
				currentUser.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, userInfo);
				token.clear();
			} catch ( UnknownAccountException uae ) {
		        throw new com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT);
		    } catch ( IncorrectCredentialsException ice ) {
		    	throw new com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT);
		    } catch ( LockedAccountException lae ) {
		    	throw new com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT);
		    } catch ( AuthenticationException ae ) {
		    	throw new com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT);
		    }
		}
		else{
			userInfo = (UserInfo)currentUser.getSession().getAttribute(Constants.LOGIN_INFO_OBJECT_NAME);
		}
		
		return userInfo;
	}
	
	public void logout(){
		Subject subject = SecurityUtils.getSubject();
		logger.error("Logout request for user {}",subject.getPrincipal());
		if(subject.isAuthenticated()){
			SecurityUtils.getSubject().logout();
		}
	}
}
