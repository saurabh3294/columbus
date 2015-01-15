package com.proptiger.data.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApplicationNameService;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.service.user.UserSubscriptionService;

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
public class ResponseInterceptorListing {

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    private final int               objTypeIdLocality  = DomainObject.locality.getObjectTypeId();

    private final int               objTypeIdCity      = DomainObject.city.getObjectTypeId();

    private final String            fieldTagAuthorized = "authorized";

    @Autowired
    private static Logger           logger             = LoggerFactory.getLogger(ResponseInterceptorListing.class);

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.ProjectListing)",
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
            if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                    objTypeIdCity,
                    cityId) != null))) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, false);
            }
        }
    }

    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.ProjectDetail)",
            returning = "retVal")
    public void filterResponseProjectDetails(Object retVal) throws Throwable {

        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null || userSubscriptionMap == null) {
             return;
        }

        if (!(data instanceof Project)) {
            throw new ProAPIException("Unrecognised Response from ProjectDetail API.");
        }

        Project project = (Project) data;
        int localityId = project.getLocalityId();
        int cityId = project.getLocality().getSuburb().getCity().getId();
        if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                objTypeIdCity,
                cityId) != null))) {
            project.setAuthorized(false);
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.LocalityListing)",
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

            if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                    objTypeIdCity,
                    cityId) != null))) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.CityListing))",
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
            if (!(userSubscriptionMap.containsKey(objTypeIdCity, cityId))) {
                ((Map<String, Object>) element).put(fieldTagAuthorized, false);
            }
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
        if (!ApplicationNameService.isB2BApplicationRequest()) {
            return null;
        }
        ActiveUser activeUser = SecurityContextUtils.getActiveUser();
        if (activeUser != null) {
            return (userSubscriptionService.getUserSubscriptionMap(activeUser.getUserIdentifier()));
        }
        else {
            return null;
        }
    }
}