package com.proptiger.data.service.user;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.dto.internal.user.CustomUser;
import com.proptiger.core.exception.AuthenticationExceptionImpl;
import com.proptiger.core.model.user.User;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class UserServiceHelper {

    private static final String URL_GET_ACTIVE_USER_DETAILS = "/app/v1/user/details";
    private static final String URL_GET_ACTIVE_USER         = "/data/v1/entity/user";

    @Autowired
    private HttpRequestUtil     httpRequestUtil;

    private HttpHeaders createJsessionIdHeader() {
        HttpHeaders requestHeaders = null;
        String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
        if (jsessionId != null && !jsessionId.isEmpty()) {
            requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
        }
        else {
            throw new AuthenticationExceptionImpl(
                    ResponseCodes.AUTHENTICATION_ERROR,
                    ResponseErrorMessages.User.AUTHENTICATION_ERROR);
        }
        return requestHeaders;
    }

    public CustomUser getActiveUserCustomDetails() {
        HttpHeaders header = createJsessionIdHeader();
        String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(URL_GET_ACTIVE_USER_DETAILS).toString();
        CustomUser customUser = httpRequestUtil.getInternalApiResultAsType(
                URI.create(stringUrl),
                header,
                CustomUser.class);
        return customUser;
    }

    public User getActiveUser() {
        HttpHeaders header = createJsessionIdHeader();
        String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(URL_GET_ACTIVE_USER).toString();
        User user = httpRequestUtil.getInternalApiResultAsType(URI.create(stringUrl), header, User.class);
        return user;
    }

}
