package com.proptiger.data.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.UserService;

/**
 * This class is used for authentication based filtering of APIResponse.
 */

/*
 * TODO :: This class should be re-factored to reuse some code snippets. e.g.
 * map-lookup-and-set-auth-flag-code
 */

@Aspect
@Order(1)
@Component
public class ResponseFilteringAuth {

    @Autowired
    private UserService   userService;

    private final int     objTypeIdLocality    = DomainObject.locality.getObjectTypeId();

    private final int     objTypeIdCity        = DomainObject.city.getObjectTypeId();
    private final int     objTypeIdBuilder     = DomainObject.builder.getObjectTypeId();

    private final String  objTypeTextCity      = DomainObject.city.getText();
    private final String  objTypeTextLocality  = DomainObject.locality.getText();
    private final String  objTypeTextProject   = DomainObject.project.getText();
    private final String  objTypeTextBuilder   = DomainObject.builder.getText();

    private final String  fieldTagAuthorized   = "authorized";
    private final String  typeAheadIdSeparator = "-";

    @Autowired
    private static Logger logger               = LoggerFactory.getLogger(ResponseFilteringAuth.class);

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

        HashMap<String, Object> responseMap = (HashMap<String, Object>) data;
        List<Object> projectItemsList = (List<Object>) responseMap.get("items");
        for (Object element : projectItemsList) {
            int localityId = getEntityIdFromResponseElement(element, "localityId");
            int cityId = extractCityIdFromProjectListingResponse(element);
            if ((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                    objTypeIdCity,
                    cityId) != null)) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "execution(com.proptiger.data.pojo.response.APIResponse com.proptiger.app.mvc.AppLocalityController.getLocalityListingData(..))",
            returning = "retVal")
    public void filterResponseLocalityListings(Object retVal) throws Throwable {
        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null || userSubscriptionMap == null) {
            return;
        }

        List<Object> resultList = (List<Object>) data;
        for (Object element : resultList) {
            int localityId = getEntityIdFromResponseElement(element, "localityId");
            int cityId = getEntityIdFromResponseElement(element, "cityId");

            if ((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                    objTypeIdCity,
                    cityId) != null)) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "execution(com.proptiger.data.pojo.response.APIResponse com.proptiger.data.mvc.CityController.getCities(..))",
            returning = "retVal")
    public void filterResponseCityListings(Object retVal) throws Throwable {
        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null || userSubscriptionMap == null) {
            return;
        }

        List<Object> resultList = (List<Object>) data;
        for (Object element : resultList) {
            int cityId = getEntityIdFromResponseElement(element, "id");
            if (userSubscriptionMap.containsKey(objTypeIdCity, cityId)) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "execution(com.proptiger.data.pojo.response.APIResponse com.proptiger.data.mvc.TypeaheadController.*(..))",
            returning = "retVal")
    public void filterTypeAhead(Object retVal) throws Throwable {
        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null || userSubscriptionMap == null) {
            return;
        }

        int cityId = 0, localityId = 0;
        List<Object> resultList = (List<Object>) data;
        for (Object element : resultList) {
            Map<String, Object> map = ((Map<String, Object>) element);
            String entityType = String.valueOf(map.get("type"));
            String typeAheadRespId = String.valueOf(map.get("id"));

            if (entityType.equalsIgnoreCase(objTypeTextCity)) {
                cityId = extractEntityIdFromTypeaheadResponseId(typeAheadRespId);
                if (userSubscriptionMap.get(objTypeIdCity, cityId) != null) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, true);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextLocality)) {
                localityId = extractEntityIdFromTypeaheadResponseId(typeAheadRespId);
                cityId = getEntityIdFromResponseElement(element, "cityId");
                if ((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null)) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, true);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextProject)) {
                cityId = getEntityIdFromResponseElement(element, "cityId");
                localityId = getEntityIdFromResponseElement(element, "localityId");
                if ((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null)) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, true);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextBuilder)) {
                String[] typeAheadIdParts = typeAheadRespId.split(typeAheadIdSeparator);
                int builderId = Integer.parseInt(typeAheadIdParts[typeAheadIdParts.length - 1]);
                if (userSubscriptionMap.containsKey(objTypeIdBuilder, builderId)) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, true);
                }
            }
        }
    }

    private int extractEntityIdFromTypeaheadResponseId(String typeaheadRespId) {
        try {
            return Integer.parseInt(StringUtils.split(typeaheadRespId, '-')[2]);
        }
        catch (Exception e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private int getEntityIdFromResponseElement(Object response, String entityTag) {
        try {
            return ((Integer) (((Map<String, Object>) response).get(entityTag)));
        }
        catch (Exception e) {
            return -1;
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

    private Object getApiResponseData(Object retVal) {
        if (retVal == null || !(retVal instanceof APIResponse)) {
            return null;
        }
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
