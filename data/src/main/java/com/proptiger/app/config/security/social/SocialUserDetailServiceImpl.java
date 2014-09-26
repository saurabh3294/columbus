package com.proptiger.app.config.security.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import com.proptiger.data.enums.Application;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * Social user details service
 * 
 * @author Rajeev Pandey
 * 
 */
public class SocialUserDetailServiceImpl implements SocialUserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(SocialUserDetailServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        SocialUser socialUser = null;
        User user = userDao.findById(Integer.parseInt(userId));
        if (user != null) {
            String password = user.getPassword() == null ? "" : user.getPassword();
            Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
            socialUser = new ActiveUser(
                    user.getId(),
                    user.getEmail(),
                    password,
                    true,
                    true,
                    true,
                    true,
                    SecurityContextUtils.getDefaultAuthority(user.getId()),
                    applicationType);
        }
        else {
            logger.error("User not found with id {}", userId);
        }
        return socialUser;
    }

}
