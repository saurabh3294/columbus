package com.proptiger.data.init;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.enums.security.UserRole;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.service.APIAccessDetailPersistentService;
import com.proptiger.data.service.security.CrawlPreventionService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.SecurityContextUtils;
import com.proptiger.exception.BadRequestException;

/**
 * 
 * @author Rajeev Pandey
 * @author Azitabh Ajit
 * 
 */
public class RequestResponseInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private APIAccessDetailPersistentService userAccessDetailPersistentService;

    @Autowired
    private CrawlPreventionService           crawlPreventionService;

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        Authentication auth = SecurityContextUtils.getAuthentication();
        if (auth.getPrincipal() instanceof ActiveUser) {
            ActiveUser activeUser = (ActiveUser) auth.getPrincipal();
            for (GrantedAuthority authority : activeUser.getAuthorities()) {
                if (authority.getAuthority().equals(UserRole.ADMIN_BACKEND.name())) {
                    activeUser = new ActiveUser(
                            Constants.ADMIN_USER_ID,
                            activeUser.getUsername(),
                            activeUser.getPassword() == null ? "" : activeUser.getPassword(),
                            activeUser.isEnabled(),
                            activeUser.isAccountNonExpired(),
                            activeUser.isCredentialsNonExpired(),
                            activeUser.isAccountNonLocked(),
                            activeUser.getAuthorities(),
                            activeUser.getApplicationType());
                    SecurityContextUtils.autoLogin(activeUser);
                }
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (crawlPreventionService.isValidRequest(request, response)) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            userAccessDetailPersistentService.processRequest(request, response);
            modifyUserIdByBackendAdmin(request, response);
            return super.preHandle(request, response, handler);
        }
        return false;
    }

    private void modifyUserIdByBackendAdmin(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextUtils.getAuthentication();
        if (auth.getPrincipal() instanceof ActiveUser) {
            ActiveUser activeUser = (ActiveUser) auth.getPrincipal();
            for (GrantedAuthority authority : activeUser.getAuthorities()) {
                if (authority.getAuthority().equals(UserRole.ADMIN_BACKEND.name()) && activeUser.getUserIdentifier()
                        .equals(Constants.ADMIN_USER_ID)) {
                    Integer userId = getUserIdFromRequest(request);
                    /*
                     * Admin request from backed script to do something behalf
                     * of userId
                     */
                    if (userId != null) {
                        activeUser.setUserIdentifier(userId);
                        SecurityContextUtils.putActiveUserInSession(request, new UsernamePasswordAuthenticationToken(
                                activeUser,
                                null,
                                activeUser.getAuthorities()));
                    }
                    break;
                }
            }
        }
    }

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        Integer userId = null;
        String[] values = request.getParameterValues(Constants.REQ_PARAMETER_FOR_USER_ID);
        if (values != null && values.length > 0) {
            try {
                userId = Integer.parseInt(values[0]);
            }
            catch (NumberFormatException e) {
                throw new BadRequestException(ResponseCodes.BAD_REQUEST, "Invalid userId in request");
            }
        }
        return userId;
    }

}