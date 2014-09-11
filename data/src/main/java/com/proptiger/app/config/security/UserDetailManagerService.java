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
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.user.UserSubscriptionService;
import com.proptiger.exception.InvalidUserRoleException;

/**
 * Custom implementation of UserDetailsService to provide criteria to
 * authenicate a user. This class uses database to authenticate.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class UserDetailManagerService implements UserDetailsService {

    private static Logger         logger = LoggerFactory.getLogger(UserDetailManagerService.class);
    
    @Autowired
    private ForumUserDao          forumUserDao;
    
    @Autowired
    private UserSubscriptionService userSubscriptionService;
    
    private String errorMessageNonB2BUser = "You are not authorized to access this portal. In case this is happening by mistake, please connect with us at datalabs@proptiger.com";
    private String errorMessageExpiredPermissionB2BUser = "Your access has expired. To continue using this service, please connect with us at datalabs@proptiger.com";
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        ForumUser forumUser = null;
        if (username != null && !username.isEmpty()) {
            /*
             * since there can be multiple rows for same email, say one from direct registration
             * and other from some service provider login like facebook.
             * 
             * TODO this call need to be changed once we make user merge live
             */
            forumUser = forumUserDao.findByEmailAndProvider(username, "");
            if (forumUser != null) {
                userDetails = new ActiveUser(
                        forumUser.getUserId(),
                        forumUser.getEmail(),
                        forumUser.getPassword(),
                        true,
                        true,
                        true,
                        true,
                        new ArrayList<GrantedAuthority>());
            }
            else{
                logger.error("User not found with email {}",username);
            }
        }
        // if no user found with given username(email)
        if (userDetails == null) {
            throw new UsernameNotFoundException("User name or password are incorrect");
        }
        
        /* If a b2b-user's permissions have expired then login request is denied */
        if(forumUser != null && ApplicationNameService.isB2BApplicationRequest())
        {
            int userId = forumUser.getUserId();
            
            /* Throw error if user has no subscriptions at all (non-b2b user). */
            List<?> userSubscriptionMappingList = userSubscriptionService.getUserSubscriptionMappingList(userId);
            if(userSubscriptionMappingList == null || userSubscriptionMappingList.isEmpty()){
                throw new InvalidUserRoleException(errorMessageNonB2BUser);
            }
            
            /* Throw error if user has no *active* subscriptions. */
            List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(forumUser.getUserId());
            if(permissionList == null || permissionList.isEmpty()){
                throw new InvalidUserRoleException(errorMessageExpiredPermissionB2BUser);
            }
        }
        
        return userDetails;
    }

}
