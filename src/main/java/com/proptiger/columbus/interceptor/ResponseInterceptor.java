package com.proptiger.columbus.interceptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApplicationNameService;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Aspect
@Order(1)
@Component
public class ResponseInterceptor {
    private final int       objTypeIdLocality            = DomainObject.locality.getObjectTypeId();
    private final int       objTypeIdCity                = DomainObject.city.getObjectTypeId();
    private final int       objTypeIdBuilder             = DomainObject.builder.getObjectTypeId();
    private final String    objTypeTextCity              = DomainObject.city.getText();
    private final String    objTypeTextLocality          = DomainObject.locality.getText();
    private final String    objTypeTextProject           = DomainObject.project.getText();
    private final String    objTypeTextBuilder           = DomainObject.builder.getText();

    private final String    fieldTagAuthorized           = "authorized";
    private final String    typeAheadIdSeparator         = "-";

    private final int       maxLocalityIdCountForApiCall = 512;
    private final int       maxPermissionCountForApiCall = 256;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private static Logger   logger                       = LoggerFactory.getLogger(ResponseInterceptor.class);

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.TypeaheadListing))",
            returning = "retVal")
    public void filterTypeAhead(Object retVal) throws Throwable {
        if (!!ApplicationNameService.isB2BApplicationRequest()) {
            return;
        }
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
                if (!(userSubscriptionMap.get(objTypeIdCity, cityId) != null)) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextLocality)) {
                localityId = extractEntityIdFromTypeaheadResponseId(typeAheadRespId);
                cityId = getEntityIdFromResponseElement(element, "cityId");
                if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null))) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextProject)) {
                cityId = getEntityIdFromResponseElement(element, "cityId");
                localityId = getEntityIdFromResponseElement(element, "localityId");
                if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null))) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextBuilder)) {
                String[] typeAheadIdParts = typeAheadRespId.split(typeAheadIdSeparator);
                int builderId = Integer.parseInt(typeAheadIdParts[typeAheadIdParts.length - 1]);
                if (!(userSubscriptionMap.containsKey(objTypeIdBuilder, builderId))) {
                    ((Map<String, Object>) element).put(fieldTagAuthorized, false);
                }
            }
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
            return (getUserSubscriptionMap(activeUser.getUserIdentifier()));
        }
        catch (Exception e) {
            return null;
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

    /*
     * Returns a MultiKeyMap with 2 keys. [K1 = ObjectTypeId, K2 = ObjectId],
     * [Value = Permission Object]
     * 
     * [1]. In case of locality, checking existence of the key should be enough.
     * [2]. For city, a key will exist (with a value null), even if the user has
     * a permission for some locality in the city. To check for full city
     * permission, check if the value mapped to that key is not null.
     */

    public MultiKeyMap getUserSubscriptionMap(int userId) {

        List<Permission> permissions = getUserPermissions(userId);
        MultiKeyMap userSubscriptionMap = new MultiKeyMap();
        int objectTypeId, objectId;
        List<Integer> localityIDList = new ArrayList<Integer>();

        int objTypeIdLocality = DomainObject.locality.getObjectTypeId();
        int objTypeIdCity = DomainObject.city.getObjectTypeId();
        for (Permission permission : permissions) {
            if (permission != null) {
                objectTypeId = permission.getObjectTypeId();
                objectId = permission.getObjectId();
                userSubscriptionMap.put(objectTypeId, objectId, permission);
                if (objectTypeId == objTypeIdLocality) {
                    localityIDList.add(objectId);
                }
            }
        }

        /*
         * populating psuedo permissions for city if any locality in that city
         * is permitted
         */
        Set<Integer> cityIdList = getCityIdListFromLocalityIdList(localityIDList);
        for (int cityId : cityIdList) {
            userSubscriptionMap.put(objTypeIdCity, cityId, cityId);
        }

        List<Integer> subscribedBuilders = getSubscribedBuilderList(userId);
        for (Integer builderId : subscribedBuilders) {
            userSubscriptionMap.put(DomainObject.builder.getObjectTypeId(), builderId, builderId);
        }
        return userSubscriptionMap;
    }

    private List<Permission> getUserPermissions(int userId) {
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(PropertyKeys.PERMISSION_API_URL) + "?userId=" + userId)
                .build().encode().toString());
        List<Permission> permissions = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Permission.class);
        return permissions;
    }

    private Set<Integer> getCityIdListFromLocalityIdList(List<Integer> localityIDList) {
        Set<Integer> cityIdList = new HashSet<Integer>();
        List<Locality> localiltyList = getLocalityListFromLocalityIds(localityIDList);
        for (Locality locality : localiltyList) {
            cityIdList.add(locality.getSuburb().getCityId());
        }
        return cityIdList;
    }

    private List<Locality> getLocalityListFromLocalityIds(List<Integer> localityIds) {
        List<Locality> localities = new ArrayList<>();
        if (localityIds != null) {
            int size = localityIds.size();
            for (int i = 0; i < size; i = i + maxLocalityIdCountForApiCall) {
                List<Integer> partialLocalityIds = localityIds.subList(
                        i,
                        Math.min(size, i + maxLocalityIdCountForApiCall));
                URI uri = URI.create(UriComponentsBuilder
                        .fromUriString(
                                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                        .getRequiredPropertyAsString(PropertyKeys.LOCALITY_API_URL)
                                        + "?"
                                        + URLGenerationConstants.Selector
                                        + String.format(
                                                URLGenerationConstants.SelectorGetCityIdsByLocalityIds,
                                                StringUtils.join(partialLocalityIds, ","),
                                                maxLocalityIdCountForApiCall)).build().encode().toString());
                List<Locality> partialLocalities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                        uri,
                        Locality.class);
                localities.addAll(partialLocalities);
            }
        }
        return localities;
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    private List<Integer> getSubscribedBuilderList(int userId) {
        List<Permission> permissions = getUserPermissions(userId);
        List<Integer> builderList = new ArrayList<>();

        int size = permissions.size();
        for (int i = 0; i < size; i = i + maxPermissionCountForApiCall) {
            List<Permission> partialPermissions = permissions.subList(
                    i,
                    Math.min(size, i + maxPermissionCountForApiCall));
            FIQLSelector selector = getUserAppSubscriptionFilters(partialPermissions);

            if (selector.getFilters() != null) {
                String builderId = "builderId";
                selector.setFields(builderId);
                selector.setGroup(builderId);
                selector.setRows(partialPermissions.size());

                String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                        .getRequiredPropertyAsString(PropertyKeys.TREND_API_URL) + "?" + selector.getStringFIQL();
                URI uri = URI.create(stringUrl);

                List<Trend> list = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Trend.class);
                for (Trend inventoryPriceTrend : list) {
                    builderList.add(inventoryPriceTrend.getBuilderId());
                }
            }
        }
        return builderList;
    }

    public FIQLSelector getUserAppSubscriptionFilters(List<Permission> permissions) {
        FIQLSelector selector = new FIQLSelector();
        for (Permission permission : permissions) {
            int objectTypeId = permission.getObjectTypeId();

            switch (DomainObject.getFromObjectTypeId(objectTypeId)) {

                case city:
                    selector.addOrConditionToFilter("cityId==" + permission.getObjectId());
                    break;

                case locality:
                    selector.addOrConditionToFilter("localityId==" + permission.getObjectId());
                    break;

                default:
                    break;
            }
        }
        return selector;
    }
}