package com.proptiger.data.service.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.model.proptiger.SubscriptionPermission;
import com.proptiger.core.model.proptiger.UserSubscriptionMapping;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.service.ApplicationNameService;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.trend.TrendService;

/**
 * User subscription helper service to get data from userservice
 * @author Rajeev Pandey
 *
 */
@Service
public class UserSubscriptionHelperService {

    private final String    URL_USERDETAILS_SUBSCRIPTION_PERMISSION = "/data/v1/entity/user/subscription/permission";
    private final String    URL_USERDETAILS_SUBSCRIPTION_MAPPING = "/data/v1/entity/user/subscription/mapping";

    @Autowired
    private HttpRequestUtil httpRequestUtil;
    
    @Autowired
    private LocalityService localityService;
    
    @Autowired
    private TrendService trendService;
    

    public String getUserAppSubscriptionFilters(int userId) {

        List<SubscriptionPermission> subscriptionPermissions = getUserAppSubscriptionDetails(userId);
        List<String> filterList = new ArrayList<String>();

        int objectTypeId = 0;
        Permission permission;
        for (SubscriptionPermission subscriptionPermission : subscriptionPermissions) {

            permission = subscriptionPermission.getPermission();
            objectTypeId = permission.getObjectTypeId();
            switch (DomainObject.getFromObjectTypeId(objectTypeId)) {
                case city:
                    filterList.add("cityId==" + permission.getObjectId());
                    break;
                case locality:
                    filterList.add("localityId==" + permission.getObjectId());
                    break;
                default:
                    break;
            }
        }
        return StringUtils.join(filterList, ",");
    }

    public List<SubscriptionPermission> getUserAppSubscriptionDetails(int userId) {
        HttpHeaders header = createJsessionIdHeader();
        if(header == null){
            return new ArrayList<SubscriptionPermission>();
        }
        String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + URL_USERDETAILS_SUBSCRIPTION_PERMISSION;
        List<SubscriptionPermission> subscriptionPermissions = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl),
                header,
                SubscriptionPermission.class);
        
        return subscriptionPermissions;
    }
    
    private HttpHeaders createJsessionIdHeader() {
        HttpHeaders requestHeaders = null;
        String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
        if(jsessionId != null && !jsessionId.isEmpty()){
            requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
        }
        return requestHeaders;
    }
    
    public List<UserSubscriptionMapping> getUserSubscriptionMapping(int userId){
        HttpHeaders header = createJsessionIdHeader();
        if(header == null){
            return new ArrayList<UserSubscriptionMapping>();
        }
        String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + URL_USERDETAILS_SUBSCRIPTION_MAPPING;
        List<UserSubscriptionMapping> subscriptionPermissions = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl),
                header,
                UserSubscriptionMapping.class);
        
        return subscriptionPermissions;
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
    public MultiKeyMap getUserSubscriptionMap() {
        if (!ApplicationNameService.isB2BApplicationRequest()) {
            return null;
        }
        ActiveUser activeUser = SecurityContextUtils.getActiveUser();
        if (activeUser != null) {
            List<SubscriptionPermission> subscriptionPermissions =  getUserAppSubscriptionDetails(activeUser.getUserIdentifier());
            
            MultiKeyMap userSubscriptionMap = new MultiKeyMap();
            Permission permission;
            int objectTypeId, objectId;
            List<Integer> localityIDList = new ArrayList<Integer>();

            int objTypeIdLocality = DomainObject.locality.getObjectTypeId();
            int objTypeIdCity = DomainObject.city.getObjectTypeId();
            for (SubscriptionPermission sp : subscriptionPermissions) {
                permission = sp.getPermission();

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
                userSubscriptionMap.put(objTypeIdCity, cityId, null);
            }

            List<Integer> subscribedBuilders = getSubscribedBuilderList(activeUser.getUserIdentifier());
            for (Integer builderId : subscribedBuilders) {
                userSubscriptionMap.put(DomainObject.builder.getObjectTypeId(), builderId, builderId);
            }
            return userSubscriptionMap;
        }
        else {
            return null;
        }
    }
    
    private List<Integer> getSubscribedBuilderList(int userId) {
        FIQLSelector selector = new FIQLSelector().addAndConditionToFilter(getUserAppSubscriptionFilters(userId));
        List<Integer> builderList = new ArrayList<>();
        if (selector.getFilters() != null) {
            String builderId = "builderId";
            selector.setFields(builderId);
            selector.setGroup(builderId);

            List<Trend> list = trendService.getTrend(selector);
            for (Trend inventoryPriceTrend : list) {
                builderList.add(inventoryPriceTrend.getBuilderId());
            }
        }
        return builderList;
    }

    private Set<Integer> getCityIdListFromLocalityIdList(List<Integer> localityIDList) {
        Set<Integer> cityIdList = new HashSet<Integer>();
        FIQLSelector fiqlSelector = new FIQLSelector();
        for(Integer id: localityIDList){
            fiqlSelector.addOrConditionToFilter("localityId=="+id);
        }
        fiqlSelector.setStart(0).setRows(9999);
        List<Locality> localiltyList = localityService.getLocalities(fiqlSelector).getResults();
        for (Locality locality : localiltyList) {
            cityIdList.add(locality.getSuburb().getCityId());
        }
        return cityIdList;
    }
}
