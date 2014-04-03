package com.proptiger.data.service.b2b;

import javax.ws.rs.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.B2bUserDetail;
import com.proptiger.data.repo.b2b.B2bUserDetailDao;
import com.proptiger.data.util.b2b.B2bUserPreferenceProcessor;

/**
 * B2b User Detail Service Class
 * 
 * @author Azitabh Ajit
 * 
 */

@Service
public class B2bUserDetailService {
    @Autowired
    private B2bUserDetailDao b2bUserDetailDao;

    public B2bUserDetail updateUserDetails(B2bUserDetail b2bUserDetail, UserInfo userInfo) {
        if (!B2bUserPreferenceProcessor.isValidPreference(b2bUserDetail.getPreference())) {
            throw new BadRequestException();
        }
        b2bUserDetail.setId(userInfo.getUserIdentifier());
        return b2bUserDetailDao.save(b2bUserDetail);
    }

    public B2bUserDetail getUserDetails(UserInfo userInfo) {
        B2bUserDetail b2bUserDetail = b2bUserDetailDao.findOne(userInfo.getUserIdentifier());
        b2bUserDetail.setPreference(B2bUserPreferenceProcessor.mergeDefaultPreference(b2bUserDetail.getPreference()));
        return b2bUserDetail;
    }
}