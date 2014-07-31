package com.proptiger.data.repo.user;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.user.User;

/**
 * 
 * @author azi
 * 
 */

public class UserDaoImpl {
    @Autowired
    private UserDao userDao;

    public User findUserDetailsByPrimaryEmail(String primaryEmail) {
        User user = userDao.findByPrimaryEmail(primaryEmail);
        user.getEmails();
        return user;
    }
}