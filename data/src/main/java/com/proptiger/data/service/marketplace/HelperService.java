package com.proptiger.data.service.marketplace;

/**
 * @author azi
 */

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.user.UserHierarchy;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.data.external.dto.CustomUser;

@Service
public class HelperService {
    @Autowired
    HttpRequestUtil requestUtil;

    /**
     * 
     * @return List of roles for logged in user
     */
    public List<String> getLoggedInUserRoles() {
        return requestUtil.getInternalApiResultAsType(
                URI.create(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                        .getRequiredPropertyAsString(PropertyKeys.USER_DETAILS_API)),
                RequestHolderUtil.getHeaderContainingJsessionId(),
                CustomUser.class).getRoles();
    }

    /**
     * 
     * @return list of UserHierarchy for logged in users
     */
    public List<UserHierarchy> getChildren() {
        return requestUtil.getInternalApiResultAsType(
                URI.create(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                        .getRequiredPropertyAsString(PropertyKeys.USER_CHILDREN_API)),
                RequestHolderUtil.getHeaderContainingJsessionId(),
                UserHierarchy.class).getChild();
    }
}