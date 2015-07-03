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
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.model.user.User.WhoAmIDetail;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;

@Aspect
@Order(1)
@Component
public class ResponseInterceptor {
    private final int           objTypeIdLocality                 = DomainObject.locality.getObjectTypeId();
    private final int           objTypeIdCity                     = DomainObject.city.getObjectTypeId();
    private final int           objTypeIdBuilder                  = DomainObject.builder.getObjectTypeId();
    private final String        objTypeTextCity                   = DomainObject.city.getText();
    private final String        objTypeTextLocality               = DomainObject.locality.getText();
    private final String        objTypeTextProject                = DomainObject.project.getText();
    private final String        objTypeTextBuilder                = DomainObject.builder.getText();

    private static final String FIELD_TAG_AUTHORIZED              = "authorized";
    private static final String TYPEAHEAD_ID_SEPARATOR            = "-";

    private static final int    MAX_LOCALITY_ID_COUNT_FOR_APICALL = 512;
    private static final int    MAX_PERMISSION_COUNT_FOR_APICALL  = 256;

    private static final String BUILDER_ID                        = "builderId";
    private static final String CITY_ID                           = "cityId";
    private static final String LOCALITY_ID                       = "localityId";
    private static final int    IDS_LIMIT_FOR_URL                 = 50;
    @Autowired
    private HttpRequestUtil     httpRequestUtil;

    @Autowired
    private static Logger       logger                            = LoggerFactory.getLogger(ResponseInterceptor.class);

    @SuppressWarnings("unchecked")
    @AfterReturning(
            pointcut = "@annotation(com.proptiger.core.annotations.Intercepted.TypeaheadListing))",
            returning = "retVal")
    public void filterTypeAhead(Object retVal) throws Throwable {
        if (RequestHolderUtil.getApplicationTypeFromRequest() == null || !RequestHolderUtil
                .getApplicationTypeFromRequest().equals(Application.B2B)) {
            logger.debug("Not a B2B request. Skipping authorized check");
            return;
        }
        logger.debug("TIME AT STEP 1: {}", new Date().getTime());
        Object data = getApiResponseData(retVal);
        MultiKeyMap userSubscriptionMap = getUserSubscriptionMap();
        if (data == null) {
            return;
        }
        logger.debug("TIME AT STEP 17: {}", new Date().getTime());
        int cityId = 0, localityId = 0;
        List<Object> resultList = (List<Object>) data;
        for (Object element : resultList) {
            Map<String, Object> map = (Map<String, Object>) element;
            String entityType = String.valueOf(map.get("type"));
            String typeAheadRespId = String.valueOf(map.get("id"));

            if (entityType.equalsIgnoreCase(objTypeTextCity)) {
                cityId = extractEntityIdFromTypeaheadResponseId(typeAheadRespId);
                if (!(userSubscriptionMap.get(objTypeIdCity, cityId) != null)) {
                    ((Map<String, Object>) element).put(FIELD_TAG_AUTHORIZED, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextLocality)) {
                localityId = extractEntityIdFromTypeaheadResponseId(typeAheadRespId);
                cityId = getEntityIdFromResponseElement(element, "cityId");
                if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null))) {
                    ((Map<String, Object>) element).put(FIELD_TAG_AUTHORIZED, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextProject)) {
                cityId = getEntityIdFromResponseElement(element, "cityId");
                localityId = getEntityIdFromResponseElement(element, "localityId");
                if (!((userSubscriptionMap.containsKey(objTypeIdLocality, localityId)) || (userSubscriptionMap.get(
                        objTypeIdCity,
                        cityId) != null))) {
                    ((Map<String, Object>) element).put(FIELD_TAG_AUTHORIZED, false);
                }
            }
            else if (entityType.equalsIgnoreCase(objTypeTextBuilder)) {
                String[] typeAheadIdParts = typeAheadRespId.split(TYPEAHEAD_ID_SEPARATOR);
                int builderId = Integer.parseInt(typeAheadIdParts[typeAheadIdParts.length - 1]);
                if (!(userSubscriptionMap.containsKey(objTypeIdBuilder, builderId))) {
                    ((Map<String, Object>) element).put(FIELD_TAG_AUTHORIZED, false);
                }
            }
        }
        logger.debug("TIME AT STEP 18: {}", new Date().getTime());
    }

    private Object getApiResponseData(Object retVal) {
        if (retVal == null || !(retVal instanceof APIResponse)) {
            return null;
        }
        APIResponse apiResponse = (APIResponse) retVal;
        if (apiResponse.getError() != null) {
            return null;
        }
        return apiResponse.getData();
    }

    private MultiKeyMap getUserSubscriptionMap() {
        int userId = 0;
        logger.debug("TIME AT STEP 2: {}", new Date().getTime());
        try {
            URI uri = URI.create(UriComponentsBuilder
                    .fromUriString(
                            PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                    .getRequiredPropertyAsString(CorePropertyKeys.WHO_AM_I_URL)).build().encode()
                    .toString());
            HttpHeaders requestHeaders = new HttpHeaders();
            String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
            logger.info("COOKIE FOUND: {}", jsessionId);
            requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
            WhoAmIDetail whoAmI = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                    uri,
                    requestHeaders,
                    WhoAmIDetail.class);
            if (whoAmI != null) {
                userId = whoAmI.getUserId();
                logger.info("USER ID IDENTIFIED: {}", userId);
            }
            logger.debug("TIME AT STEP 3: {}", new Date().getTime());
        }
        catch (Exception e) {
            logger.error("Error in extracting user id", e);
        }
        return getUserSubscriptionMap(userId);
    }

    private int extractEntityIdFromTypeaheadResponseId(String typeaheadRespId) {
        try {
            return Integer.parseInt(StringUtils.split(typeaheadRespId, '-')[2]);
        }
        catch (Exception e) {
            logger.warn("Not able to parse-extractEntityIdFromTypeheadResonceId", e);
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private int getEntityIdFromResponseElement(Object response, String entityTag) {
        try {
            return ((Integer) (((Map<String, Object>) response).get(entityTag)));
        }
        catch (Exception e) {
            logger.warn("Caught Exception in getEntityIdFromResponceElement", e);
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
        logger.debug("TIME AT STEP 6: {}", new Date().getTime());
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
        logger.debug("TIME AT STEP 7: {}", new Date().getTime());

        /*
         * populating psuedo permissions for city if any locality in that city
         * is permitted
         */
        Set<Integer> cityIdList = getCityIdListFromLocalityIdList(localityIDList);
        for (int cityId : cityIdList) {
            userSubscriptionMap.put(objTypeIdCity, cityId, cityId);
        }
        logger.debug("TIME AT STEP 10: {}", new Date().getTime());

        Set<String> subscribedBuilders = getSubscribedBuilderList(userId);
        for (String builderId : subscribedBuilders) {
            int bId = Integer.parseInt(builderId);
            userSubscriptionMap.put(DomainObject.builder.getObjectTypeId(), bId, bId);
        }
        logger.debug("TIME AT STEP 16: {}", new Date().getTime());
        return userSubscriptionMap;
    }

    private List<Permission> getUserPermissions(int userId) {
        logger.debug("TIME AT STEP 4: {}", new Date().getTime());
        HttpHeaders requestHeaders = new HttpHeaders();
        String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
        requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
        URI uri = URI
                .create(UriComponentsBuilder
                        .fromUriString(
                                PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                        .getRequiredPropertyAsString(CorePropertyKeys.PERMISSION_API_URL)
                                        + "?userId="
                                        + userId).build().encode().toString());
        List<Permission> permissions = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                uri,
                requestHeaders,
                Permission.class);
        logger.debug("TIME AT STEP 5: {}", new Date().getTime());
        return permissions;
    }

    private Set<Integer> getCityIdListFromLocalityIdList(List<Integer> localityIDList) {
        logger.debug("TIME AT STEP 8: {}", new Date().getTime());
        Set<Integer> cityIdList = new HashSet<Integer>();
        List<Locality> localiltyList = getLocalityListFromLocalityIds(localityIDList);
        for (Locality locality : localiltyList) {
            cityIdList.add(locality.getSuburb().getCityId());
        }
        logger.debug("TIME AT STEP 9: {}", new Date().getTime());
        return cityIdList;
    }

    private List<Locality> getLocalityListFromLocalityIds(List<Integer> localityIds) {
        List<Locality> localities = new ArrayList<>();
        if (localityIds != null) {
            int size = localityIds.size();
            for (int i = 0; i < size; i = i + MAX_LOCALITY_ID_COUNT_FOR_APICALL) {
                List<Integer> partialLocalityIds = localityIds.subList(
                        i,
                        Math.min(size, i + MAX_LOCALITY_ID_COUNT_FOR_APICALL));
                URI uri = URI
                        .create(UriComponentsBuilder
                                .fromUriString(
                                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                                .getRequiredPropertyAsString(CorePropertyKeys.LOCALITY_API_URL)
                                                + "?"
                                                + URLGenerationConstants.SELECTOR
                                                + String.format(
                                                        URLGenerationConstants.SELECTOR_GET_CITYIDS_BY_LOCALITYIDS,
                                                        StringUtils.join(partialLocalityIds, ","),
                                                        MAX_LOCALITY_ID_COUNT_FOR_APICALL)).build().encode().toString());
                List<Locality> partialLocalities = httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                        uri,
                        Locality.class);
                localities.addAll(partialLocalities);
            }
        }
        return localities;
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS)
    @SuppressWarnings("unchecked")
    private Set<String> getSubscribedBuilderList(int userId) {
        logger.debug("xxxyyyzzz :: Inside *getSubscribedBuilderList*");
        List<Permission> permissions = getUserPermissions(userId);
        Set<String> bIdsHash = new HashSet<>();

        int size = permissions.size();
        logger.debug("xxxyyyzzz :: Permissions Size = {}", size);

        logger.debug("TIME AT STEP 11: {}", new Date().getTime());
        for (int i = 0; i < size; i = i + MAX_PERMISSION_COUNT_FOR_APICALL) {
            logger.debug("xxxyyyzzz :: enter for loop :: i = {}", i);
            List<Permission> partialPermissions = permissions.subList(
                    i,
                    Math.min(size, i + MAX_PERMISSION_COUNT_FOR_APICALL));

            List<String> requests = getUserAppSubscriptionRequests(partialPermissions);
            logger.debug("TIME AT STEP 14: {}", new Date().getTime());
            for (String req : requests) {
                String stringUrl = PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                        .getRequiredPropertyAsString(CorePropertyKeys.PROJECT_LISTING_API_URL)
                        + "?"
                        + URLGenerationConstants.SELECTOR
                        + req;
                logger.debug("xxxyyyzzz :: stringUrl : {}", stringUrl);
                URI uri = URI.create(UriComponentsBuilder.fromUriString(stringUrl).build().encode().toString());
                logger.debug("xxxyyyzzz :: URI  : {}", uri.toString());
                logger.debug("xxxyyyzzz :: Attempt list retrieval");
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.set(Constants.APPLICATION_TYPE_HEADER, Application.B2B.name());
                Map<String, Object> response = httpRequestUtil.getInternalApiResultAsTypeFromCache(
                        uri,
                        requestHeaders,
                        Map.class);
                Map<String, List<Map<String, Long>>> facets = (Map<String, List<Map<String, Long>>>) response
                        .get("facets");
                List<Map<String, Long>> builderIds = facets.get(BUILDER_ID);
                logger.debug("xxxyyyzzz :: list retrieved : size = {}", (builderIds != null
                        ? builderIds.size()
                        : "null"));
                for (Map<String, Long> m : builderIds) {
                    bIdsHash.addAll(m.keySet());
                }
            }
            logger.debug("xxxyyyzzz :: exit for loop :: i = {}", i);
            logger.debug("TIME AT STEP 15: {}", new Date().getTime());
        }
        logger.debug("xxxyyyzzz :: Exiting *getSubscribedBuilderList*");

        return bIdsHash;
    }

    private List<String> getUserAppSubscriptionRequests(List<Permission> permissions) {
        logger.debug("TIME AT STEP 12: {}", new Date().getTime());
        List<Integer> cityIds = new ArrayList<>();
        List<Integer> localityIds = new ArrayList<>();
        for (Permission permission : permissions) {
            int objectTypeId = permission.getObjectTypeId();

            switch (DomainObject.getFromObjectTypeId(objectTypeId)) {

                case city:
                    cityIds.add(permission.getObjectId());
                    break;

                case locality:
                    localityIds.add(permission.getObjectId());
                    break;

                default:
                    break;
            }
        }
        List<String> requests = new ArrayList<>();
        if (!cityIds.isEmpty()) {
            generateLimitedLengthUrls(requests, CITY_ID, cityIds);
        }
        if (!localityIds.isEmpty()) {
            generateLimitedLengthUrls(requests, LOCALITY_ID, localityIds);
        }
        logger.debug("TIME AT STEP 13: {}", new Date().getTime());

        return requests;
    }

    private void generateLimitedLengthUrls(List<String> requests, String idLabel, List<Integer> ids) {
        int i;
        for (i = 0; i < ids.size() - IDS_LIMIT_FOR_URL + 1; i += IDS_LIMIT_FOR_URL) {
            requests.add(String.format(
                    URLGenerationConstants.SELECTOR_GET_BUILDERIDS_AS_FACET,
                    idLabel,
                    ids.subList(i, i + IDS_LIMIT_FOR_URL).toString(),
                    BUILDER_ID));
        }
        if (i < ids.size()) {
            requests.add(String.format(
                    URLGenerationConstants.SELECTOR_GET_BUILDERIDS_AS_FACET,
                    idLabel,
                    ids.subList(i, ids.size()).toString(),
                    BUILDER_ID));
        }

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