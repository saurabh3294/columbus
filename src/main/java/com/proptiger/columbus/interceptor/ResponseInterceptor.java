package com.proptiger.columbus.interceptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.enums.Application;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.model.user.User.WhoAmIDetail;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;

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

    private final int       maxRowsForBuilderList        = 20000;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private static Logger   logger                       = LoggerFactory.getLogger(ResponseInterceptor.class);

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.TypeaheadListing))",
            returning = "retVal")
    public void filterTypeAhead(Object retVal) throws Throwable {
        if (RequestHolderUtil.getApplicationTypeFromRequest() == null || !RequestHolderUtil
                .getApplicationTypeFromRequest().equals(Application.B2B)) {
            logger.info("Not a B2B request. Skipping authorized check");
            return;
        }
        logger.debug("TIME AT STEP 1: " + new Date().getTime());
        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null) {
            return;
        }
        logger.debug("TIME AT STEP 17: " + new Date().getTime());
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
        logger.debug("TIME AT STEP 18: " + new Date().getTime());
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
        int userId = 0;
        logger.debug("TIME AT STEP 2: " + new Date().getTime());
        try {
            URI uri = URI.create(UriComponentsBuilder
                    .fromUriString(
                            PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                    .getRequiredPropertyAsString(PropertyKeys.WHO_AM_I_URL)).build().encode()
                    .toString());
            HttpHeaders requestHeaders = new HttpHeaders();
            String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
            logger.info("COOKIE FOUND: " + jsessionId);
            requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
            WhoAmIDetail whoAmI = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                    uri,
                    requestHeaders,
                    WhoAmIDetail.class);
            if (whoAmI != null) {
                userId = whoAmI.getUserId();
                logger.info("USER ID IDENTIFIED: " + userId);
            }
            logger.debug("TIME AT STEP 3: " + new Date().getTime());
        }
        catch (Exception e) {
            logger.error("Error in extracting user id", e);
            userId = 155124;
        }
        return getUserSubscriptionMap(userId);
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
        logger.debug("TIME AT STEP 6: " + new Date().getTime());
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
        logger.debug("TIME AT STEP 7: " + new Date().getTime());

        /*
         * populating psuedo permissions for city if any locality in that city
         * is permitted
         */
        Set<Integer> cityIdList = getCityIdListFromLocalityIdList(localityIDList);
        for (int cityId : cityIdList) {
            userSubscriptionMap.put(objTypeIdCity, cityId, cityId);
        }
        logger.debug("TIME AT STEP 10: " + new Date().getTime());

        List<Integer> subscribedBuilders = getSubscribedBuilderList(userId);
        for (Integer builderId : subscribedBuilders) {
            userSubscriptionMap.put(DomainObject.builder.getObjectTypeId(), builderId, builderId);
        }
        logger.debug("TIME AT STEP 16: " + new Date().getTime());
        return userSubscriptionMap;
    }

    private List<Permission> getUserPermissions(int userId) {
        logger.debug("TIME AT STEP 4: " + new Date().getTime());
        URI uri = URI.create(UriComponentsBuilder
                .fromUriString(
                        PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                .getRequiredPropertyAsString(PropertyKeys.PERMISSION_API_URL) + "?userId=" + userId)
                .build().encode().toString());
        List<Permission> permissions = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Permission.class);
        logger.debug("TIME AT STEP 5: " + new Date().getTime());
        return permissions;
    }

    private Set<Integer> getCityIdListFromLocalityIdList(List<Integer> localityIDList) {
        logger.debug("TIME AT STEP 8: " + new Date().getTime());
        Set<Integer> cityIdList = new HashSet<Integer>();
        List<Locality> localiltyList = getLocalityListFromLocalityIds(localityIDList);
        for (Locality locality : localiltyList) {
            cityIdList.add(locality.getSuburb().getCityId());
        }
        logger.debug("TIME AT STEP 9: " + new Date().getTime());
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
        logger.debug("TIME AT STEP 11: " + new Date().getTime());
        for (int i = 0; i < size; i = i + maxPermissionCountForApiCall) {
            List<Permission> partialPermissions = permissions.subList(
                    i,
                    Math.min(size, i + maxPermissionCountForApiCall));
            FIQLSelector selector = getUserAppSubscriptionFilters(partialPermissions);
            logger.debug("TIME AT STEP 14: " + new Date().getTime());
            if (selector.getFilters() != null) {
                String builderId = "builderId";
                selector.setFields(builderId);
                selector.setGroup(builderId);
                selector.setRows(maxRowsForBuilderList);

                String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                        .getRequiredPropertyAsString(PropertyKeys.TREND_API_URL) + "?" + selector.getStringFIQL();
                URI uri = URI.create(stringUrl);

                List<Trend> list = httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Trend.class);
                for (Trend inventoryPriceTrend : list) {
                    builderList.add(inventoryPriceTrend.getBuilderId());
                }
            }
            logger.debug("TIME AT STEP 15: " + new Date().getTime());
        }
        return builderList;
    }

    public FIQLSelector getUserAppSubscriptionFilters(List<Permission> permissions) {
        logger.debug("TIME AT STEP 12: " + new Date().getTime());
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
        logger.debug("TIME AT STEP 13: " + new Date().getTime());
        return selector;
    }
}