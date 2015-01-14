package com.proptiger.data.service.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String URL_GET_ACTIVE_USER_DETAILS    = "/app/v1/user/details";
    private static final String URL_GET_ACTIVE_USER            = "/data/v1/entity/user";
    private static final String URL_GET_USERS_BY_USER_IDS              = "/data/v1/entity/user?userId=";
    private static final String URL_GET_USER_LIST_WITH_DETAILS = "data/v1/entity/user-details?userId=";
    private static final String URL_GET_USER_DETAILS_BY_EMAIL = "data/v1/entity/user-details?email=";

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

    /**
     * This will get only user object, so contact and other details will not be
     * present
     * 
     * @param userIds
     * @return
     */
    public List<User> getUsersByUserIds_CallerLoginRequired(Collection<Integer> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            return new ArrayList<User>();
        }
        HttpHeaders header = createJsessionIdHeader();
        List<User> users = getUsersByIds(userIds, URL_GET_USERS_BY_USER_IDS, header);
        return users;
    }

    public Map<Integer, User> getUserWithCompleteDetailsByUserIds_CallerNonLogin(Collection<Integer> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            return new HashMap<Integer, User>();
        }
        List<User> users = getUsersByIds(userIds, URL_GET_USER_LIST_WITH_DETAILS, null);
        return userListToMap(users);
    }
    
    public User getUserWithCompleteDetailsByUserIds_CallerNonLogin(Integer userId) {
        List<User> users = getUsersByIds(Arrays.asList(userId), URL_GET_USER_LIST_WITH_DETAILS, null);
        if(users == null || users.isEmpty()){
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET); 
        }
        return users.get(0);
    }

    private List<User> getUsersByIds(Collection<Integer> userIds, String userGetUrl, HttpHeaders header) {
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL)).append(userGetUrl);
        boolean first = true;
        for (Integer id : userIds) {
            if (!first) {
                stringUrl.append(",");
                first = false;
            }
            stringUrl.append(id);
        }
        List<User> users = new ArrayList<User>();
        if (header != null) {
            users = httpRequestUtil
                    .getInternalApiResultAsTypeList(URI.create(stringUrl.toString()), header, User.class);
        }
        else{
            users = httpRequestUtil
                    .getInternalApiResultAsTypeList(URI.create(stringUrl.toString()), User.class);
        }
        return users;
    }

    public User getUserById_CallerNonLogin(Integer userId) {
        List<User> list = getUsersByIds(Arrays.asList(userId), URL_GET_USERS_BY_USER_IDS, null);
        if (list == null || list.isEmpty()) {
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }
        return list.get(0);
    }

    public Map<Integer, User> getUsersMapByUserIds_CallerNonLogin(Collection<Integer> userIds) {
        List<User> users = getUsersByIds(userIds, URL_GET_USERS_BY_USER_IDS, null);
        return userListToMap(users);
    }

    private Map<Integer, User> userListToMap(List<User> users) {
        Map<Integer, User> map = new HashMap<Integer, User>();
        for (User u : users) {
            map.put(u.getId(), u);
        }
        return map;
    }
    
    public User getUserByEmail_CallerNonLogin(String email){
        if(email == null || email.isEmpty()){
            throw new BadRequestException("Invalid email id");
        }
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL)).append(URL_GET_USER_DETAILS_BY_EMAIL).append(email);
        
        List<User> users = httpRequestUtil
        .getInternalApiResultAsTypeList(URI.create(stringUrl.toString()), User.class);
        if(users == null || users.isEmpty()){
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.GET);
        }
        return users.get(0);
    }
}
