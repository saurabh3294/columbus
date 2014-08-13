package com.proptiger.app.config.security;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.ActiveUser;
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

    private static Logger logger = LoggerFactory.getLogger(UserDetailManagerService.class);

    @Autowired
    private UserDao       userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        if (username != null && !username.isEmpty()) {
            User user = userDao.findByEmail(username);
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
        return userDetails;
    }

}
