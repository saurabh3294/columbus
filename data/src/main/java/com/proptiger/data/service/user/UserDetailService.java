package com.proptiger.data.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.UserDetail;
import com.proptiger.data.repo.user.UserDetailDao;
import com.proptiger.data.util.UserPreferenceProcessor;

/**
 * B2b User Detail Service Class
 * 
 * @author Azitabh Ajit
 * 
 */

@Service
public class UserDetailService {
    @Autowired
    private UserDetailDao b2bUserDetailDao;

    public UserDetail updateUserDetails(UserDetail b2bUserDetail, UserInfo userInfo) {
        if (!UserPreferenceProcessor.isValidPreference(b2bUserDetail.getPreference())) {
            throw new com.proptiger.exception.BadRequestException(ResponseErrorMessages.INVALID_USER_PREFERENCE);
        }
        b2bUserDetail.setId(userInfo.getUserIdentifier());
        return b2bUserDetailDao.save(b2bUserDetail);
    }

    public UserDetail getUserDetails(UserInfo userInfo) {
        UserDetail b2bUserDetail = b2bUserDetailDao.findOne(userInfo.getUserIdentifier());
        b2bUserDetail.setPreference(UserPreferenceProcessor.mergeDefaultPreference(b2bUserDetail.getPreference()));
        return b2bUserDetail;
    }
}