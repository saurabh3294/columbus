package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.AndroidApplication;
import com.proptiger.data.model.GCMUsers;
import com.proptiger.data.repo.GCMUsersDao;

/**
 * @author Sahil Garg
 * 
 */
@Service
public class GCMUserService {

    @Autowired
    private GCMUsersDao gcmUsersDao;

    public List<GCMUsers> findByAppIdentifierAndLoggedInUserId(AndroidApplication appIdentifier, Integer userId) {
        return gcmUsersDao.findByAppIdentifierAndUserIdAndLoginStatus(appIdentifier, userId, Boolean.TRUE);
    }

    public List<GCMUsers> findByAppIdentifier(AndroidApplication appIdentifier) {
        return gcmUsersDao.findByAppIdentifier(appIdentifier);
    }
    
    public List<GCMUsers> findByLoggedInUserId(Integer userId) {
        return gcmUsersDao.findByUserIdAndLoginStatus(userId, Boolean.TRUE);
    }
    
    public List<GCMUsers> findAll() {
        return gcmUsersDao.findAll();
    }
}
