package com.proptiger.app.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.Application;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * Custom implementation of UserDetailsService to provide criteria to
 * authenticate a user. This class uses database to authenticate.
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
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ActiveUser userDetails = null;
        User user = null;
        if (username != null && !username.isEmpty()) {
            user = userDao.findByEmail(username);
            if (user != null) {
                Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
                String password = user.getPassword() == null ? "" : user.getPassword();
                userDetails = new ActiveUser(
                        user.getId(),
                        user.getEmail(),
                        password,
                        true,
                        true,
                        true,
                        true,
                       SecurityContextUtils.getUserAuthority(applicationType), applicationType);
            }
            else {
                logger.error("User not found with email {}", username);
            }
        }
        // if no user found with given username(email)
        if (userDetails == null) {
            throw new UsernameNotFoundException("User name or password are incorrect");
        }
        return userDetails;
    }

}
