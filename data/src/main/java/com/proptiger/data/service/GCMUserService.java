package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.AndroidApplication;
import com.proptiger.data.model.GCMUser;
import com.proptiger.data.notification.exception.MultipleGCMRegistrationIdFoundException;
import com.proptiger.data.repo.GCMUserDao;

/**
 * @author Sahil Garg
 * 
 */
@Service
public class GCMUserService {

    @Autowired
    private GCMUserDao gcmUserDao;

    public List<GCMUser> findByAppIdentifierAndLoggedInUserId(AndroidApplication appIdentifier, Integer userId) {
        return gcmUserDao.findByAppIdentifierAndUserIdAndLoginStatus(appIdentifier, userId, Boolean.TRUE);
    }

    public List<GCMUser> findByAppIdentifier(AndroidApplication appIdentifier) {
        return gcmUserDao.findByAppIdentifier(appIdentifier);
    }

    public List<GCMUser> findByLoggedInUserId(Integer userId) {
        return gcmUserDao.findByUserIdAndLoginStatus(userId, Boolean.TRUE);
    }

    public List<GCMUser> findAll() {
        return gcmUserDao.findAll();
    }

    public GCMUser postGCMUser(GCMUser gcmUser, Integer userId) {
        String gcmRegId = gcmUser.getGcmRegId();
        AndroidApplication appIdentifier = gcmUser.getAppIdentifier();

        if (gcmRegId == null || appIdentifier == null) {
            return null;
        }

        List<GCMUser> gcmUsers = gcmUserDao.findByGcmRegId(gcmRegId);

        if (gcmUsers.size() > 1) {
            throw new MultipleGCMRegistrationIdFoundException(
                    "More than one GCM Users with RegistrationId: " + gcmRegId
                            + " and AppIdentifier: "
                            + appIdentifier
                            + " found in DB");
        }

        GCMUser persistedGCMUser;
        if (gcmUsers == null || gcmUsers.isEmpty()) {
            persistedGCMUser = gcmUser;
        }
        else {
            persistedGCMUser = gcmUsers.get(0);
            if (gcmUser.getEmail() != null) {
                persistedGCMUser.setEmail(gcmUser.getEmail());
            }
        }

        if (userId != null) {
            persistedGCMUser.setUserId(userId);
            persistedGCMUser.setLoginStatus(Boolean.TRUE);
        }
        else {
            persistedGCMUser.setLoginStatus(Boolean.FALSE);
        }

        return gcmUserDao.save(persistedGCMUser);
    }

    public void deleteGCMUser(String gcmRegId) {
        gcmUserDao.deleteByGcmRegId(gcmRegId);
    }
}
