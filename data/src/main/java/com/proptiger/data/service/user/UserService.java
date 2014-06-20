package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.external.dto.CustomUser;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomCity;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomLocality;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.UserAppSubscription;
import com.proptiger.data.model.CompanySubscription;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Permission;
import com.proptiger.data.model.SubscriptionPermission;
import com.proptiger.data.model.SubscriptionSection;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.model.UserSubscriptionMapping;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.SubscriptionPermissionDao;
import com.proptiger.data.repo.UserSubscriptionMappingDao;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.util.UtilityClass;

/**
 * Service class to get if user have already enquired about an entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class UserService {

    @Value("${enquired.within.days}")
    private Integer               enquiredWithinDays;

    @Autowired
    private EnquiryDao            enquiryDao;

    @Autowired
    private ForumUserDao          forumUserDao;

    @Autowired
    private UserPreferenceService preferenceService;

    @Autowired
    private LocalityService       localityService;

    @Autowired
    UserSubscriptionMappingDao    userSubscriptionMappingDao;

    @Autowired
    SubscriptionPermissionDao     subscriptionPermissionDao;

    public boolean isRegistered(String email) {
        if (forumUserDao.findByEmail(email) != null) {
            return true;
        }

        return false;
    }

    /**
     * Returns details forum user object for a user including all his dashboards
     * and preferences and subscription details
     * 
     * @param userInfo
     * @return {@link ForumUser}
     */
    @Transactional
    public CustomUser getUserDetails(int userId) {
        ForumUser user = forumUserDao.findByUserId(userId);
        CustomUser customUser = new CustomUser();
        customUser.setId(user.getUserId());
        customUser.setEmail(user.getEmail());
        customUser.setFirstName(user.getUsername());
        customUser.setContactNumber(Long.toString(user.getContact()));
        customUser.setProfileImageUrl(user.getFbImageUrl());

        Hibernate.initialize(user.getDashboards());
        customUser.setDashboards(user.getDashboards());

        setAppDetails(customUser, user);
        return customUser;
    }

    /**
     * Sets app specific details for user object
     * 
     * @param user
     * @return {@link ForumUser}
     */
    private CustomUser setAppDetails(CustomUser customUser, ForumUser user) {
        HashMap<Application, UserAppDetail> appDetailsMap = new HashMap<>();

        for (UserPreference preference : preferenceService.getUserPreferences(user.getUserId())) {
            UserAppDetail appDetail = new UserAppDetail();
            appDetail.setPreferences(preference.getPreference());
            appDetailsMap.put(preference.getApp(), appDetail);
        }

        List<UserAppSubscription> subscriptions = new ArrayList<>();
        for (UserSubscriptionMapping mapping : user.getUserSubscriptionMappings()) {
            CompanySubscription subscription = mapping.getSubscription();
            customUser.getCompanyIds().add(subscription.getCompanyId());

            if (subscription.getExpiryTime().getTime() < new Date().getTime()) {
                continue;
            }

            UserAppSubscription appSubscription = new UserAppSubscription();
            for (SubscriptionSection section : subscription.getSections()) {
                appSubscription.getSections().add(section.getSection());
            }
            appSubscription.setExpiryDate(subscription.getExpiryTime());

            Hibernate.initialize(subscription.getCompany());
            appSubscription.setCompany(subscription.getCompany());

            setUserAppSubscriptionDetails(subscription.getPermissions(), appSubscription);
            subscriptions.add(appSubscription);
        }

        if (!appDetailsMap.containsKey(Application.B2B)) {
            appDetailsMap.put(Application.B2B, new UserAppDetail());
        }
        appDetailsMap.get(Application.B2B).setSubscriptions(subscriptions);
        customUser.setAppDetails(appDetailsMap);
        return customUser;
    }

    /**
     * 
     * @param subscriptionPermissions
     * @param userAppSubscription
     * @return
     */
    private UserAppSubscription setUserAppSubscriptionDetails(
            List<SubscriptionPermission> subscriptionPermissions,
            UserAppSubscription userAppSubscription) {
        List<Integer> subscribedIds = new ArrayList<>();
        for (SubscriptionPermission subscriptionPermission : subscriptionPermissions) {
            subscribedIds.add(subscriptionPermission.getPermission().getObjectId());
        }

        if (!subscribedIds.isEmpty()) {
            userAppSubscription.setUserType(DomainObject.getFromObjectTypeId(
                    subscriptionPermissions.get(0).getPermission().getObjectTypeId()).toString());

            String json = "{\"filters\":{\"and\":[{\"equal\":{\"" + userAppSubscription.getUserType()
                    + "Id\":["
                    + StringUtils.join(subscribedIds, ',')
                    + "]}}]},\"paging\":{\"start\":0,\"rows\":9999}}";

            List<Locality> localities = localityService.getLocalities(new Gson().fromJson(json, Selector.class))
                    .getResults();

            @SuppressWarnings("unchecked")
            Map<Integer, List<Locality>> cityGroupedLocalities = (Map<Integer, List<Locality>>) UtilityClass
                    .groupFieldsAsPerKeys(localities, Arrays.asList("cityId"));

            for (Integer cityId : cityGroupedLocalities.keySet()) {
                userAppSubscription.setCityCount(userAppSubscription.getCityCount() + 1);
                List<Locality> cityLocalities = cityGroupedLocalities.get(cityId);
                CustomCity city = new CustomCity();
                city.setId(cityId);
                city.setName(cityLocalities.get(0).getSuburb().getCity().getLabel());

                List<CustomLocality> cityCustomLocalities = new ArrayList<>();
                for (Locality locality : cityLocalities) {
                    userAppSubscription.setLocalityCount(userAppSubscription.getLocalityCount() + 1);

                    if (locality.getProjectCount() != null) {
                        userAppSubscription.setProjectCount(userAppSubscription.getProjectCount() + locality
                                .getProjectCount());
                    }

                    CustomLocality cityLocality = new CustomLocality();
                    cityLocality.setId(locality.getLocalityId());
                    cityLocality.setName(locality.getLabel());
                    cityCustomLocalities.add(cityLocality);
                }
                city.setLocalities(cityCustomLocalities);
                userAppSubscription.getCities().add(city);
            }
        }
        return userAppSubscription;
    }

    /**
     * @param userId
     *            userId for which subscription permissions are needed.
     * @return List of subscriptionPermissions or an empty-list if there are no
     *         permissions installed.
     */
    private List<SubscriptionPermission> getUserAppSubscriptionDetails(int userId) {
        List<UserSubscriptionMapping> userSubscriptionMappingList = userSubscriptionMappingDao.findAllByUserId(userId);
        if (userSubscriptionMappingList == null) {
            return (new ArrayList<SubscriptionPermission>());
        }

        List<Integer> subscriptionIdList = new ArrayList<Integer>();
        for (UserSubscriptionMapping usm : userSubscriptionMappingList) {
            subscriptionIdList.add(usm.getSubscriptionId());
        }

        List<SubscriptionPermission> subscriptionPermissions = subscriptionPermissionDao
                .findAllBySubscriptionId(subscriptionIdList);
        if (subscriptionPermissions == null) {
            return (new ArrayList<SubscriptionPermission>());
        }

        return subscriptionPermissions;
    }

    public MultiKeyMap getUserSubscriptionMap(int userId) {
        List<SubscriptionPermission> subscriptionPermissions = getUserAppSubscriptionDetails(userId);
        MultiKeyMap userSubscriptionMap = new MultiKeyMap();
        Permission permission;
        int objectTypeId, objectId;
        for (SubscriptionPermission sp : subscriptionPermissions) {
            permission = sp.getPermission();

            if (permission != null) {
                objectTypeId = permission.getObjectTypeId();
                objectId = permission.getObjectId();
                userSubscriptionMap.put(objectTypeId, objectId, permission);
                if (objectTypeId == DomainObject.locality.getObjectTypeId()) {
                    int cityId = getCityIdFromLocalityId(objectId);
                    userSubscriptionMap.put(DomainObject.city.getObjectTypeId(), cityId, null);
                }
            }
        }
        return userSubscriptionMap;
    }

    private int getCityIdFromLocalityId(int localityId) {
        try {
            return localityService.getLocality(localityId).getSuburb().getCityId();
        }
        catch (Exception ex) {
            return -1;
        }
    }

    /**
     * Get if user have already enquired a entity
     * 
     * @param projectId
     * @param userId
     * @return
     */
    public AlreadyEnquiredDetails hasEnquired(Integer projectId, Integer userId) {
        String email = forumUserDao.findEmailByUserId(userId);
        Enquiry enquiry = null;
        AlreadyEnquiredDetails alreadyEnquiredDetails = new AlreadyEnquiredDetails(null, false, enquiredWithinDays);
        if (projectId != null) {
            List<Enquiry> enquiries = enquiryDao.findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(email, new Long(
                    projectId));
            if (enquiries != null && !enquiries.isEmpty()) {
                enquiry = enquiries.get(0);
            }

            if (enquiry != null) {
                alreadyEnquiredDetails.setLastEnquiryDate(enquiry.getCreatedDate());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -enquiredWithinDays);
                Date date = cal.getTime();
                if (enquiry.getCreatedDate().compareTo(date) >= 0) {
                    alreadyEnquiredDetails.setHasValidEnquiry(true);
                }
            }
        }
        return alreadyEnquiredDetails;
    }

    public static class AlreadyEnquiredDetails {
        // last enquiry date
        private Date    lastEnquiryDate;
        // true if user enquired with last {enquiredWithinDays} number of days
        private boolean hasValidEnquiry = false;
        // number of days within which enquiry is done
        private int     enquiryValidityPeriod;

        public AlreadyEnquiredDetails(Date lastEnquiredOn, boolean enquiredWithinTimeLimit, int enquiredWithinDays) {
            super();
            this.lastEnquiryDate = lastEnquiredOn;
            this.hasValidEnquiry = enquiredWithinTimeLimit;
            this.enquiryValidityPeriod = enquiredWithinDays;
        }

        public Date getLastEnquiryDate() {
            return lastEnquiryDate;
        }

        public void setLastEnquiryDate(Date lastEnquiryDate) {
            this.lastEnquiryDate = lastEnquiryDate;
        }

        public boolean isHasValidEnquiry() {
            return hasValidEnquiry;
        }

        public void setHasValidEnquiry(boolean hasValidEnquiry) {
            this.hasValidEnquiry = hasValidEnquiry;
        }

        public int getEnquiryValidityPeriod() {
            return enquiryValidityPeriod;
        }

        public void setEnquiryValidityPeriod(int enquiryValidityPeriod) {
            this.enquiryValidityPeriod = enquiryValidityPeriod;
        }

    }
}
