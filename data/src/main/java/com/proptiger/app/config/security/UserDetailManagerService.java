package com.proptiger.app.config.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.user.UserSubscriptionService;
import com.proptiger.exception.InvalidUserRoleException;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.user.UserDao;

/**
 * Custom implementation of UserDetailsService to provide criteria to
 * authenicate a user. This class uses database to authenticate.
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
@Service
public class UserDetailManagerService implements UserDetailsService {

    private static Logger           logger   = LoggerFactory.getLogger(UserDetailManagerService.class);

    @Autowired
    private UserDao                 userDao;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    private String                  errorMessageNonB2BUser               = "Invalid userid and password. Please send mail to datalabs@proptiger.com for verifying userid and password.";
    private String                  errorMessageExpiredPermissionB2BUser = "Thanks for using our product. Validity of your subscription has expired. To continue using this service, please connect with your relationship manager or send us mail at datalabs@proptiger.com";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        User user = null;
        if (username != null && !username.isEmpty()) {
            user = userDao.findByEmail(username);
            if (user != null) {
                String password = user.getPassword() == null ? "" : user.getPassword();
                userDetails = new ActiveUser(
                        user.getId(),
                        user.getEmail(),
                        password,
                        true,
                        true,
                        true,
                        true,
                        new ArrayList<GrantedAuthority>());
            }
            else {
                logger.error("User not found with email {}", username);
            }
        }
        // if no user found with given username(email)
        if (userDetails == null) {
            throw new UsernameNotFoundException("User name or password are incorrect");
        }

        /* If a b2b-user's permissions have expired then login request is denied */
        if (user != null && ApplicationNameService.isB2BApplicationRequest()) {
            int userId = user.getId();

            /* Throw error if user has no subscriptions at all (non-b2b user). */
            List<?> userSubscriptionMappingList = userSubscriptionService.getUserSubscriptionMappingList(userId);
            if (userSubscriptionMappingList == null || userSubscriptionMappingList.isEmpty()) {
                throw new InvalidUserRoleException(errorMessageNonB2BUser);
            }

            /* Throw error if user has no *active* subscriptions. */
            List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(user.getId());
            if (permissionList == null || permissionList.isEmpty()) {
                throw new InvalidUserRoleException(errorMessageExpiredPermissionB2BUser);
            }
        }

        return userDetails;
    }

}
