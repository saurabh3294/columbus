package com.proptiger.app.config.security.social;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import com.proptiger.data.enums.Application;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;
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
    private ForumUserDao  forumUserDao;

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        SocialUser socialUser = null;
        ForumUser forumUser = forumUserDao.findByUserId(Integer.parseInt(userId));
        if (forumUser != null) {
            String password = forumUser.getPassword() == null ? "" : forumUser.getPassword();
            Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
            socialUser = new ActiveUser(
                    forumUser.getUserId(),
                    forumUser.getEmail(),
                    password,
                    true,
                    true,
                    true,
                    true,
                    SecurityContextUtils.getUserAuthority(applicationType),
                    applicationType);
        }
        else {
            logger.error("User not found with id {}", userId);
        }
        return socialUser;
    }

}
