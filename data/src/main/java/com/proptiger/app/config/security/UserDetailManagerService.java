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

    @Autowired
    ApplicationNameService applicationNameService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        ForumUser forumUser = null;
        if (username != null && !username.isEmpty()) {
            /*
             * since there can be multiple rows for same email, say one from direct registration
             * and other from srom some service provider login like facebook.
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
        if(forumUser != null && applicationNameService.isB2BApplicationRequest())
        {
            List<?> permissionList = userSubscriptionService.getUserAppSubscriptionDetails(forumUser.getUserId());
            if(permissionList == null || permissionList.isEmpty()){
                throw new UsernameNotFoundException("User does not have permissions for this feature.");
            }
        }
        
        return userDetails;
    }

}
