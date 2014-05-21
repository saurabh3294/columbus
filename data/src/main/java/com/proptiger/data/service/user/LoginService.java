package com.proptiger.data.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Service class to provide methods for login and logout to shiro system
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class LoginService {

    @Autowired
    private ForumUserDao  forumUserDao;

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    /**
     * Login to shiro system
     * 
     * @param email
     * @param password
     * @param rememberMe
     * @return
     */
    public UserInfo login(String email, String password, boolean rememberMe) {
        // Subject currentUser = SecurityUtils.getSubject();
        // if(currentUser != null && currentUser.getPrincipal() != null &&
        // !currentUser.getPrincipal().toString().equals(email)){
        // //new login request with different user
        // logger.debug("Logout primary user {}", currentUser.getPrincipal());
        // logout();
        // }
        UserInfo userInfo = new UserInfo();
        // if ( !currentUser.isAuthenticated() ) {
        // UsernamePasswordToken token = new UsernamePasswordToken(email,
        // password);
        // token.setRememberMe(rememberMe);
        // try {
        // logger.debug("Login request for user {} and remember me {}", email,
        // rememberMe);
        // currentUser.login(token);
        // ForumUser forumUser = forumUserDao.findByEmail(email);
        // String sessionId = currentUser.getSession(false).getId()
        // .toString();
        // userInfo.setName(forumUser.getUsername());
        // userInfo.setSessionId(sessionId);
        // userInfo.setUserIdentifier(forumUser.getUserId());
        // userInfo.setContact(forumUser.getContact());
        // if (userInfo.getUserIdentifier()
        // .equals(Constants.ADMIN_USER_ID)) {
        // logger.debug(
        // "Login request for admin user id {} ",
        // userInfo.getUserIdentifier());
        // userInfo.setAdmin(true);
        // }
        // currentUser.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME,
        // userInfo);
        // token.clear();
        // logger.debug("User {} logged in", email);
        // } catch ( UnknownAccountException uae ) {
        // throw new
        // com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT,
        // uae);
        // } catch ( IncorrectCredentialsException ice ) {
        // throw new
        // com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT,
        // ice);
        // } catch ( LockedAccountException lae ) {
        // throw new
        // com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT,
        // lae);
        // } catch ( AuthenticationException ae ) {
        // throw new
        // com.proptiger.exception.AuthenticationException(ResponseErrorMessages.USER_NAME_PASSWORD_INCORRECT,
        // ae);
        // }
        // }
        // else{
        // userInfo =
        // (UserInfo)currentUser.getSession().getAttribute(Constants.LOGIN_INFO_OBJECT_NAME);
        // }
        logger.debug("Dummy login call for user {}", email);
        return userInfo;
    }

    /**
     * Logout the current user from shiro system
     */
    public boolean logout() {
        // Subject subject = SecurityUtils.getSubject();
        // logger.debug("Logout request for user {}",subject.getPrincipal());
        // if(subject.isAuthenticated()){
        // SecurityUtils.getSubject().logout();
        // }
        return true;
    }
}
