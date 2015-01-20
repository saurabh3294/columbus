package com.proptiger.data.service.user;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.dto.internal.user.CustomUser;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.AuthenticationExceptionImpl;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ResourceNotAvailableException;
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
    
    @Value("${userservice.module.name}")
    private String userServiceModuleName;
    
    private static final String URL_GET_ACTIVE_USER_DETAILS    = "/app/v1/user/details";
    private static final String URL_DATA_V1_ENTITY_USER        = "/data/v1/entity/user";
    private static final String URL_APP_V1_USER_BY_USER_IDS      = "/app/v1/user?userId=";
    private static final String URL_APP_V1_USER_DETAILS_BY_USER_IDS = "/app/v1/user-details?userId=";
    private static final String URL_APP_V1_USER_DETAILS_BY_EMAIL  = "/app/v1/user-details?email=";

    @Autowired
    private HttpRequestUtil     httpRequestUtil;
    
    private static Logger                logger = LoggerFactory.getLogger(UserServiceHelper.class);

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
                .append(getRelativeUrl(URL_GET_ACTIVE_USER_DETAILS)).toString();
        CustomUser customUser = httpRequestUtil.getInternalApiResultAsType(
                URI.create(stringUrl),
                header,
                CustomUser.class);
        return customUser;
    }

    public User getLoggedInUserObj() {
        HttpHeaders header = createJsessionIdHeader();
        String stringUrl = new StringBuilder(PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(getRelativeUrl(URL_DATA_V1_ENTITY_USER)).toString();
        User user = httpRequestUtil.getInternalApiResultAsType(URI.create(stringUrl), header, User.class);
        return user;
    }


    public Map<Integer, User> getUserWithCompleteDetailsByUserIds_CallerNonLogin(Collection<Integer> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            return new HashMap<Integer, User>();
        }
        List<User> users = getUsersByIds(userIds, getRelativeUrl(URL_APP_V1_USER_DETAILS_BY_USER_IDS), null);
        return userListToMap(users);
    }

    public User getUserWithCompleteDetailsById_CallerNonLogin(Integer userId) {
        List<User> users = getUsersByIds(Arrays.asList(userId), getRelativeUrl(URL_APP_V1_USER_DETAILS_BY_USER_IDS), null);
        if (users == null || users.isEmpty()) {
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }
        return users.get(0);
    }

    private List<User> getUsersByIds(Collection<Integer> userIds, String completeURI, HttpHeaders header) {
        StringBuilder completeURL = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL)).append(completeURI);
        boolean first = true;
        for (Integer id : userIds) {
            if (!first) {
                completeURL.append(",");
            }
            first = false;
            completeURL.append(id);
        }
        List<User> users = new ArrayList<User>();
        String encoded;
        try {
            encoded = URLEncoder.encode(completeURL.toString(), Constants.DEFAULT_ENCODING);
            if (header != null) {
                users = httpRequestUtil
                        .getInternalApiResultAsTypeList(URI.create(encoded), header, User.class);
            }
            else {
                users = httpRequestUtil.getInternalApiResultAsTypeList(URI.create(completeURL.toString()), User.class);
            }
        }
        catch (UnsupportedEncodingException e) {
           logger.error("error while fetching user details for {}",userIds.toString(),e);
        }
        
        return users;
    }

    public User getUserById_CallerNonLogin(Integer userId) {
        List<User> list = getUsersByIds(Arrays.asList(userId), getRelativeUrl(URL_APP_V1_USER_BY_USER_IDS), null);
        if (list == null || list.isEmpty()) {
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }
        return list.get(0);
    }

    public Map<Integer, User> getUsersMapByUserIds_CallerNonLogin(Collection<Integer> userIds) {
        List<User> users = getUsersByIds(userIds, getRelativeUrl(URL_APP_V1_USER_BY_USER_IDS), null);
        return userListToMap(users);
    }

    private Map<Integer, User> userListToMap(List<User> users) {
        Map<Integer, User> map = new HashMap<Integer, User>();
        for (User u : users) {
            map.put(u.getId(), u);
        }
        return map;
    }

    public User getUserByEmail_CallerNonLogin(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Invalid email id");
        }
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL)).append(
                        getRelativeUrl(URL_APP_V1_USER_DETAILS_BY_EMAIL)).append(email);

        User user = httpRequestUtil.getInternalApiResultAsTypeFromCache(URI.create(stringUrl.toString()), User.class);
        if (user == null) {
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }
        return user;
    }

    public User getOrCreateUser_CallerNonLogin(User userToFind) {
        User user = null;
        try {
            user = getUserById_CallerNonLogin(userToFind.getId());
        }
        catch (ResourceNotAvailableException e) {
            // user does not exist so call register
            user = createOrPatchUser_CallerNonLogin(userToFind);
        }

        return user;
    }

    public User createOrPatchUser_CallerNonLogin(User userToFind) {
        User user;
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(getRelativeUrl(URL_DATA_V1_ENTITY_USER));
        user = httpRequestUtil.postAndReturnInternalJsonRequest(
                URI.create(stringUrl.toString()),
                userToFind,
                User.class);
        return user;
    }
    
    private String getRelativeUrl(String url){
        return new StringBuilder(userServiceModuleName).append(url).toString();
    }
}
