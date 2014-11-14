package com.proptiger.data.interceptor;

import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.service.ApplicationNameService;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.service.user.UserSubscriptionService;

/**
 * This class appends the subscription permissions for logged in user to the
 * FIQL selector .
 */
@Aspect
@Component
public class RequestInterceptorTrend {

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Before("@annotation(com.proptiger.core.annotations.Intercepted.Trend)")
    public void addSubscriptionPermissionsToSelectorTrend(JoinPoint joinPoint) {
        if (!ApplicationNameService.isB2BApplicationRequest()) {
            return;
        }
        ActiveUser user = SecurityContextUtils.getActiveUser();
        if(user != null){
            addSubscriptionBasedFiltersToFIQLSelector(joinPoint, user);
        }
    }

    @Before("@annotation(com.proptiger.core.annotations.Intercepted.TrendReport)")
    public void addSubscriptionPermissionsToSelectorTrendreport(JoinPoint joinPoint) {
        ActiveUser user = SecurityContextUtils.getActiveUser();
        if (user == null) {
            throw new UnauthorizedException(ResponseCodes.ACCESS_DENIED, ResponseErrorMessages.ACCESS_DENIED);
        }
        addSubscriptionBasedFiltersToFIQLSelector(joinPoint, user);
    }

    private void addSubscriptionBasedFiltersToFIQLSelector(JoinPoint jointPoint, ActiveUser user) {
        /* If all of users's permissions have expired then log him out */
        List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(user.getUserIdentifier());
        if (permissionList == null || permissionList.isEmpty()) {
            throw new UnauthorizedException(
                    ResponseCodes.ACCESS_EXPIRED,
                    ResponseErrorMessages.EXPIRED_PERMISSION_B2B_USER);
        }

        Object[] methodArgs = jointPoint.getArgs();
        for (Object arg : methodArgs) {
            if (arg != null && arg.getClass().equals(FIQLSelector.class)) {

                String filters = userSubscriptionService.getUserAppSubscriptionFilters(user.getUserIdentifier());
                if (filters != null) {
                    ((FIQLSelector) arg).addAndConditionToFilter(filters);
                }
                /* TODO :: implement using fiqlSelector.setRows(0); */
                else {
                    ((FIQLSelector) arg).addAndConditionToFilter("cityId==500000");
                }
            }
        }
    }

}