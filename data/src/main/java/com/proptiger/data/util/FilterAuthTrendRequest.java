package com.proptiger.data.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.user.UserSubscriptionService;

/**
 * This class appends the subscription permissions for logged in user to the
 * FIQL selector .
 */
@Aspect
@Component
public class FilterAuthTrendRequest {

    @Autowired
    ApplicationNameService applicationNameService;

    @Autowired
    private UserSubscriptionService    userSubscriptionService;

    @Pointcut(
            value = "execution(* com.proptiger.data.mvc.trend.TrendController.get*Trend(..)) || execution(* com.proptiger.data.mvc.trend.BuilderTrendController.get*(..))")
    public void addSubscriptionPermissionsToSelectorPointCut() {
    }

    @Before(value = "addSubscriptionPermissionsToSelectorPointCut()")
    public void beforeAddSubscriptionPermissionsToSelectorPointCut(JoinPoint jointPoint) throws Throwable {
        if (!ApplicationNameService.isB2BApplicationRequest()) {
            return;
        }
        ActiveUser user = SecurityContextUtils.getLoggedInUser();
        if (user != null) {
            Object[] methodArgs = jointPoint.getArgs();
            for (Object arg : methodArgs) {
                if (arg != null && arg.getClass().equals(FIQLSelector.class)) {

                    String filters = userSubscriptionService.getUserAppSubscriptionFilters(user.getUserIdentifier()).getFilters();
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

}