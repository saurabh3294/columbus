package com.proptiger.app.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.user.UserSubscriptionService;
import com.proptiger.exception.InvalidUserRoleException;

/**
 * Class to handle post auth checks. After successfull credential validation
 * there may be some checks say on type of user and their roles and access type.
 * 
 * @author Rajeev Pandey
 *
 */
public class PostAuthenticationCheck implements UserDetailsChecker {

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    private String                  errorMessageNonB2BUser               = "Invalid userid and password. Please send mail to datalabs@proptiger.com for verifying userid and password.";
    private String                  errorMessageExpiredPermissionB2BUser = "Thanks for using our product. Validity of your subscription has expired. To continue using this service, please connect with your relationship manager or send us mail at datalabs@proptiger.com";

    @Override
    public void check(UserDetails toCheck) {
        /*
         * If a b2b-user's permissions have expired then login request is denied
         */
        if (ApplicationNameService.isB2BApplicationRequest()) {
            if (toCheck instanceof ActiveUser) {
                int userId = ((ActiveUser) toCheck).getUserIdentifier();
                /*
                 * Throw error if user has no subscriptions at all (non-b2b
                 * user).
                 */
                List<?> userSubscriptionMappingList = userSubscriptionService.getUserSubscriptionMappingList(userId);
                if (userSubscriptionMappingList == null || userSubscriptionMappingList.isEmpty()) {
                    throw new InvalidUserRoleException(errorMessageNonB2BUser);
                }

                /* Throw error if user has no *active* subscriptions. */
                List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(userId);
                if (permissionList == null || permissionList.isEmpty()) {
                    throw new InvalidUserRoleException(errorMessageExpiredPermissionB2BUser);
                }
            }

        }

    }

}
