package com.proptiger.app.config.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.Constants;

/**
 * Auth success handler to manage session and response after authentication. It
 * put the logged in user details to request session so that would be available
 * to controllers
 * 
 * @author Rajeev Pandey
 *
 */
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public AuthSuccessHandler() {
        super();
    }
    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws ServletException, IOException {

        ActiveUser userInfo = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof ActiveUser) {
            userInfo = (ActiveUser) principal;
            /*
             * putting in request session so it would be acessible to
             * controllers
             */
            request.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, userInfo);
        }
        PrintWriter out = response.getWriter();
        ForumUser forumUserDetails = userService.getUserDetails(userInfo.getUserIdentifier());
        out.println(objectMapper.writeValueAsString(new APIResponse(forumUserDetails)));
        clearAuthenticationAttributes(request);

    }
}
