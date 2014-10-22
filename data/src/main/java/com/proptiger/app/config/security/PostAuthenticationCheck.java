package com.proptiger.app.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.Application;
import com.proptiger.data.service.user.UserSubscriptionService;
import com.proptiger.exception.AuthenticationExceptionImpl;

/**
 * Class to handle post auth checks. After successful credential validation
 * there may be some checks say on type of user and their roles and access type.
 * 
 * @author Rajeev Pandey
 *
 */
public class PostAuthenticationCheck implements UserDetailsChecker {

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Override
    public void check(UserDetails toCheck) {
        if (toCheck instanceof ActiveUser) {
            ActiveUser activeUser = ((ActiveUser) toCheck);
            validateSubscription(activeUser);
        }
    }

    /**
     * Validate subscription and account expiration of user
     * 
     * @param activeUser
     */
    private void validateSubscription(ActiveUser activeUser) {
        /*
         * If a b2b-user's permissions have expired then login request is denied
         */
        if (activeUser.getApplicationType().equals(Application.B2B)) {
            int userId = activeUser.getUserIdentifier();
            /*
             * Throw error if user has no subscriptions at all (non-b2b user).
             */
            List<?> userSubscriptionMappingList = userSubscriptionService.getUserSubscriptionMappingList(userId);
            if (userSubscriptionMappingList == null || userSubscriptionMappingList.isEmpty()) {
                throw new AuthenticationExceptionImpl(ResponseCodes.ACCESS_DENIED, ResponseErrorMessages.NON_B2B_USER);
            }

            /* Throw error if user has no *active* subscriptions. */
            List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(userId);
            if (permissionList == null || permissionList.isEmpty()) {
                throw new AuthenticationExceptionImpl(ResponseCodes.ACCESS_EXPIRED, ResponseErrorMessages.EXPIRED_PERMISSION_B2B_USER);
            }
        }

    }

}
