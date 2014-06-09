package com.proptiger.data.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.user.DashboardDao;
import com.proptiger.data.repo.user.UserPreferenceDao;
import com.proptiger.data.util.UserPreferenceProcessor;

/**
 * B2b User Detail Service Class
 * 
 * @author Azitabh Ajit
 * 
 */

@Service
public class UserPreferenceService {
    @Autowired
    private UserPreferenceDao userPreferenceDao;

    @Autowired
    private DashboardDao      dashboardDao;

    @Autowired
    private ForumUserDao      userDao;

    public UserPreference updateUserDetails(UserPreference b2bUserDetail, UserInfo userInfo) {
        if (!UserPreferenceProcessor.isValidPreference(b2bUserDetail.getPreference())) {
            throw new com.proptiger.exception.BadRequestException(ResponseErrorMessages.INVALID_USER_PREFERENCE);
        }
        b2bUserDetail.setId(userInfo.getUserIdentifier());
        return userPreferenceDao.save(b2bUserDetail);
    }

    public List<UserPreference> getUserPreferences(int userId) {
        List<UserPreference> preferences = userPreferenceDao.findByuserId(userId);
        for (UserPreference preference : preferences) {
            preference.setPreference(UserPreferenceProcessor.mergeDefaultPreference(preference.getPreference()));
        }
        return preferences;
    }
}