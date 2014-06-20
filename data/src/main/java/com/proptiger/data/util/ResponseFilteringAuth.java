package com.proptiger.data.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.Project;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.UserService;

/**
 * This class is used for authentication based filtering of APIResponse.
 */
@Aspect
@Component
public class ResponseFilteringAuth {

    @Autowired
    private UserService userService;

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "execution(java.lang.Object com.proptiger.app.mvc.ProjectListingController.getProjectListings(..))",
            returning = "retVal")
    public void filterResponseProjectListings(Object retVal) throws Throwable {

        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null || userSubscriptionMap == null) {
            return;
        }

        /* Filtering */
        HashMap<String, Object> resultMap = (HashMap<String, Object>) data;
        List<Object> projectItemsList = (List<Object>) resultMap.get("items");

        int objTypeIdLocality = DomainObject.locality.getObjectTypeId();
        int objTypeIdCity = DomainObject.city.getObjectTypeId();

        /*
         * [1]. For locality-id keys we only need to check if a key exists. [2].
         * For city-id a key will exist (with a value null) even if the user has
         * a permission for some locality in the city. To check for full city
         * permission we nee to check if the value mapped to that key is not
         * null.
         */

        for (Object value : projectItemsList) {
            int localityId = (Integer) (((Map<String, Object>) value).get("localityId"));
            if (userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) {
                ((Map<String, Object>) value).put("authorized", true);
            }
            else if (userSubscriptionMap.get(objTypeIdCity, extractCityIdFromProjectListingResponse(value)) != null) {
                ((Map<String, Object>) value).put("authorized", true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int extractCityIdFromProjectListingResponse(Object response) {
        try {
            Object locality = ((Map<String, Object>) response).get("locality");
            Object suburb = ((Map<String, Object>) locality).get("suburb");
            Integer cityId = (Integer) ((Map<String, Object>) suburb).get("cityId");
            return cityId.intValue();
        }
        catch (Exception ex) {
            return -1;
        }

    }

    @AfterReturning(
            pointcut = "execution(com.proptiger.data.pojo.response.APIResponse com.proptiger.app.mvc.AppLocalityController.getLocalityListingData(..))",
            returning = "retVal")
    public void filterResponseLocalityListings(Object retVal) throws Throwable {
        findAndReplaceInResponseData(
                retVal,
                DomainObject.locality.getObjectTypeId(),
                "localityId",
                "authorized",
                ((Boolean) true));
    }

    @AfterReturning(
            pointcut = "execution(com.proptiger.data.pojo.response.APIResponse com.proptiger.data.mvc.CityController.getCities(..))",
            returning = "retVal")
    public void filterResponseCityListings(Object retVal) throws Throwable {
        findAndReplaceInResponseData(retVal, DomainObject.city.getObjectTypeId(), "id", "authorized", ((Boolean) true));
    }

    @SuppressWarnings("unchecked")
    private void findAndReplaceInResponseData(
            Object retVal,
            int objectTypeId,
            String objectIdTag,
            String replaceFieldTag,
            Object replaceFieldValue) {
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();

        Object data = getApiResponseData(retVal);
        if (data == null || userSubscriptionMap == null) {
            return;
        }

        List<Object> resultList = (List<Object>) data;
        for (Object result : resultList) {
            int objectId = (Integer) (((Map<String, Object>) result).get(objectIdTag));
            if (userSubscriptionMap.containsKey(objectTypeId, objectId)) {
                ((Map<String, Object>) result).put(replaceFieldTag, replaceFieldValue);
            }
        }
    }

    private Object getApiResponseData(Object retVal) {
        APIResponse apiResponse = (APIResponse) retVal;
        if (apiResponse.getError() != null) {
            return null;
        }
        return (apiResponse.getData());
    }

    private MultiKeyMap getUserSubscriptionMap() {
        try {
            ActiveUser activeUser = (ActiveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (userService.getUserSubscriptionMap(activeUser.getUserIdentifier()));
        }
        catch (Exception e) {
            return null;
        }
    }
}
