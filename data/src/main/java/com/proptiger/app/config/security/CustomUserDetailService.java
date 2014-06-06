package com.proptiger.app.config.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Custom implementation of UserDetailsService to provide criteria to authenicate a user.
 * This class uses database to authenticate.
 * @author Rajeev Pandey
 *
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private ForumUserDao forumUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser forumUser = forumUserDao.findByEmail(username);
        UserDetails userDetails = null;
        if (forumUser == null) {
            throw new UsernameNotFoundException("User name or password are incorrect");
        }
        userDetails = new ActiveUser(forumUser.getUserId(),
                forumUser.getEmail(),
                forumUser.getPassword(),
                true,
                true,
                true,
                true,
                new ArrayList<GrantedAuthority>());
        return userDetails;
    }

}
